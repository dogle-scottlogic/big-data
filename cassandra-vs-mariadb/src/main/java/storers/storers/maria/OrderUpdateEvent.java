package storers.storers.maria;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.util.Iterator;

/**
 * Update an Order or it's Line Items
 */
public class OrderUpdateEvent extends QueryEvent {
    private JSONObject data;

    public OrderUpdateEvent(Connection connection, JSONObject data) {
        super(connection, (String) data.get("id"), DBEventType.DELETE);
        this.data = data;
    }

    public String[] runQuery() {
        JSONObject client = (JSONObject) data.get("client");
        String clientId = (String) client.get("id");
        Long date = (Long) data.get("date");
        doQuery("UPDATE orders.`order` " +
                "SET " +
                "client_id='" + clientId + "', " +
                "created='" + Long.valueOf(date).toString() + "' " +
                "WHERE id='" + orderId + "';");
        JSONArray lineItems = (JSONArray) data.get("lineItems");
        updateLineItems(orderId, lineItems);
        return end();
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
