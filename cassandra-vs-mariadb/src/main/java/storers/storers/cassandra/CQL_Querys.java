package storers.storers.cassandra;

/**
 * Created by dogle on 09/12/2016.
 */
public class CQL_Querys {

    public static String createKeySpace(String keyspaceName, int replication) {
        String query = "CREATE KEYSPACE IF NOT EXISTS " + keyspaceName + " WITH replication "
                + "= {'class':'SimpleStrategy', 'replication_factor':" + replication + "}; ";
        return query;
    }

    public static String dropKeySpace(String keyspaceName) {
        String query = "Drop KEYSPACE " + keyspaceName;
        return query;
    }

    public static String dropTable(String tableName) {
        String query = "DROP TABLE IF EXISTS " + tableName + ";";
        return query;
    }

    public static String createLineItemTable(String keyspaceName) {
        String query = "CREATE TABLE IF NOT EXISTS " + keyspaceName + ".lineItems"
                + "( lineItem_id text, "
                + "order_id text, "
                + "product_id text, "
                + "quantity int, "
                + "line_price double, "
                + "PRIMARY KEY (lineItem_id)"
                + ");";
        return query;
    }

    public static String createOrderTable(String keyspaceName) {
        String query = "CREATE TABLE IF NOT EXISTS " + keyspaceName + ".orders"
                + "( order_id text, "
                + "lineItem_ids list<text>, "
                + "client_id text, "
                + "date_created text, "
                + "status text, "
                + "order_subTotal double, "
                + "PRIMARY KEY (order_id)"
                + ");";
        return query;
    }

    public static String addLineItem(String keyspaceName) {
        String query = "INSERT INTO " + keyspaceName + ".lineItems" +
                "( lineItem_id, order_id, product_id, quantity, line_price ) " +
                "VALUES (?, ?, ?, ?, ?);";
        return query;
    }

    public static String addOrder(String keyspaceName) {
        String query = "INSERT INTO " + keyspaceName + ".orders"
                + "( order_id, lineItem_ids, client_id, date_created, status, order_subTotal ) "
                + "VALUES (?, ?, ?, ?, ?, ?) IF NOT EXISTS;";
        return query;
    }

    public static String updateLineItem(String keyspaceName) {
        return "UPDATE " + keyspaceName + ".lineItems SET quantity=?, line_price=? WHERE lineItem_id=?;";
    }

    public static String updateOrder(String keyspaceName) {
        return "UPDATE " + keyspaceName + ".orders SET date_created=?, status=?, order_subTotal=? WHERE order_id=? IF EXISTS;";
    }

    public static String selectAllLineItemIDs(String keyspaceName) {
        return "SELECT lineitem_ids FROM " + keyspaceName + ".orders WHERE order_id=?;";
    }

    public static String deleteLineItem(String keyspaceName) {
        return "DELETE FROM " + keyspaceName + ".lineItems WHERE lineItem_id=?;";
    }

    public static String deleteOrder(String keyspaceName) {
        return "DELETE FROM " + keyspaceName + ".orders WHERE order_id=? IF EXISTS;";
    }

    public static String updateOrderStatus(String keyspaceName) {
        return "UPDATE " + keyspaceName + ".orders SET status=? WHERE order_id=? IF EXISTS;";
    }

    public static String selectAllLineItems(String keyspaceName) {
        return "SELECT * FROM " + keyspaceName + ".lineItems WHERE order_id=?;";
    }

    public static String selectAllOrders(String keyspaceName) {
        return"SELECT * FROM " + keyspaceName + ".orders WHERE order_id=?;";
    }
}
