package storers.storers;

import org.json.simple.JSONObject;
import storers.DatabaseEventFailedException;
import storers.storers.cassandra.Cassandra;

import java.util.Arrays;

/**
 * Created by dogle on 05/12/2016.
 */
public class CassandraDBStorer implements Storer{

    private Cassandra cassandra;
    private Timer timer = new Timer();

    public CassandraDBStorer() {
        boolean success;
        this.cassandra = new Cassandra("127.0.0.7");
        cassandra.connect();
        success = cassandra.createKeySpace("orders");
        if (success) success = cassandra.createLineItemTable();
        if (success) success = cassandra.createOrderTable();
        if (!success) System.out.println("An error occurred setting up the database");
        this.cassandra = cassandra;
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
        // Return Database type, eventType, time Taken, success, errorMessage
        String [] log = new String[] {"Cassandra", type, String.valueOf(timeTaken), String.valueOf(success), errorMessage, String.valueOf(System.nanoTime())};
        System.out.println(Arrays.toString(log));
        return log;
    }

    public long create(final JSONObject message) throws DatabaseEventFailedException {
        timer.startTimer();
        try {
            cassandra.addOrder(message);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return timer.stopTimer();
    }

    public long read(final JSONObject message) throws DatabaseEventFailedException {
        timer.startTimer();
        try {
            cassandra.readOrder(message);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return timer.stopTimer();
    }

    public long update(final JSONObject message) throws DatabaseEventFailedException {
        timer.startTimer();
        try {
            cassandra.updateOrder(message);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return timer.stopTimer();
    }

    public long updateStatus(final JSONObject message) throws DatabaseEventFailedException {
        timer.startTimer();
        try {
            cassandra.updateOrderStatus(message);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return timer.stopTimer();
    }

    public long delete(final JSONObject message) throws DatabaseEventFailedException {
        timer.startTimer();
        try {
            cassandra.removeOrder(message);
        } catch (Exception e) {
            throw new DatabaseEventFailedException(e.getMessage());
        }
        return timer.stopTimer();
    }
}