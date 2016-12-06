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
public class RunnableOrderCreator implements Runnable {
    private static final String ACTION_TYPE = "CREATE";

    private Thread thread;
    private Connection connection;
    private String orderId;
    private JSONObject data;

    public RunnableOrderCreator(Connection connection, JSONObject data){
        this.connection = connection;
        this.data = data;
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
        doQuery("INSERT INTO orders.`order` VALUES('" + orderId + "', '" + clientId + "', '" + Long.valueOf(date).toString() + "');");
        JSONArray lineItems = (JSONArray) data.get("lineItems");
        createLineItems(orderId, lineItems);
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

    private void createLineItems(String orderId, JSONArray lineItems) {
        Iterator lineItemsIterator = lineItems.iterator();
        String query = "INSERT INTO orders.line_item(order_id, product_id, quantity) VALUES";

        while (lineItemsIterator.hasNext()) {
            JSONObject nextLineItem = (JSONObject) lineItemsIterator.next();
            query = query.concat(createLineItemPartialQuery(orderId, nextLineItem)).concat(", ");
        }
        // Chop last comma
        query = query.substring(0, query.length() -2);
        doQuery(query);
    }

    private String createLineItemPartialQuery(String orderId, JSONObject lineItem) {
        JSONObject product = (JSONObject) lineItem.get("product");
        String productId = (String) product.get("id");
        Long quantity = (Long) lineItem.get("quantity");
        return "('" + orderId + "', '" + productId + "', " + Long.valueOf(quantity).toString() + ")";
    }
}