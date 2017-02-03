package storers.storers.maria.enums;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public enum SQLQuery {
    CONNECTION_STRING("jdbc:mariadb://${0}:3306/?user=root&password=myfirstpassword"),

    DROP_ORDERS_DB("DROP DATABASE IF EXISTS `orders`"),

    CREATE_ORDERS_DB("CREATE DATABASE `orders`"),

    SHOW_TABLES("SHOW TABLES IN `orders`"),

    CREATE_ORDER_TABLE(
        "CREATE TABLE orders.`order`(" +
            "id VARCHAR(40) NOT NULL, " +
            "client_id VARCHAR(40) NOT NULL, " +
            "created VARCHAR(40) NOT NULL, " +
            "status VARCHAR(40) NOT NULL, " +
            "PRIMARY KEY (id)) ENGINE=NDBCLUSTER"
    ),

    CREATE_LINE_ITEM_TABLE(
        "CREATE TABLE orders.`line_item` (" +
            "id VARCHAR(40) NOT NULL, " +
            "order_id VARCHAR(40) NOT NULL, " +
            "product_id VARCHAR(40) NOT NULL, " +
            "quantity INT NOT NULL, " +
        "PRIMARY KEY(id, order_id), FOREIGN KEY(order_id) REFERENCES orders.order(id)" + ")" +
            "ENGINE=NDBCLUSTER PARTITION BY KEY(order_id)"
    );

    private String query;

    /**
     * Replaces ${int} in the SQL query with the value at the same index in the provided parameters.
     */
    public String getQuery(String... parameters) {
        String subsitutedQuery = query;
        for (int i = 0; i < parameters.length; i++) {
            subsitutedQuery = subsitutedQuery.replace("${"+ i +"}", parameters[i]);
        }
        return subsitutedQuery;
    }

    SQLQuery(String query) {
        this.query = query;
    }
}
