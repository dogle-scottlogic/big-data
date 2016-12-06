package storers.MariaDB;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public class RunnableOrderUpdater implements Runnable {
    private static final String ACTION_TYPE = "UPDATE";

    private Thread thread;
    private JSONObject data;
    private String orderId;
    private Connection connection;

    public RunnableOrderUpdater(Connection connection, JSONObject data) {
        this.data = data;
        this.connection = connection;
        this.orderId = (String) data.get("id");
    }


    public void start() {
        thread = new Thread(this, ACTION_TYPE + ":" + orderId);
        thread.start();
    }

    public void run() {
        JSONObject client = (JSONObject) data.get("client");
        String clientId = (String) client.get("id");
        Long date = (Long) data.get("date");
        doQuery("UPDATE orders.`order` " +
            "SET " +
                "client_id='" + clientId + "', " +
                "created='" + Long.valueOf(date).toString() + "' " +
            "WHERE id='" + orderId + "';");
        JSONArray lineItems = (JSONArray) data.get("lineItems");
        updateLineItems(orderId, lineItems);
    }

    private void updateLineItems(String orderId, JSONArray lineItems) {
        Iterator lineItemsIterator = lineItems.iterator();

        while (lineItemsIterator.hasNext()) {
            JSONObject nextLineItem = (JSONObject) lineItemsIterator.next();
            JSONObject product = (JSONObject) nextLineItem.get("product");
            String productId = (String) product.get("id");
            Long quantity = (Long) nextLineItem.get("quantity");

            doQuery("UPDATE orders.line_item " +
                "SET " +
                    "order_id='" + orderId + "', " +
                    "product_id='" + productId + "', " +
                    "quantity=" + Long.valueOf(quantity).toString() + " " +
                "WHERE order_id='" + orderId + "';");
        }
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
}
