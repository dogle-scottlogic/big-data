package storers.storers;

import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.DatabaseEventFailedException;
import storers.storers.cassandra.Cassandra;

/**
 * Created by dogle on 05/12/2016.
 */
public class CassandraDBStorer implements Storer {

    private Cassandra cassandra;

    public CassandraDBStorer(CSVLogger logger) {
        boolean success;
        this.cassandra = new Cassandra("127.0.0.7", logger);
        cassandra.connect();
        success = cassandra.createKeySpace("orders");
        if (success) success = cassandra.createLineItemTable();
        if (success) success = cassandra.createOrderTable();
        if (!success) System.out.println("An error occurred setting up the database");
    }

    public void messageHandler(JSONObject message) {
        String type = (String) message.get("type");
        try {
            if (type.equals("CREATE")) {
                create((JSONObject) message.get("data"));
            }
            if (type.equals("READ")) {
                read(message);
            }
            if (type.equals("UPDATE")) {
                update((JSONObject) message.get("data"));
            }
            if (type.equals("UPDATE_STATUS")) {
                updateStatus((JSONObject) message.get("data"));
            }
            if (type.equals("DELETE")) {
                delete(message);
            }
        } catch (DatabaseEventFailedException exception) {
            exception.printStackTrace();
        }
    }

    public void shutdown() {
    }

    public void create(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.addOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
    }

    public void read(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.readOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
    }

    public void update(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.updateOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
    }

    public void updateStatus(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.updateOrderStatus(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
    }

    public void delete(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.deleteOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
    }

    public void setLogger(CSVLogger logger) {
        this.cassandra.setLogger(logger);
    }
}