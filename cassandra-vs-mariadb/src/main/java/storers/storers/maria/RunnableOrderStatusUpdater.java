package storers.storers.maria;

import org.json.simple.JSONObject;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;

/**
 * Update Order Status
 */
public class RunnableOrderStatusUpdater extends RunnableDBQuery {
    private JSONObject data;

    public RunnableOrderStatusUpdater(Connection connection, JSONObject data) {
        super(connection, (String) data.get("id"), DBEventType.UPDATE_STATUS);
        this.data = data;
    }

    public void run() {
        doQuery("UPDATE orders.`order` SET status='" + (String) data.get("status") + "' WHERE id='" + orderId + "';");
        end();
    }
}
