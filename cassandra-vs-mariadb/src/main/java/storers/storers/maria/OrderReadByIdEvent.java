package storers.storers.maria;

import storers.CSVLogger;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Read Order & it's Line Items by Order Id
 */
public class OrderReadByIdEvent extends QueryEvent {
    public OrderReadByIdEvent(boolean useASync, Connection connection, String orderId, CSVLogger csvLogger) {
        super(useASync, connection, orderId, DBEventType.READ, csvLogger);
    }

    public void runQuery() {
        doQueryNoBatch("SELECT * FROM orders.`order` WHERE id='" + orderId + "';");
        doQueryNoBatch("SELECT * FROM orders.`line_item` WHERE order_id='" + orderId + "';");
        end();
    }
}
