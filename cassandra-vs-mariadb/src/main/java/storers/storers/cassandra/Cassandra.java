package storers.storers.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import storers.CSVLogger;
import storers.storers.Timer;

/**
 * Created by dogle on 05/12/2016.
 */
public class Cassandra {

    private Cluster cluster;
    private Session session;
    private String keyspaceName = "";
    private final boolean readEventHappened[] = {false};
    private CSVLogger logger;

    public Cassandra(String host, CSVLogger logger) {
        this.cluster = Cluster.builder().addContactPoint(host).withLoadBalancingPolicy(new RoundRobinPolicy()).build();
        this.logger = logger;
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
    public void addOrder(JSONObject order, Timer timer) {
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

            //Add Order
            String clientId = (String) ((JSONObject) order.get("client")).get("id");
            Long dateLong = (Long) order.get("date");
            String created = new Date(dateLong).toString();
            String status = (String) order.get("status");
            Double subTotal = (Double) order.get("subTotal");

            // Add prepared statement to batch
            PreparedStatement p = this.session.prepare(CQL_Querys.addOrder(this.keyspaceName));
            batch.add(p.bind(orderId, lineItemsIds, clientId, created, status, subTotal));
            timer.startTimer();
            ResultSetFuture futureOrders = session.executeAsync(batch);
            queryHandler(futureOrders, "CREATE", timer);
    }

    public void deleteOrder(JSONObject order, Timer timer) {
        BatchStatement batch = new BatchStatement();
        String orderId = (String) order.get("data");
        PreparedStatement p = session.prepare(CQL_Querys.deleteLineItem(this.keyspaceName));
        batch.add(p.bind(orderId));
        p = session.prepare(CQL_Querys.deleteOrder(this.keyspaceName));
        batch.add(p.bind(orderId));
        timer.startTimer();
        ResultSetFuture fo = session.executeAsync(batch);
        queryHandler(fo, "DELETE", timer);
    }

    public void updateOrder(JSONObject order, Timer timer) {
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
        Long dateLong = (Long) order.get("date");
        String created = new Date(dateLong).toString();
        String status = (String) order.get("status");
        Double subTotal = (Double) order.get("subTotal");
        PreparedStatement p = session.prepare(CQL_Querys.updateOrder(this.keyspaceName));
        batchStatement.add(p.bind(created, status, subTotal, orderId));
        timer.startTimer();
        ResultSetFuture fo = session.executeAsync(batchStatement);
        queryHandler(fo, "UPDATE", timer);

    }

    public void updateOrderStatus(JSONObject order, Timer timer) {
        String orderId = (String) order.get("id");
        String newStatus = (String) order.get("status");
        PreparedStatement p = session.prepare(CQL_Querys.updateOrderStatus(this.keyspaceName));
        BoundStatement b = p.bind(newStatus, orderId);
        timer.startTimer();
        ResultSetFuture fo = session.executeAsync(b);
        queryHandler(fo, "UPDATE_STATUS", timer);
    }

    public void readOrder(JSONObject order, Timer timer) {
        String orderId = (String) order.get("data");
        PreparedStatement p = session.prepare(CQL_Querys.selectAllLineItems(this.keyspaceName));
        BoundStatement b = p.bind(orderId);
        ResultSetFuture fo = session.executeAsync(b);
        readHandler(fo, "READ", timer);
        p = session.prepare(CQL_Querys.selectAllOrders(this.keyspaceName));
        b = p.bind(orderId);
        timer.startTimer();
        fo = session.executeAsync(b);
        readHandler(fo, "READ", timer);
    }

    private void readHandler(ResultSetFuture future, final String type, final Timer timer) {
        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            public void onSuccess(ResultSet result) {
                if (readEventHappened[0]) {
                    readEventHappened[0] = false;
                    String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(true), "No Error", String.valueOf(System.nanoTime())};
                    logger.logEvent(log, false);
                } else {
                    readEventHappened[0] = true;
                }
            }

            public void onFailure(Throwable t) {
                String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(false), t.getMessage(), String.valueOf(System.nanoTime())};
                logger.logEvent(log, false);
            }
        });
    }

    private void queryHandler(ResultSetFuture future, final String type, final Timer timer) {
        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            public void onSuccess(ResultSet result) {
                String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(true), "No Error", String.valueOf(System.nanoTime())};
                logger.logEvent(log, false);
            }

            public void onFailure(Throwable t) {
                String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(false), t.getMessage(), String.valueOf(System.nanoTime())};
                logger.logEvent(log, false);
            }
        });
    }

    public void setLogger(CSVLogger logger) {
        this.logger = logger;
    }
}
