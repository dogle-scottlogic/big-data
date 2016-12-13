package storers.storers;

import com.zaxxer.hikari.HikariDataSource;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.maria.*;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.SQLQuery;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * maria Storer.
 * Takes as input events from The Hat Shop as JSON data and as appropriate Creates, Updates and Deletes Order data.
 */
public class MariaDBStorer implements Storer{
    private static Connection synchronisedConnection;
    private static final HikariDataSource hikariDataSource = new HikariDataSource();
    private static CSVLogger csvLogger;

    private boolean useASync;

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
            this.synchronisedConnection.close();
        }
    }

    private static void initialise() throws SQLException {
        synchronisedConnection = DriverManager.getConnection(SQLQuery.CONNECTION_STRING.getQuery());
        synchronisedConnection.setAutoCommit(false);
        initTables(synchronisedConnection);
        synchronisedConnection = DriverManager.getConnection(SQLQuery.CONNECTION_STRING.getQuery());
        synchronisedConnection.setAutoCommit(false);
    }

    private static void initialiseASync() throws SQLException {
        hikariDataSource.setMaximumPoolSize(100);
        hikariDataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariDataSource.setJdbcUrl(SQLQuery.CONNECTION_STRING.getQuery());
        hikariDataSource.setAutoCommit(false);
        initTables(hikariDataSource.getConnection());
    }

    private static void initTables(Connection queryConnection) throws SQLException {
        doQuery(queryConnection, SQLQuery.DROP_ORDERS_DB);
        doQuery(queryConnection, SQLQuery.CREATE_ORDERS_DB);
        doQuery(queryConnection, SQLQuery.CREATE_ORDER_TABLE);
        doQuery(queryConnection, SQLQuery.CREATE_LINE_ITEM_TABLE);
        queryConnection.commit();
        queryConnection.close();
    }

    private static void doQuery(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        statement.close();
    }

    public void messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));
        Connection queryConnection = null;
        try {
            if (useASync) {
                queryConnection = hikariDataSource.getConnection();
            } else {
                queryConnection = synchronisedConnection;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
}
