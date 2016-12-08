package storers.storers.maria;

import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Delete Order and Associated Line Items
 */
public class OrderDeleteEvent extends QueryEvent {
    public OrderDeleteEvent(Connection connection, String orderId) {
        super(connection, orderId, DBEventType.DELETE);
    }

    public String[] runQuery() {
        doQuery(deleteString("orders.`line_item`", "order_id", orderId));
        doQuery(deleteString("orders.`order`", "id", orderId));
        return end();
    }

    private String deleteString(String table, String idField, String orderId) {
        return "DELETE FROM " + table + " WHERE " + idField + "='" + orderId + "';";
    }
}
