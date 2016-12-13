package storers.storers;

import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.DatabaseEventFailedException;
import storers.storers.cassandra.Cassandra;

import java.sql.Struct;
import java.util.EventListener;

/**
 * Created by dogle on 05/12/2016.
 */
public class CassandraDBStorer implements Storer {

    private Cassandra cassandra;
    private CSVLogger logger;

    public CassandraDBStorer() { //TODO remove
        boolean success;
        this.cassandra = new Cassandra("127.0.0.7");
        cassandra.connect();
        success = cassandra.createKeySpace("orders");
        if (success) success = cassandra.createLineItemTable();
        if (success) success = cassandra.createOrderTable();
        if (!success) System.out.println("An error occurred setting up the database");
    }

    public CassandraDBStorer(CSVLogger logger) {
        boolean success;
        this.cassandra = new Cassandra("127.0.0.7", logger);
        cassandra.connect();
        success = cassandra.createKeySpace("orders");
        this.logger = logger;
        if (success) success = cassandra.createLineItemTable();
        if (success) success = cassandra.createOrderTable();
        if (!success) System.out.println("An error occurred setting up the database");
    }

    public String[] messageHandler(JSONObject message) {

        String type = (String) message.get("type");
        long timeTaken = 0;
        boolean success = true;
        String errorMessage = "No error";

        try {

            if (type.equals("CREATE")) {
                timeTaken = create((JSONObject) message.get("data"));
            }
            if (type.equals("READ")) {
                timeTaken = read(message);
            }
            if (type.equals("UPDATE")) {
                timeTaken = update((JSONObject) message.get("data"));
            }
            if (type.equals("UPDATE_STATUS")) {
                timeTaken = updateStatus((JSONObject) message.get("data"));
            }
            if (type.equals("DELETE")) {
                timeTaken = delete(message);
            }
        } catch (DatabaseEventFailedException exception) {
            success = false;
            errorMessage = exception.getMessage();
        }
        String[] log = new String[]{"Cassandra", type, String.valueOf(timeTaken), String.valueOf(success), errorMessage, String.valueOf(System.nanoTime())};
        return log;
    }

    public long create(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.addOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return 1;
    }

    public long read(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.readOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return 1;
    }

    public long update(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.updateOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return 1;
    }

    public long updateStatus(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.updateOrderStatus(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return 1;
    }

    public long delete(final JSONObject message) throws DatabaseEventFailedException {
        Timer timer = new Timer();
        try {
            cassandra.deleteOrder(message, timer);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return 1;
    }
}