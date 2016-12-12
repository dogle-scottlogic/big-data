package storers.storers.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
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
            this.session.execute(CQL_Querys.dropTable("lineItems_by_orderId"));
            this.session.execute(CQL_Querys.createLineItemTable(this.keyspaceName));
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

    // CRUD
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
            batch.add(p.bind(orderId, lineItemId, productId, quantity, linePrice));
            lineItemsIds.add("'" + lineItemId + "'");
        }
        ResultSetFuture futureLineItems = session.executeAsync(batch);
        handleError(futureLineItems);

        //Add Order
        String clientId = (String) ((JSONObject) order.get("client")).get("id");
        Long dateLong = (Long) order.get("date");
        String created = new Date(dateLong).toString();
        String status = (String) order.get("status");
        Double subTotal = (Double) order.get("subTotal");

        // Add prepared statement to batch
        PreparedStatement p = this.session.prepare(CQL_Querys.addOrder(this.keyspaceName));
        BoundStatement b = p.bind(orderId, lineItemsIds, clientId, created, status, subTotal);
        ResultSetFuture futureOrders = session.executeAsync(b);
        handleError(futureOrders);
    }

    public void removeOrder(JSONObject order) {
        String orderId = (String) order.get("data");
        PreparedStatement p = session.prepare(CQL_Querys.deleteLineItem(this.keyspaceName));
        BoundStatement b = p.bind(orderId);
        ResultSetFuture fli = session.executeAsync(b);
        handleError(fli);
        p = session.prepare(CQL_Querys.deleteOrder(this.keyspaceName));
        b = p.bind(orderId);
        ResultSetFuture fo = session.executeAsync(b);
        handleError(fo);
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
            batchStatement.add(p.bind(quantity, linePrice, lineItemId, orderId));
        }
        ResultSetFuture fli = session.executeAsync(batchStatement);
        handleError(fli);
        Long dateLong = (Long) order.get("date");
        String created = new Date(dateLong).toString();
        String status = (String) order.get("status");
        Double subTotal = (Double) order.get("subTotal");
        PreparedStatement p = session.prepare(CQL_Querys.updateOrder(this.keyspaceName));
        BoundStatement bound = p.bind(created, status, subTotal, orderId);
        ResultSetFuture fo = session.executeAsync(bound);
        handleError(fo);

    }

    public void updateOrderStatus(JSONObject order) {
        String orderId = (String) order.get("id");
        String newStatus = (String) order.get("status");
        PreparedStatement p = session.prepare(CQL_Querys.updateOrderStatus(this.keyspaceName));
        BoundStatement b = p.bind(newStatus, orderId);
        ResultSetFuture fo = session.executeAsync(b);
        handleError(fo);
    }

    public void readOrder(JSONObject order) {
        String orderId = (String) order.get("data");
        PreparedStatement p = session.prepare(CQL_Querys.selectAllLineItems(this.keyspaceName));
        BoundStatement b = p.bind(orderId);
        ResultSetFuture fli = session.executeAsync(b);
        handleError(fli);
        p = session.prepare(CQL_Querys.selectAllOrders(this.keyspaceName));
        b = p.bind(orderId);
        ResultSetFuture fo = session.executeAsync(b);
        handleError(fo);
    }

    private void handleError(ResultSetFuture futureLineItems) {
        Futures.addCallback(futureLineItems, new FutureCallback<ResultSet>() {
            public void onSuccess(ResultSet result) {
            }
            public void onFailure(Throwable t) {
                try {
                    throw t;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }
}
