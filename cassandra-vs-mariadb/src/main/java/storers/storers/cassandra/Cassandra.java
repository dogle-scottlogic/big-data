package storers.storers.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dogle on 05/12/2016.
 */
public class Cassandra {

    private Cluster cluster;
    private Session session;
    private String keyspaceName = "";

    public Cassandra(String host) {
        cluster = Cluster.builder().addContactPoint(host).withLoadBalancingPolicy(new RoundRobinPolicy()).build();
    }

    public boolean connect() {
        try {
            this.session = this.cluster.connect();
            return true;
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
    }

    public boolean close() {
        try {
            this.session.close();
            this.cluster.close();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean createKeySpace(String name) {
        try {
            dropKeySpace(name);
            this.session.execute(CQL_Querys.createKeySpace(name, 1));
            this.session.execute("USE " + name);
            this.keyspaceName = name;
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean dropKeySpace(String name) {
        try {
            this.session.execute(CQL_Querys.dropKeySpace(name));
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean createLineItemTable() {
        try {
            String query;
            this.session.execute(CQL_Querys.dropTable("lineItems"));
            this.session.execute(CQL_Querys.createLineItemTable(this.keyspaceName));
            query = "CREATE INDEX order_ids ON " + this.keyspaceName + ".lineItems ( order_id );";
            session.execute(query);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean createOrderTable() {
        try {
            this.session.execute(CQL_Querys.dropTable("orders"));
            this.session.execute(CQL_Querys.createOrderTable(this.keyspaceName));
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public void addOrder(JSONObject order) {
        String orderId = (String) order.get("id");
        JSONArray lineItems = (JSONArray) order.get("lineItems");
        ArrayList<String> lineItemsIds = new ArrayList<String>();
        BatchStatement batch = new BatchStatement();

        for (int i = 0; i < lineItems.size(); i++) {
            // Extract values
            JSONObject lineItem = (JSONObject) lineItems.get(i);
            String lineItemId = (String) lineItem.get("id");
            String productId = (String) ((JSONObject) lineItem.get("product")).get("id");
            int quantity = new Integer(((Long) lineItem.get("quantity")).intValue());
            double linePrice = (Double) lineItem.get("linePrice");

            // Add prepared statement to batch
            PreparedStatement p = this.session.prepare(CQL_Querys.addLineItem(this.keyspaceName));
            batch.add(p.bind(lineItemId, orderId, productId, quantity, linePrice));
            lineItemsIds.add("'" + lineItemId + "'");
        }
        session.execute(batch);
        //Add Order
        String clientId = (String) ((JSONObject) order.get("client")).get("id");
        Long dateLong = (Long) order.get("date");
        String created = new Date(dateLong).toString();
        String status = (String) order.get("status");
        Double subTotal = (Double) order.get("subTotal");

        // Add prepared statement to batch
        PreparedStatement p = this.session.prepare(CQL_Querys.addOrder(this.keyspaceName));
        BoundStatement b = p.bind(orderId, lineItemsIds, clientId, created, status, subTotal);
        session.execute(b);
    }

    public void removeOrder(JSONObject order) {
        BatchStatement batch = new BatchStatement();
        String orderId = (String) order.get("data");
        PreparedStatement p = session.prepare(CQL_Querys.selectAllLineItemIDs(this.keyspaceName));
        BoundStatement b = p.bind(orderId);
        // Remove all lineItems
        ResultSet lineItemIds = session.execute(b);
        List<String> results = lineItemIds.all().get(0).getList(0, String.class);
        for (String id : results) {
            p = session.prepare(CQL_Querys.deleteLineItem(this.keyspaceName));
            batch.add(p.bind(id));
        }
        session.execute(batch);
        p = session.prepare(CQL_Querys.deleteOrder(this.keyspaceName));
        b = p.bind(orderId);
        session.execute(b);
    }

    public void updateOrder(JSONObject order) {
        BatchStatement batchStatement = new BatchStatement();
        String orderId = (String) order.get("id");
        JSONArray lineItems = (JSONArray) order.get("lineItems");
        for (int i = 0; i < lineItems.size(); i++) {
            JSONObject lineItem = (JSONObject) lineItems.get(i);
            String lineItemId = (String) lineItem.get("id");
            int quantity = new Integer(((Long) lineItem.get("quantity")).intValue());
            double linePrice = (Double) lineItem.get("linePrice");
            PreparedStatement p = session.prepare(CQL_Querys.updateLineItem(this.keyspaceName));
            batchStatement.add(p.bind(quantity, linePrice, lineItemId));
        }
        session.execute(batchStatement);
        Long dateLong = (Long) order.get("date");
        String created = new Date(dateLong).toString();
        String status = (String) order.get("status");
        Double subTotal = (Double) order.get("subTotal");
        PreparedStatement p = session.prepare(CQL_Querys.updateOrder(this.keyspaceName));
        BoundStatement bound = p.bind(created, status, subTotal, orderId);
        session.execute(bound);
    }

    public void updateOrderStatus(JSONObject order) {
        String orderId = (String) order.get("id");
        String newStatus = (String) order.get("status");
        PreparedStatement p = session.prepare(CQL_Querys.updateOrderStatus(this.keyspaceName));
        BoundStatement b = p.bind(newStatus, orderId);
        session.execute(b);
    }

    public void readOrder(JSONObject order) {
        String orderId = (String) order.get("data");
        PreparedStatement p = session.prepare(CQL_Querys.selectAllLineItems(this.keyspaceName));
        BoundStatement b = p.bind(orderId);
        session.execute(b);
        p = session.prepare(CQL_Querys.selectAllOrders(this.keyspaceName));
        b = p.bind(orderId);
        session.execute(b);
    }
}
