package storers.storers;

import com.datastax.driver.core.*;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.zaxxer.hikari.HikariDataSource;
import dataGenerator.data_handlers.Settings;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.combo.*;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.DBType;
import storers.storers.maria.enums.SQLQuery;

import java.sql.*;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class Combo implements Storer {
    private final static Logger LOG = Logger.getLogger(Combo.class);
    private static final List<HikariDataSource> hikariDataSources = new ArrayList<>();
    private CSVLogger logger;
    private Cluster cluster;
    private Session session;
    private String keyspaceName = "";
    private static final String cassandra_ip = Settings.getStringVmSetting("CASSANDRA_IP");
    private static final String[] mariaIps = Settings.getStringVmSetting("MARIA_IPS").split(",");
    private int mariaConnectionIndex = 0;
    private DBType type;
    private ExecutorService cachedPool = Executors.newCachedThreadPool();
    private int mariaConnectionPoolMax;
    private Map<DBEventType, PreparedStatement> orderPreparedStatements = new HashMap<>();
    private Map<DBEventType, PreparedStatement>  lineItemsPreparedStatements = new HashMap<>();
    private final ConsistencyLevel consistencyLevel;
    private final int replicationNumber;

    public Combo(CSVLogger logger, DBType type, int mariaConnectionPoolMax) {
        this(logger, type, mariaConnectionPoolMax, ConsistencyLevel.ONE, 1);
    }

    public Combo(CSVLogger logger, DBType type) {
        this(logger, type, 1);
    }

    public Combo(CSVLogger logger, DBType type, int mariaConnectionPoolMax, ConsistencyLevel consistencyLevel, int replicationNumber) {
        this.type = type;
        this.logger = logger;
        this.mariaConnectionPoolMax = mariaConnectionPoolMax;
        this.consistencyLevel = consistencyLevel;
        this.replicationNumber = replicationNumber;
        if (DBType.CASSANDRA.equals(type)) {
            initCassandraInstance();
        } else if (DBType.MARIA_DB.equals(type)) {
            try {
                initMariaDBInstance();
            } catch (SQLException e) {
                LOG.warn("Failed to initialise database connections", e);
            }
        } else {
            throw new IllegalArgumentException("Database type must be provided");
        }
    }

    public void messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));

        Order order;

        try {
            order = new Order((JSONObject) message.get("data"));
        } catch (ClassCastException cce) {
            order = new Order((String) message.get("data"));
        }

        PreparedStatement orderPreparedStatement = orderPreparedStatements.get(eventType);
        PreparedStatement lineItemPreparedStatement = lineItemsPreparedStatements.get(eventType);
        try {
            switch (eventType) {
                case CREATE:
                    this.cachedPool.submit(new Create(session, orderPreparedStatement, lineItemPreparedStatement, getConnection(), logger, order, this.type));
                    break;
                case UPDATE:
                    this.cachedPool.submit(new Update(session, orderPreparedStatement, lineItemPreparedStatement, getConnection(), logger, order, this.type));
                    break;
                case UPDATE_STATUS:
                    this.cachedPool.submit(new UpdateStatus(session, orderPreparedStatement, lineItemPreparedStatement, getConnection(), logger, order, this.type));
                    break;
                case DELETE:
                    this.cachedPool.submit(new Delete(session, orderPreparedStatement, lineItemPreparedStatement, getConnection(), logger, order, this.type));
                    break;
                case READ:
                    this.cachedPool.submit(new Read(session, orderPreparedStatement, lineItemPreparedStatement, getConnection(), logger, order, this.type));
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            LOG.warn("Failed to apply event.", e);
        }
    }

    private void initMariaDBInstance() throws SQLException {
        for(String ip: mariaIps) {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setMaximumPoolSize(mariaConnectionPoolMax); // CPU Cores!
            hikariDataSource.setDriverClassName("org.mariadb.jdbc.Driver");
            hikariDataSource.setJdbcUrl(SQLQuery.CONNECTION_STRING.getQuery(ip));
            hikariDataSource.setAutoCommit(false);
            hikariDataSources.add(hikariDataSource);
        }
        Connection connection = getConnection();
        doSingleMariaQuery(connection, SQLQuery.DROP_ORDERS_DB);
        doSingleMariaQuery(connection, SQLQuery.CREATE_ORDERS_DB);
        LOG.info("Created DB");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        doSingleMariaQuery(connection, SQLQuery.CREATE_ORDER_TABLE);
        LOG.info("Created one table");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        doSingleMariaQuery(connection, SQLQuery.CREATE_LINE_ITEM_TABLE);
        LOG.info("Created twp tables");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        connection.commit();
        connection.close();
    }

    /**
     * Returns the next maria connection from the list to provide round robin load balancing.
     */
    private Connection getConnection() throws SQLException {
        if (hikariDataSources.isEmpty()) {
            // Return null if this is a Cassandra connection.
            return null;
        }

        if (mariaConnectionIndex + 1 == hikariDataSources.size()) {
            mariaConnectionIndex = 0;
        } else {
            mariaConnectionIndex++;
        }
        return hikariDataSources.get(mariaConnectionIndex).getConnection();
    }

    private void doSingleMariaQuery(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        statement.close();
    }

    private void doSingleMariaQueryAndLog(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        java.sql.ResultSet results = statement.getResultSet();

        while ((results != null) && results.next()) {
            LOG.info("TABLE ROW: " + results.getString("Tables_in_orders"));
        }

        statement.close();
    }

    // Cassandra Setup
    private void initCassandraInstance() {
        this.cluster = Cluster.builder().addContactPoint(cassandra_ip).withLoadBalancingPolicy(new RoundRobinPolicy()).withQueryOptions(new QueryOptions().setConsistencyLevel(consistencyLevel)).build();
        cassandraConnect();
        createKeySpace("orders");
        createLineItemTable();
        createOrderTable();
        createPreparedStatements();
    }

    private void createPreparedStatements() {
        orderPreparedStatements.put(DBEventType.CREATE, session.prepare(CQL_Querys.addOrder(keyspaceName)));
        orderPreparedStatements.put(DBEventType.UPDATE, session.prepare(CQL_Querys.updateOrder(keyspaceName)));
        orderPreparedStatements.put(DBEventType.UPDATE_STATUS, session.prepare(CQL_Querys.updateOrderStatus(keyspaceName)));
        orderPreparedStatements.put(DBEventType.DELETE, session.prepare(CQL_Querys.deleteOrder(keyspaceName)));
        orderPreparedStatements.put(DBEventType.READ, session.prepare(CQL_Querys.selectAllOrders(keyspaceName)));

        lineItemsPreparedStatements.put(DBEventType.CREATE, session.prepare(CQL_Querys.addLineItem(keyspaceName)));
        lineItemsPreparedStatements.put(DBEventType.UPDATE, session.prepare(CQL_Querys.updateLineItem(keyspaceName)));
        lineItemsPreparedStatements.put(DBEventType.DELETE, session.prepare(CQL_Querys.deleteLineItem(keyspaceName)));
        lineItemsPreparedStatements.put(DBEventType.READ, session.prepare(CQL_Querys.selectAllLineItems(keyspaceName)));
    }

    private void cassandraConnect() {
        this.session = this.cluster.connect();
    }

    private void createKeySpace(String name) {
        dropKeySpace(name);
        this.session.execute(CQL_Querys.createKeySpace(name, replicationNumber));
        this.session.execute("USE " + name);
        this.keyspaceName = name;
    }

    private void dropKeySpace(String name) {
        try {
            this.session.execute(CQL_Querys.dropKeySpace(name));
        } catch (RuntimeException e) {
            LOG.warn("Failed to drop key space.", e);
        }
    }

    private void createLineItemTable() {
        this.session.execute(CQL_Querys.dropTable("lineItems_by_orderId"));
        this.session.execute(CQL_Querys.createLineItemTable(this.keyspaceName));
    }

    private void createOrderTable() {
        this.session.execute(CQL_Querys.dropTable("orders"));
        this.session.execute(CQL_Querys.createOrderTable(this.keyspaceName));
    }

    public void setLogger(CSVLogger logger) {
        this.logger = logger;
    }

    public void reinitThreadPool(){
        this.cachedPool = Executors.newCachedThreadPool();
    }

    public void shutdown() {
        cachedPool.shutdown();
        try {
            cachedPool.awaitTermination(60, TimeUnit.SECONDS);
        }catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            cachedPool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
