package storers.storers.combo;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.DBType;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class Create extends ComboQuery {

    private ArrayList<String> lineItemsIds = new ArrayList<String>();
    private String mariaLineItemQuery = "INSERT INTO orders.line_item(order_id, product_id, quantity) VALUES";

    public Create(Session cassandraConnection, Connection mariaConnection, CSVLogger logger, Order order, DBType type) throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.CREATE, type);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {
        String keyspaceName = getCassandraConnection().getLoggedKeyspace();

        if (getDbtype() == DBType.MARIA_DB) {
            // Maria (Create Order Query)
            getMariaBatch().addBatch("INSERT INTO orders.`order` VALUES('" + order.getOrderId() + "', '" + order.getClientId() + "', '" + order.getDate() + "', '" + order.getStatus() + "');");
        }

        for (int i = 0; i < order.getLineItems().size(); i++) {
            HashMap<String, String> lineItem = order.getLineItems().get(i);
            addLineItemToBatch(lineItem, order, keyspaceName);
        }

        if (getDbtype() == DBType.MARIA_DB) {
            this.mariaLineItemQuery = this.mariaLineItemQuery.substring(0, this.mariaLineItemQuery.length() - 2);
            getMariaBatch().addBatch(this.mariaLineItemQuery);
        }

        // Add prepared statement to batch
        if (getDbtype() == DBType.CASSANDRA) {
            PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.addOrder(keyspaceName));
            getCassandraBatch().add(p.bind(order.getOrderId(), this.lineItemsIds, order.getClientId(), order.getDate(), order.getStatus(), order.getSubTotal()));
        }
    }

    private void addLineItemToBatch(HashMap<String, String> lineItem, Order order, String keyspaceName) throws SQLException {
        // Extract values
        String lineItemId = lineItem.get("id");
        String productId = lineItem.get("productId");
        int quantity = Integer.parseInt(lineItem.get("quantity"));
        double linePrice = Double.parseDouble(lineItem.get("linePrice"));

        if (getDbtype() == DBType.CASSANDRA) {
            // Add Cassandra Batch Statement
            PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.addLineItem(keyspaceName));
            getCassandraBatch().add(p.bind(order.getOrderId(), lineItemId, productId, quantity, linePrice));
            this.lineItemsIds.add("'" + lineItemId + "'");
        }

        if (getDbtype() == DBType.MARIA_DB) {
            // Add Maria Batch Statement
            this.mariaLineItemQuery = this.mariaLineItemQuery.concat(createLineItemPartialMariaQuery(order.getOrderId(), productId, lineItem.get("quantity"))).concat(", ");
        }
    }

    private String createLineItemPartialMariaQuery(String orderId, String productId, String quantity) {
        return "('" + orderId + "', '" + productId + "', " + quantity + ")";
    }
}
