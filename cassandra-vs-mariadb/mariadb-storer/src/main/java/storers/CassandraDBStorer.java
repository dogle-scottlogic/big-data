package storers;

import org.json.simple.JSONObject;
import storers.cassandra.Cassandra;

/**
 * Created by dogle on 05/12/2016.
 */
public class CassandraDBStorer {

    private Cassandra cassandra;
    private Thread create, update, delete;

    public CassandraDBStorer(Cassandra cassandra) {
        boolean success;
        success = cassandra.createKeySpace("orders");
        if (success) success = cassandra.createLineItemTable();
        if (success) success = cassandra.createOrderTable();
        if (!success) System.out.println("An error occurred setting up the database");
        this.cassandra = cassandra;
    }

    public void messageHandler(JSONObject message) {

        String type = (String) message.get("type");

        if (type.equals("UPDATE")) {
            update((JSONObject) message.get("data"));
        }

        if (type.equals("CREATE")) {
            create((JSONObject) message.get("data"));
        }

        if (type.equals("DELETE")) {
            delete(message);
        }
    }

    public void create(final JSONObject message) {
        this.create = new Thread(new Runnable() {
            public void run() {
                boolean success = cassandra.addOrder(message);
                System.out.println("Create Event completed in Cassandra: " + success);
            }
        });
        this.create.start();
    }

    public void update(final JSONObject message) {
        this.update = new Thread(new Runnable() {
            public void run() {
                boolean success = cassandra.updateOrder(message);
                System.out.println("Update Event completed in Cassandra: " + success);
            }
        });
        this.update.start();
    }

    public void delete(final JSONObject message) {
        this.delete = new Thread(new Runnable() {
            public void run() {
                boolean success = cassandra.removeOrder(message);
                System.out.println("Delete Event completed in Cassandra: " + success);
            }
        });
        this.delete.start();
    }
}
