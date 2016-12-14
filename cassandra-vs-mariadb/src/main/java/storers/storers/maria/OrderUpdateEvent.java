package storers.storers.maria;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Update an Order or it's Line Items
 */
public class OrderUpdateEvent extends QueryEvent {
    private JSONObject data;

    public OrderUpdateEvent(boolean useASync, Connection connection, JSONObject data, CSVLogger csvLogger) {
        super(useASync, connection, (String) data.get("id"), DBEventType.UPDATE, csvLogger);
        this.data = data;
    }

    public void runQuery() {
        JSONObject client = (JSONObject) data.get("client");
        String clientId = (String) client.get("id");
        Long date = (Long) data.get("date");
        JSONArray lineItems = (JSONArray) data.get("lineItems");

        doQuery("UPDATE orders.`order` " +
                "SET " +
                "client_id='" + clientId + "', " +
                "created='" + Long.valueOf(date).toString() + "' " +
                "WHERE id='" + orderId + "';");
        updateLineItems(orderId, lineItems);
        end();
    }

    private void updateLineItems(String orderId, JSONArray lineItems) {
        Iterator lineItemsIterator = lineItems.iterator();

        while (lineItemsIterator.hasNext()) {
            JSONObject nextLineItem = (JSONObject) lineItemsIterator.next();
            JSONObject product = (JSONObject) nextLineItem.get("product");
            String productId = (String) product.get("id");
            Long quantity = (Long) nextLineItem.get("quantity");

            doQuery("UPDATE orders.line_item " +
                    "SET " +
                    "order_id='" + orderId + "', " +
                    "product_id='" + productId + "', " +
                    "quantity=" + Long.valueOf(quantity).toString() + " " +
                    "WHERE order_id='" + orderId + "';");
        }
    }
}
