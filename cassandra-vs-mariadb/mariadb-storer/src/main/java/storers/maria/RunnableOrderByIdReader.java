package storers.maria;

import storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Read Order & it's Line Items by Order Id
 */
public class RunnableOrderByIdReader extends RunnableDBQuery {
    public RunnableOrderByIdReader(Connection connection, String orderId) {
        super(connection, orderId, DBEventType.ORDER_STATUS_UPDATE);
    }

    public void run() {
        doQuery("SELECT * FROM orders.`order` WHERE id='" + orderId + "';");
        doQuery("SELECT * FROM orders.`line_item` WHERE order_id='" + orderId + "';");
    }
}
