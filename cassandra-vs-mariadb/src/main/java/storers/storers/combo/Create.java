package storers.storers.combo;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.DBType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class Create extends ComboQuery {

    private ArrayList<String> lineItemsIds = new ArrayList<>();
    private String mariaLineItemQuery = "INSERT INTO orders.line_item(id, order_id, product_id, quantity) VALUES";

    public Create(Session cassandraConnection, PreparedStatement orderPreparedStatement, PreparedStatement lineItemPreparedStatement, Connection mariaConnection, CSVLogger logger, Order order, DBType type) throws SQLException {
        super(cassandraConnection, orderPreparedStatement, lineItemPreparedStatement, mariaConnection, logger, DBEventType.CREATE, type);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {

        if (getDbtype() == DBType.MARIA_DB) {
            // Maria (Create Order Query)
            getMariaBatch().addBatch("INSERT INTO orders.`order` VALUES('" + order.getOrderId() + "', '" + order.getClientId() + "', '" + order.getDate() + "', '" + order.getStatus() + "');");
        }

        for (int i = 0; i < order.getLineItems().size(); i++) {
            HashMap<String, String> lineItem = order.getLineItems().get(i);
            addLineItemToBatch(lineItem, order);
        }

        if (getDbtype() == DBType.MARIA_DB) {
            this.mariaLineItemQuery = this.mariaLineItemQuery.substring(0, this.mariaLineItemQuery.length() - 2);
            getMariaBatch().addBatch(this.mariaLineItemQuery);
        }

        // Add prepared statement to batch
        if (getDbtype() == DBType.CASSANDRA) {
            getCassandraBatch().add(getOrderPreparedStatement().bind(order.getOrderId(), this.lineItemsIds, order.getClientId(), order.getDate(), order.getStatus(), order.getSubTotal()));
        }
    }

    private void addLineItemToBatch(HashMap<String, String> lineItem, Order order) throws SQLException {
        // Extract values
        String lineItemId = lineItem.get("id");
        String productId = lineItem.get("productId");
        int quantity = Integer.parseInt(lineItem.get("quantity"));
        double linePrice = Double.parseDouble(lineItem.get("linePrice"));

        if (getDbtype() == DBType.CASSANDRA) {
            // Add Cassandra Batch Statement
            getCassandraBatch().add(getLineItemPreparedStatement().bind(order.getOrderId(), lineItemId, productId, quantity, linePrice));
            this.lineItemsIds.add("'" + lineItemId + "'");
        }

        if (getDbtype() == DBType.MARIA_DB) {
            // Add Maria Batch Statement
            this.mariaLineItemQuery = this.mariaLineItemQuery.concat(createLineItemPartialMariaQuery(lineItemId, order.getOrderId(), productId, lineItem.get("quantity"))).concat(", ");
        }
    }

    private String createLineItemPartialMariaQuery(String id, String orderId, String productId, String quantity) {
        return "('" + id + "', '" + orderId + "', '" + productId + "', " + quantity + ")";
    }
}
