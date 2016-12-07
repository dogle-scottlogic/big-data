package storers.storers.cassandra;

import com.datastax.driver.core.*;
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
        cluster = Cluster.builder().addContactPoint(host).build();
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
            int replication = 1;
            String query = "CREATE KEYSPACE IF NOT EXISTS " + name + " WITH replication "
                    + "= {'class':'SimpleStrategy', 'replication_factor':" + replication + "}; ";
            dropKeySpace(name);
            this.session.execute(query);
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
            String query = "Drop KEYSPACE " + name;
            this.session.execute(query);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean createLineItemTable() {
        try {
            String query;
            query = "DROP TABLE IF EXISTS lineItems;";
            this.session.execute(query);
            query = "CREATE TABLE IF NOT EXISTS " + this.keyspaceName + ".lineItems"
                    + "( lineItem_id text, "
                    + "order_id text, "
                    + "product_id text, "
                    + "quantity int, "
                    + "line_price double, "
                    + "PRIMARY KEY (lineItem_id)"
                    + ");";
            this.session.execute(query);
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
            String query;
            query = "DROP TABLE IF EXISTS orders;";
            this.session.execute(query);
            query = "CREATE TABLE IF NOT EXISTS " + this.keyspaceName + ".orders"
                    + "( order_id text PRIMARY KEY, "
                    + "lineItem_ids list<text>, "
                    + "client_id text, "
                    + "date_created text, "
                    + "status text, "
                    + "order_subTotal double );";
            this.session.execute(query);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean addOrder(JSONObject order) {
        String query;
        try {
            String orderId = (String) order.get("id");
            JSONArray lineItems = (JSONArray) order.get("lineItems");
            ArrayList<String> lineItemsIds = new ArrayList<String>();
            for (int i = 0; i < lineItems.size(); i++) {
                JSONObject lineItem = (JSONObject) lineItems.get(i);
                String lineItemId = (String) lineItem.get("id");
                String productId = (String) ((JSONObject) lineItem.get("product")).get("id");
                int quantity = new Integer(((Long) lineItem.get("quantity")).intValue());
                double linePrice = (Double) lineItem.get("linePrice");
                query = "INSERT INTO " + this.keyspaceName + ".lineItems" +
                        "( lineItem_id, order_id, product_id, quantity, line_price ) " +
                        "VALUES (?, ?, ?, ?, ?) IF NOT EXISTS;";
                PreparedStatement p = this.session.prepare(query);
                BoundStatement b = p.bind(lineItemId, orderId, productId, quantity, linePrice);
                session.execute(b);
                lineItemsIds.add("'" + lineItemId + "'");
            }
            //Add Order
            String clientId = (String) ((JSONObject) order.get("client")).get("id");
            Long dateLong = (Long) order.get("date");
            String created = new Date(dateLong).toString();
            String status = (String) order.get("status");
            Double subTotal = (Double) order.get("subTotal");
            query = "INSERT INTO " + this.keyspaceName + ".orders"
                    + "( order_id, lineItem_ids, client_id, date_created, status, order_subTotal ) "
                    + "VALUES (?, ?, ?, ?, ?, ?) IF NOT EXISTS;";
            PreparedStatement p = this.session.prepare(query);
            BoundStatement b = p.bind(orderId, lineItemsIds, clientId, created, status, subTotal);
            session.execute(b);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean removeOrder(JSONObject order) {
        String orderId = (String) order.get("data");
        try {
            PreparedStatement p = session.prepare("SELECT lineitem_ids FROM " + this.keyspaceName + ".orders WHERE order_id=?;");
            BoundStatement b = p.bind(orderId);
            // Remove all lineItems
            ResultSet lineItemIds = session.execute(b);
            List<String> results = lineItemIds.all().get(0).getList(0, String.class);
            for (String id : results) {
                p = session.prepare(
                        "DELETE FROM " + this.keyspaceName + ".lineItems WHERE lineItem_id=? IF EXISTS;"

                );
                b = p.bind(id);
                session.execute(b);
            }
            p = session.prepare("DELETE FROM " + this.keyspaceName + ".orders WHERE order_id=? IF EXISTS;");
            b = p.bind(orderId);
            session.execute(b);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean updateOrder(JSONObject order) {
        String orderId = (String) order.get("id");
        try {
            JSONArray lineItems = (JSONArray) order.get("lineItems");
            for (int i = 0; i < lineItems.size(); i++) {
                JSONObject lineItem = (JSONObject) lineItems.get(i);
                String lineItemId = (String) lineItem.get("id");
                int quantity = new Integer(((Long) lineItem.get("quantity")).intValue());
                double linePrice = (Double) lineItem.get("linePrice");
                PreparedStatement p = session.prepare(
                        "UPDATE " + this.keyspaceName + ".lineItems SET quantity=?, line_price=? WHERE lineItem_id=? IF EXISTS;"
                );
                BoundStatement bound = p.bind(quantity, linePrice, lineItemId);
                session.execute(bound);
            }
            Long dateLong = (Long) order.get("date");
            String created = new Date(dateLong).toString();
            String status = (String) order.get("status");
            Double subTotal = (Double) order.get("subTotal");
            PreparedStatement p = session.prepare("UPDATE " + this.keyspaceName + ".orders SET date_created=?, status=?, order_subTotal=? WHERE order_id=? IF EXISTS;");
            BoundStatement bound = p.bind(created, status, subTotal, orderId);
            session.execute(bound);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean updateOrderStatus(JSONObject order) {
        try {
            String orderId = (String) order.get("id");
            String newStatus = (String) order.get("status");
            PreparedStatement p = session.prepare("UPDATE " + this.keyspaceName + ".orders SET status=? WHERE order_id=? IF EXISTS;");
            BoundStatement b = p.bind(newStatus, orderId);
            session.execute(b);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean readOrder(JSONObject order) {
        String orderId = (String) order.get("data");
        try {
            PreparedStatement p = session.prepare("SELECT * FROM " + this.keyspaceName + ".lineItems WHERE order_id=?;");
            BoundStatement b = p.bind(orderId);
            session.execute(b);
            p = session.prepare("SELECT * FROM " + this.keyspaceName + ".orders WHERE order_id=?;");
            b = p.bind(orderId);
            session.execute(b);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }
}
