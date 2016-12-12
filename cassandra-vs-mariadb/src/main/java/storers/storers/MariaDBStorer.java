package storers.storers;

import Conveyor.Conveyor;
import com.zaxxer.hikari.HikariDataSource;
import org.json.simple.JSONObject;
import storers.storers.maria.*;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.SQLQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * maria Storer.
 * Takes as input events from The Hat Shop as JSON data and as appropriate Creates, Updates and Deletes Order data.
 */
public class MariaDBStorer implements Storer{
//    private static Connection connection;
    private static final HikariDataSource hikariDataSource = new HikariDataSource();


    public MariaDBStorer() throws SQLException {
        initialise();
    }

    public void end() throws SQLException {
//        this.connection.close();
    }

    private static void initialise() throws SQLException {

        hikariDataSource.setMaximumPoolSize(100);
        hikariDataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariDataSource.setJdbcUrl(SQLQuery.CONNECTION_STRING.getQuery());
        hikariDataSource.setAutoCommit(false);

//        connection = DriverManager.getConnection(SQLQuery.CONNECTION_STRING.getQuery());
//        connection.setAutoCommit(false);

        Connection connection = hikariDataSource.getConnection();
        doQuery(connection, SQLQuery.DROP_ORDERS_DB);
        doQuery(connection, SQLQuery.CREATE_ORDERS_DB);
        doQuery(connection, SQLQuery.CREATE_ORDER_TABLE);
        doQuery(connection, SQLQuery.CREATE_LINE_ITEM_TABLE);
        connection.commit();
    }

    private static void doQuery(Connection connection, SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        statement.close();
    }

    public String[] messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));

        Connection connection = null;
        try {
            connection = hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (eventType) {
            case CREATE:
                return new OrderCreateEvent(connection, (JSONObject) message.get("data")).start();
            case UPDATE:
                return new OrderUpdateEvent(connection, (JSONObject) message.get("data")).start();
            case UPDATE_STATUS:
                return new OrderStatusUpdateEvent(connection, (JSONObject) message.get("data")).start();
            case DELETE:
                return new OrderDeleteEvent(connection, (String) message.get("data")).start();
            case READ:
                return new OrderReadByIdEvent(connection, (String) message.get("data")).start();
            default:
                return new String[]{};
        }
    }
}
