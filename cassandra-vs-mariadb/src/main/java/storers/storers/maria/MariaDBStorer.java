package storers.storers.maria;

import org.json.simple.JSONObject;
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
public class MariaDBStorer {
    private static Connection connection;

    public MariaDBStorer() throws SQLException {
        initialise();
    }

    public void end() throws SQLException {
        this.connection.close();
    }

    private static void initialise() throws SQLException {
        connection = DriverManager.getConnection(SQLQuery.CONNECTION_STRING.getQuery());
        connection.setAutoCommit(false);
        doQuery(SQLQuery.DROP_ORDERS_DB);
        doQuery(SQLQuery.CREATE_ORDERS_DB);
        doQuery(SQLQuery.CREATE_ORDER_TABLE);
        doQuery(SQLQuery.CREATE_LINE_ITEM_TABLE);
        connection.commit();
    }

    private static void doQuery(SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(query.getQuery());
        statement.close();
    }

    public static String[] messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));

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
