package storers.MariaDB;

import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public class RunnableOrderDeleter implements Runnable {
    private static final String ACTION_TYPE = "DELETE";

    private Thread thread;
    private Connection connection;
    private String orderId;


    public RunnableOrderDeleter(Connection connection, String orderId) {
        this.orderId = orderId;
        this.connection = connection;
    }

    public void start() {
        thread = new Thread(this, ACTION_TYPE + ":" + orderId);
        thread.start();
    }

    public void run() {
        deleteLineItemsByOrderId(orderId);
        deleteOrder(orderId);
    }

    private void doQuery(String query) {
        try {
            Statement s = connection.createStatement();
            s.execute(query);
            System.out.println(query);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder(String orderId) {
        String query = "DELETE FROM orders.`order` WHERE id='" + orderId + "';";
        doQuery("DELETE FROM orders.`order` WHERE id='" + orderId + "';");
    }

    private void deleteLineItemsByOrderId(String orderId) {
        String query = "DELETE FROM orders.`line_item` WHERE order_id='" + orderId + "';";
        doQuery("DELETE FROM orders.`line_item` WHERE order_id='" + orderId + "';");
    }
}
