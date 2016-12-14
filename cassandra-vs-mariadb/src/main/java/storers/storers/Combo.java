package storers.storers;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.combo.Create;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.SQLQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class Combo implements Storer {

    private Connection mariaConnection;
    private CSVLogger logger;
    private Cluster cluster;
    private Session session;
    private String keyspaceName = "";
    private String host = "127.0.0.7";
    private ExecutorService cachedPool = Executors.newCachedThreadPool();
    // private final boolean readEventHappened[] = {false};

    public Combo(CSVLogger logger) {
        this.logger = logger;
        try {
            initMariaDBInstance();
            initCassandraInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));


        switch (eventType) {
            case CREATE:
                try {
                    this.cachedPool.submit(new Create(session, mariaConnection, logger, (JSONObject) message.get("data")));
//                    new Thread(
//                            new Create(session, mariaConnection, logger, (JSONObject) message.get("data"))
//                    ).start();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case UPDATE:
//                new OrderUpdateEvent(useASync, queryConnection, (JSONObject) message.get("data"), csvLogger).start();
                break;
            case UPDATE_STATUS:
//                new OrderStatusUpdateEvent(useASync, queryConnection, (JSONObject) message.get("data"), csvLogger).start();
                break;
            case DELETE:
//                new OrderDeleteEvent(useASync, queryConnection, (String) message.get("data"), csvLogger).start();
                break;
            case READ:
//                new OrderReadByIdEvent(useASync, queryConnection, (String) message.get("data"), csvLogger).start();
                break;
            default:
                break;
        }
    }

    // Maria Setup
    public void closeMariaConnection() throws SQLException {
        mariaConnection.commit();
        mariaConnection.close();
    }

    private void initMariaDBInstance() throws SQLException {
        mariaConnection = DriverManager.getConnection(SQLQuery.CONNECTION_STRING.getQuery());
        mariaConnection.setAutoCommit(false);
        doSingleMariaQuery(mariaConnection, SQLQuery.DROP_ORDERS_DB);
        doSingleMariaQuery(mariaConnection, SQLQuery.CREATE_ORDERS_DB);
        doSingleMariaQuery(mariaConnection, SQLQuery.CREATE_ORDER_TABLE);
        doSingleMariaQuery(mariaConnection, SQLQuery.CREATE_LINE_ITEM_TABLE);
        mariaConnection.commit();
    }

    private void doSingleMariaQuery(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        statement.close();
    }

    // Cassandra Setup
    private void initCassandraInstance() {
        this.cluster = Cluster.builder().addContactPoint(this.host).withLoadBalancingPolicy(new RoundRobinPolicy()).build();
        cassandraConnect();
        createKeySpace("orders");
        createLineItemTable();
        createOrderTable();
    }

    public void cassandraConnect() {
        this.session = this.cluster.connect();
    }

    public void createKeySpace(String name) {
        dropKeySpace(name);
        this.session.execute(CQL_Querys.createKeySpace(name, 1));
        this.session.execute("USE " + name);
        this.keyspaceName = name;
    }

    public void dropKeySpace(String name) {
        try {
            this.session.execute(CQL_Querys.dropKeySpace(name));
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
    }

    public void createLineItemTable() {
        this.session.execute(CQL_Querys.dropTable("lineItems_by_orderId"));
        this.session.execute(CQL_Querys.createLineItemTable(this.keyspaceName));
    }

    public void createOrderTable() {
        this.session.execute(CQL_Querys.dropTable("orders"));
        this.session.execute(CQL_Querys.createOrderTable(this.keyspaceName));

    }
}
