package storers.storers.maria;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.util.Iterator;

/**
 * Create Order with Line Items in maria
 */
public class OrderCreateEvent extends QueryEvent {
    private JSONObject data;

    public OrderCreateEvent(boolean useASync, Connection connection, JSONObject data, CSVLogger csvLogger) {
        super(useASync, connection, (String) data.get("id"), DBEventType.CREATE, csvLogger);
        this.data = data;
    }

    public void runQuery() {
        JSONObject client = (JSONObject) data.get("client");
        String clientId = (String) client.get("id");
        Long date = (Long) data.get("date");
        String status = (String) data.get("status");
        doQuery("INSERT INTO orders.`order` VALUES('" + orderId + "', '" + clientId + "', '" + date.toString() + "', '" + status + "');");
        JSONArray lineItems = (JSONArray) data.get("lineItems");
        createLineItems(orderId, lineItems);
        end();
    }

    private void createLineItems(String orderId, JSONArray lineItems) {
        Iterator lineItemsIterator = lineItems.iterator();
        String query = "INSERT INTO orders.line_item(order_id, product_id, quantity) VALUES";

        while (lineItemsIterator.hasNext()) {
            JSONObject nextLineItem = (JSONObject) lineItemsIterator.next();
            query = query.concat(createLineItemPartialQuery(orderId, nextLineItem)).concat(", ");
        }
        // Chop last comma
        query = query.substring(0, query.length() - 2);
        doQuery(query);
    }

    private String createLineItemPartialQuery(String orderId, JSONObject lineItem) {
        JSONObject product = (JSONObject) lineItem.get("product");
        String productId = (String) product.get("id");
        Long quantity = (Long) lineItem.get("quantity");
        return "('" + orderId + "', '" + productId + "', " + quantity.toString() + ")";
    }
}