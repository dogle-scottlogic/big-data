package storers.MariaDB.enums;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public enum SQLQuery {
    CONNECTION_STRING("jdbc:mariadb://localhost:3306/?user=root&password=root"),

    DROP_ORDERS_DB("DROP DATABASE IF EXISTS `orders`"),

    CREATE_ORDERS_DB("CREATE DATABASE `orders`"),

    CREATE_ORDER_TABLE(
        "CREATE TABLE orders.`order`(" +
            "id VARCHAR(40) NOT NULL, " +
            "client_id VARCHAR(40) NOT NULL, " +
            "created VARCHAR(40) NOT NULL, " +
        "PRIMARY KEY (id))"
    ),

    CREATE_LINE_ITEM_TABLE(
        "CREATE TABLE orders.`line_item` (" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "order_id VARCHAR(40) NOT NULL, " +
            "product_id VARCHAR(40) NOT NULL, " +
            "quantity INT NOT NULL, " +
        "PRIMARY KEY(id), FOREIGN KEY(order_id) REFERENCES orders.order(id)" + ")"
    );

    private String query;

    public String getQuery() {
        return query;
    }

    SQLQuery(String query) {
        this.query = query;
    }
}
