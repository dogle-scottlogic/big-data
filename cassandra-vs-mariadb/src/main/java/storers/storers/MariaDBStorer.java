package storers.storers;

import com.zaxxer.hikari.HikariDataSource;
import dataGenerator.data_handlers.Settings;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.maria.*;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.SQLQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * maria Storer.
 * Takes as input events from The Hat Shop as JSON data and as appropriate Creates, Updates and Deletes Order data.
 */
public class MariaDBStorer implements Storer {
    private final static Logger LOG = Logger.getLogger(MariaDBStorer.class);
    private static final List<Connection> synchronisedConnections = new ArrayList<>();
    private static final List<HikariDataSource> hikariDataSources = new ArrayList<>();
    private static final String[] mariaIps = Settings.getStringVmSetting("MARIA_IPS").split(",");
    private CSVLogger csvLogger;

    private boolean useASync;
    private static int mariaConnectionIndex = 0;

    public MariaDBStorer(boolean useASync, CSVLogger csvLogger) throws SQLException {
        this.csvLogger = csvLogger;
        this.useASync = useASync;
        if (useASync) {
            initialiseASync();
        } else {
            initialise();
        }
    }

    public void end() throws SQLException {
        if (!useASync) {
            for (Connection synchronisedConnection: synchronisedConnections) {
                synchronisedConnection.close();
            }
        }
    }

    private static void initialise() throws SQLException {

        for(String ip: mariaIps) {
            Connection synchronisedConnection = DriverManager.getConnection(SQLQuery.CONNECTION_STRING.getQuery(ip));
            synchronisedConnection.setAutoCommit(false);
            synchronisedConnections.add(synchronisedConnection);
        }
        initTables(getSynchronisedConnection());
    }

    private static void initialiseASync() throws SQLException {
        for(String ip: mariaIps) {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setMaximumPoolSize(100);
            hikariDataSource.setDriverClassName("org.mariadb.jdbc.Driver");
            hikariDataSource.setJdbcUrl(SQLQuery.CONNECTION_STRING.getQuery(ip));
            hikariDataSource.setAutoCommit(false);
            hikariDataSources.add(hikariDataSource);
        }
        initTables(getConnection());
    }

    /**
     * Returns the next maria connection from the list to provide round robin load balancing.
     */
    private static Connection getSynchronisedConnection() throws SQLException {
        if (mariaConnectionIndex + 1 == synchronisedConnections.size()) {
            mariaConnectionIndex = 0;
        } else {
            mariaConnectionIndex++;
        }
        return synchronisedConnections.get(mariaConnectionIndex);
    }

    private static Connection getConnection() throws SQLException {
        if (mariaConnectionIndex + 1 == hikariDataSources.size()) {
            mariaConnectionIndex = 0;
        } else {
            mariaConnectionIndex++;
        }
        return hikariDataSources.get(mariaConnectionIndex).getConnection();
    }

    private static void initTables(Connection queryConnection) throws SQLException {
        Connection connection = queryConnection;
        doSingleMariaQuery(connection, SQLQuery.DROP_ORDERS_DB);
        LOG.info("Just created DB");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        doSingleMariaQuery(connection, SQLQuery.CREATE_ORDERS_DB);
        LOG.info("Created one table");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        doSingleMariaQuery(connection, SQLQuery.CREATE_ORDER_TABLE);
        LOG.info("Created two tables");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        doSingleMariaQuery(connection, SQLQuery.CREATE_LINE_ITEM_TABLE);
        LOG.info("Created three tables");
        doSingleMariaQueryAndLog(connection, SQLQuery.SHOW_TABLES);
        queryConnection.commit();
        queryConnection.close();
    }

    private static void doQuery(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        statement.close();
    }

    private static void doSingleMariaQuery(Connection connection, SQLQuery query) throws SQLException {
        doQuery(connection, query);
    }

    private static void doSingleMariaQueryAndLog(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        java.sql.ResultSet results = statement.getResultSet();

        while (results.next()) {
            LOG.info("TABLE ROW: " + results.getString("Tables_in_orders"));
        }

        statement.close();
    }

    public void messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));
        Connection queryConnection = null;
        try {
            if (useASync) {
                queryConnection = getConnection();
            } else {
                queryConnection = getSynchronisedConnection();
            }
        } catch (SQLException e) {
            LOG.warn("Failed to handle message", e);
        }

        switch (eventType) {
            case CREATE:
                new OrderCreateEvent(useASync, queryConnection, (JSONObject) message.get("data"), csvLogger).start();
                break;
            case UPDATE:
                new OrderUpdateEvent(useASync, queryConnection, (JSONObject) message.get("data"), csvLogger).start();
                break;
            case UPDATE_STATUS:
                new OrderStatusUpdateEvent(useASync, queryConnection, (JSONObject) message.get("data"), csvLogger).start();
                break;
            case DELETE:
                new OrderDeleteEvent(useASync, queryConnection, (String) message.get("data"), csvLogger).start();
                break;
            case READ:
                new OrderReadByIdEvent(useASync, queryConnection, (String) message.get("data"), csvLogger).start();
                break;
            default:
                break;
        }
    }

    public void shutdown() {
    }

    public void setLogger(CSVLogger logger) {
        this.csvLogger = logger;
    }
}
