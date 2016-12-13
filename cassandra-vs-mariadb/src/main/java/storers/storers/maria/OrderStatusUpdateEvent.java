package storers.storers.maria;

import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Update Order Status
 */
public class OrderStatusUpdateEvent extends QueryEvent {
    private JSONObject data;

    public OrderStatusUpdateEvent(boolean useASync, Connection connection, JSONObject data, CSVLogger csvLogger) {
        super(useASync, connection, (String) data.get("id"), DBEventType.UPDATE_STATUS, csvLogger);
        this.data = data;
    }

    public void runQuery() {
        doQuery("UPDATE orders.`order` SET status='" + (String) data.get("status") + "' WHERE id='" + orderId + "';");
        end();
    }
}
