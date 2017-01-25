package storers.storers.cassandra;

/**
 * Created by dogle on 09/12/2016.
 */
public class CQL_Querys {

    public static String createKeySpace(String keyspaceName, int replication) {
        return "CREATE KEYSPACE IF NOT EXISTS " + keyspaceName + " WITH replication "
                + "= {'class':'SimpleStrategy', 'replication_factor':" + replication + "}; ";
    }

    public static String dropKeySpace(String keyspaceName) {
        return "Drop KEYSPACE " + keyspaceName;
    }

    public static String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName + ";";
    }

    public static String createLineItemTable(String keyspaceName) {
        return "CREATE TABLE IF NOT EXISTS " + keyspaceName + ".lineItems_by_orderId"
                + "( order_id text, "
                + "lineItem_id text, "
                + "product_id text, "
                + "quantity int, "
                + "line_price double, "
                + "PRIMARY KEY (order_id, lineItem_id)"
                + ");";
    }

    public static String createOrderTable(String keyspaceName) {
        return "CREATE TABLE IF NOT EXISTS " + keyspaceName + ".orders"
                + "( order_id text, "
                + "lineItem_ids list<text>, "
                + "client_id text, "
                + "date_created text, "
                + "status text, "
                + "order_subTotal double, "
                + "PRIMARY KEY (order_id)"
                + ");";
    }

    public static String addLineItem(String keyspaceName) {
        return "INSERT INTO " + keyspaceName + ".lineItems_by_orderId" +
                "( order_id, lineItem_id, product_id, quantity, line_price ) " +
                "VALUES (?, ?, ?, ?, ?);";
    }

    public static String addOrder(String keyspaceName) {
        return "INSERT INTO " + keyspaceName + ".orders"
                + "( order_id, lineItem_ids, client_id, date_created, status, order_subTotal ) "
                + "VALUES (?, ?, ?, ?, ?, ?);";
    }

    public static String updateLineItem(String keyspaceName) {
        return "UPDATE " + keyspaceName + ".lineItems_by_orderId SET quantity=?, line_price=? WHERE lineItem_id=? AND order_id=?;";
    }

    public static String updateOrder(String keyspaceName) {
        return "UPDATE " + keyspaceName + ".orders SET date_created=?, status=?, order_subTotal=? WHERE order_id=?;";
    }

    public static String selectAllLineItemIDs(String keyspaceName) {
        return "SELECT lineitem_ids FROM " + keyspaceName + ".orders WHERE order_id=?;";
    }

    public static String deleteLineItem(String keyspaceName) {
        return "DELETE FROM " + keyspaceName + ".lineItems_by_orderId WHERE order_id=?;";
    }

    public static String deleteOrder(String keyspaceName) {
        return "DELETE FROM " + keyspaceName + ".orders WHERE order_id=?;";
    }

    public static String updateOrderStatus(String keyspaceName) {
        return "UPDATE " + keyspaceName + ".orders SET status=? WHERE order_id=? IF EXISTS;";
    }

    public static String selectAllLineItems(String keyspaceName) {
        return "SELECT * FROM " + keyspaceName + ".lineItems_by_orderId WHERE order_id=?;";
    }

    public static String selectAllOrders(String keyspaceName) {
        return"SELECT * FROM " + keyspaceName + ".orders WHERE order_id=?;";
    }
}
