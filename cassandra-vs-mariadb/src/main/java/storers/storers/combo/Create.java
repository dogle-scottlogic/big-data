package storers.storers.combo;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class Create extends ComboQuery {

    public Create(Session cassandraConnection, Connection mariaConnection, CSVLogger logger, Order order) throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.CREATE);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {
        String keyspaceName = getCassandraConnection().getLoggedKeyspace();
        ArrayList<String> lineItemsIds = new ArrayList<String>();

        // Maria (Create Order Query)
        getMariaBatch().addBatch("INSERT INTO orders.`order` VALUES('" + order.getOrderId() + "', '" + order.getClientId() + "', '" + order.getDate() + "', '" + order.getStatus() + "');");
        String mariaInsertQueryPrefix = "INSERT INTO orders.line_item(order_id, product_id, quantity) VALUES";

        for (int i = 0; i < order.getLineItems().size(); i++) {
            // Extract values
            HashMap<String, String> lineItem = order.getLineItems().get(i);
            String lineItemId = lineItem.get("id");
            String productId = lineItem.get("productId");
            int quantity = Integer.parseInt(lineItem.get("quantity"));
            double linePrice = Double.parseDouble(lineItem.get("linePrice"));

            // Add Cassandra Batch Statement
            PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.addLineItem(keyspaceName));
            getCassandraBatch().add(p.bind(order.getOrderId(), lineItemId, productId, quantity, linePrice));
            lineItemsIds.add("'" + lineItemId + "'");

            // Add Maria Batch Statement
            String mariaLineItemQuery = mariaInsertQueryPrefix.concat( createLineItemPartialMariaQuery(order.getOrderId(), productId, lineItem.get("quantity"))).concat(", ");
            mariaLineItemQuery = mariaLineItemQuery.substring(0, mariaLineItemQuery.length() - 2);
            getMariaBatch().addBatch(mariaLineItemQuery);
        }

        // Add prepared statement to batch
        PreparedStatement p =  getCassandraConnection().prepare(CQL_Querys.addOrder(keyspaceName));
        getCassandraBatch().add(p.bind(order.getOrderId(), lineItemsIds, order.getClientId(), order.getDate(), order.getStatus(), order.getSubTotal()));
    }

    private String createLineItemPartialMariaQuery(String orderId, String productId, String quantity) {
        return "('" + orderId + "', '" + productId + "', " + quantity + ")";
    }
}
