package storers.MariaDB;

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
        connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/?user=root&password=root");
        connection.setAutoCommit(false);
        initialiseOrdersDB();
        initialiseOrderTable();
        initialiseLineItemTable();
        connection.commit();
    }

    private static void initialiseOrdersDB() throws SQLException {
        Statement s = connection.createStatement();
        String dropDB = "DROP DATABASE IF EXISTS `orders`";
        String createDB = "CREATE DATABASE `orders`";
        s.execute(dropDB);
        s.execute(createDB);
        s.close();
    }

    private static void initialiseOrderTable() throws SQLException {
        Statement s = connection.createStatement();
        String createTable =
            "CREATE TABLE orders.`order` (" +
            "id VARCHAR(40) NOT NULL, " +
            "client_id VARCHAR(40) NOT NULL, " +
            "created VARCHAR(40) NOT NULL, " +
            "PRIMARY KEY (id)" + ")";
        s.execute(createTable);
        s.close();
    }

    private static void initialiseLineItemTable() throws SQLException {
        Statement s = connection.createStatement();
        String createTable =
            "CREATE TABLE orders.`line_item` (" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "order_id VARCHAR(40) NOT NULL, " +
            "product_id VARCHAR(40) NOT NULL, " +
            "quantity INT NOT NULL, " +
            "PRIMARY KEY(id), " +
            "FOREIGN KEY(order_id) REFERENCES orders.order(id)" + ")";
        s.execute(createTable);
        s.close();
    }

    public static void messageHandler(JSONObject message) {
        String type = (String) message.get("type");

        if (type.equals("UPDATE")) {
            RunnableOrderUpdater runnableOrderUpdater = new RunnableOrderUpdater(connection, (JSONObject) message.get("data"));
            runnableOrderUpdater.start();
        }

        if (type.equals("CREATE")) {
            RunnableOrderCreator runnableOrderCreator = new RunnableOrderCreator(connection, (JSONObject) message.get("data"));
            runnableOrderCreator.start();
        }

        if (type.equals("DELETE")) {
            RunnableOrderDeleter runnableOrderDeleter = new RunnableOrderDeleter(connection, (String) message.get("data"));
            runnableOrderDeleter.start();
        }
    }
}
