package storers.storers.combo;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class Create extends ComboQuery {

    public Create(Session cassandraConnection, Connection mariaConnection, CSVLogger logger) throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.CREATE);
    }

    public void addToBatch(JSONObject data) throws SQLException {
        Long dateLong = (Long) data.get("date");
        Double subTotal = (Double) data.get("subTotal");
        String clientId = (String) ((JSONObject) data.get("client")).get("id");
        String created = new Date(dateLong).toString();
        String status = (String) data.get("status");
        String orderId = (String) data.get("id");
        String keyspaceName = getCassandraConnection().getLoggedKeyspace();
        JSONArray lineItems = (JSONArray) data.get("lineItems");
        ArrayList<String> lineItemsIds = new ArrayList<String>();

        // Maria (Create Order Query)
        getMariaBatch().addBatch("INSERT INTO orders.`order` VALUES('" + orderId + "', '" + clientId + "', '" + created + "', '" + status + "');");
        String mariaInsertQueryPrefix = "INSERT INTO orders.line_item(order_id, product_id, quantity) VALUES";

        for (int i = 0; i < lineItems.size(); i++) {
            // Extract values
            JSONObject lineItem = (JSONObject) lineItems.get(i);
            String lineItemId = (String) lineItem.get("id");
            String productId = (String) ((JSONObject) lineItem.get("product")).get("id");
            int quantity = new Integer(((Long) lineItem.get("quantity")).intValue());
            double linePrice = (Double) lineItem.get("linePrice");

            // Add Cassandra Batch Statement
            PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.addLineItem(keyspaceName));
            getCassandraBatch().add(p.bind(orderId, lineItemId, productId, quantity, linePrice));
            lineItemsIds.add("'" + lineItemId + "'");

            // Add Maria Batch Statement
            String mariaLineItemQuery = mariaInsertQueryPrefix.concat( createLineItemPartialMariaQuery(orderId, lineItem)).concat(", ");
            mariaLineItemQuery = mariaLineItemQuery.substring(0, mariaInsertQueryPrefix.length() - 2);
            getMariaBatch().addBatch(mariaLineItemQuery);
        }

        // Add prepared statement to batch
        PreparedStatement p =  getCassandraConnection().prepare(CQL_Querys.addOrder(keyspaceName));
        getCassandraBatch().add(p.bind(orderId, lineItemsIds, clientId, created, status, subTotal));
    }

    private String createLineItemPartialMariaQuery(String orderId, JSONObject lineItem) {
        JSONObject product = (JSONObject) lineItem.get("product");
        String productId = (String) product.get("id");
        Long quantity = (Long) lineItem.get("quantity");
        return "('" + orderId + "', '" + productId + "', " + Long.valueOf(quantity).toString() + ")";
    }
}
