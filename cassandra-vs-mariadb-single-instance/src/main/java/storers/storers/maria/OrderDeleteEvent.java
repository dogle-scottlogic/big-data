package storers.storers.maria;

import storers.CSVLogger;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Delete Order and Associated Line Items
 */
public class OrderDeleteEvent extends QueryEvent {
    public OrderDeleteEvent(boolean useASync, Connection connection, String orderId, CSVLogger csvLogger) {
        super(useASync, connection, orderId, DBEventType.DELETE, csvLogger);
    }

    public void runQuery() {
        doQuery(deleteString("orders.`line_item`", "order_id", orderId));
        doQuery(deleteString("orders.`order`", "id", orderId));
        end();
    }

    private String deleteString(String table, String idField, String orderId) {
        return "DELETE FROM " + table + " WHERE " + idField + "='" + orderId + "';";
    }
}
