package storers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Created by lcollingwood on 05/12/2016.
 */

public class MariaDBStorer {

    private static Connection connection;

    public MariaDBStorer() {
        try {
            initialise();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initialise() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/orders?user=root&password=root");
        initialiseOrderTable();
        initialiseLineItemTable();
    }

    private static void initialiseOrderTable() throws SQLException {
        Statement s = connection.createStatement();
        String dropTable = "DROP TABLE IF EXISTS `order`";
        String createTable = "CREATE TABLE `order` (" +
                "id VARCHAR(40) NOT NULL, " +
                "client_id VARCHAR(40) NOT NULL, " +
                "created VARCHAR(40) NOT NULL, " +
                "PRIMARY KEY (id)" +
                ")";

        s.execute(dropTable);
        s.execute(createTable);
        s.close();
    }

    private static void initialiseLineItemTable() throws SQLException {
        Statement s = connection.createStatement();
        String dropTable = "DROP TABLE IF EXISTS `line_item` CASCADE";
        String createTable = "CREATE TABLE `line_item` (" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "order_id VARCHAR(40) NOT NULL, " +
                "product_id VARCHAR(40) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "PRIMARY KEY(id)" + //, " +
//                "FOREIGN KEY(order_id) REFERENCES order(id)" +
                ")";

        s.execute(dropTable);
        s.execute(createTable);
        s.close();
    }

    public static void messageHandler(JSONObject message) {
        String type = (String) message.get("type");

        if (type.equals("UPDATE")) {
            JSONObject data = (JSONObject) message.get("data");
            update(data);
        }

        if (type.equals("CREATE")) {
            JSONObject data = (JSONObject) message.get("data");
            JSONArray lineItems = (JSONArray) data.get("lineItems");
            String orderId = (String) data.get("id");

            createOrder(data);
            createLineItems(orderId, lineItems);
        }

        if (type.equals("DELETE")) {
            String id = (String) message.get("data");
            delete(id);
        }
    }

    private static void createOrder(JSONObject data) {
        JSONObject client = (JSONObject) data.get("client");

        String orderId = (String) data.get("id");
        String clientId = (String) client.get("id");
        Long date = (Long) data.get("date");

        String query = "INSERT INTO `order` VALUES('" + orderId + "', '" + clientId + "', '" + Long.valueOf(date).toString() + "');";

        System.out.println(query);

        try {
            Statement s = connection.createStatement();
            s.execute(query);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createLineItems(String orderId, JSONArray lineItems) {
        Iterator lineItemsIterator = lineItems.iterator();
        while (lineItemsIterator.hasNext()) {
            JSONObject nextLineItem = (JSONObject) lineItemsIterator.next();
            createLineItem(orderId, nextLineItem);
        }
    }

    private static void createLineItem(String orderId, JSONObject lineItem) {
        JSONObject product = (JSONObject) lineItem.get("product");

        String productId = (String) product.get("id");
        Long quantity = (Long) lineItem.get("quantity");

        String query = "INSERT INTO line_item(order_id, product_id, quantity) VALUES('" + orderId + "', '" + productId + "', '" + Long.valueOf(quantity).toString() + "');";
        System.out.println("--> " + query);

        try {
            Statement s = connection.createStatement();
            s.execute(query);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void update(JSONObject data) {
//        JSONObject client = (JSONObject) data.get("client");
//
//        String orderId = (String) data.get("id");
//        String clientId = (String) client.get("id");
//        String created = (String) data.get("created");
//
//        String query = "INSERT INTO test VALUES('" + orderId + "', '" + clientId + "', '" + created + "');";
//
//        System.out.println(query);
//
//        try {
//            Statement s = connection.createStatement();
//            s.execute(query);
//            connection.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    private static void delete(String id) {
//        System.out.println("delete From Switch");
    }

}
