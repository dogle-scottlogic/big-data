package storers.storers.maria;

import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Read Order & it's Line Items by Order Id
 */
public class OrderReadByIdEvent extends QueryEvent {
    public OrderReadByIdEvent(Connection connection, String orderId) {
        super(connection, orderId, DBEventType.READ);
    }

    public void runQuery() {
        doQuery("SELECT * FROM orders.`order` WHERE id='" + orderId + "';");
        doQuery("SELECT * FROM orders.`line_item` WHERE order_id='" + orderId + "';");
        end();
    }
}
