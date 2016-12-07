package storers.maria;

import storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Delete Order and Associated Line Items
 */
public class RunnableOrderDeleter extends RunnableDBQuery {
    public RunnableOrderDeleter(Connection connection, String orderId) {
        super(connection, orderId, DBEventType.DELETE);
    }

    public void run() {
        doQuery(deleteString("orders.`line_item`", "order_id", orderId));
        doQuery(deleteString("orders.`order`", "id", orderId));
        end();
    }

    private String deleteString(String table, String idField, String orderId) {
        return "DELETE FROM " + table + " WHERE " + idField + "='" + orderId + "';";
    }
}
