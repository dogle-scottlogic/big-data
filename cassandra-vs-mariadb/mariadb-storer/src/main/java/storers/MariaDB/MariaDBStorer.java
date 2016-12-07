package storers.MariaDB;

import org.json.simple.JSONObject;
import storers.MariaDB.enums.DBEventType;
import storers.MariaDB.enums.SQLQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MariaDB Storer.
 * Takes as input events from The Hat Shop as JSON data and as appropriate Creates, Updates and Deletes Order data.
 */
public class MariaDBStorer {
    private static Connection connection;

    public MariaDBStorer() throws SQLException{
        initialise();
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

    private static boolean doQuery(SQLQuery query) throws SQLException {
        Statement statement = connection.createStatement();
        boolean hasSucceeded = statement.execute(query.getQuery());
        statement.close();
        return hasSucceeded;
    }

    public static void messageHandler(JSONObject message) {
        DBEventType eventType = DBEventType.valueOf((String) message.get("type"));
        switch (eventType) {
            case CREATE:
                new RunnableOrderCreator(connection, (JSONObject) message.get("data")).start();
                break;
            case UPDATE:
                new RunnableOrderUpdater(connection, (JSONObject) message.get("data")).start();
                break;
            case DELETE:
                new RunnableOrderDeleter(connection, (String) message.get("data")).start();
                break;
        }
    }
}
