package storers.maria;

import storers.Timer;
import storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public abstract class RunnableDBQuery implements Runnable {
    private Timer timer = new Timer();
    private long timeTaken = 0;
    private boolean wasSuccessful = false;

    public DBEventType ACTION_TYPE;
    public Thread thread;
    public Connection connection;
    public String orderId;

    public RunnableDBQuery(Connection connection, String orderId, DBEventType eventType) {
        this.connection = connection;
        this.orderId = orderId;
        this.ACTION_TYPE = eventType;
    }

    public void start() {
        thread = new Thread(this, ACTION_TYPE.toString() + ":" + orderId);
        thread.start();
        timer.startTimer();
    }

    public void end() {
        timeTaken = timer.stopTimer();
        System.out.println(ACTION_TYPE.toString() + " Event (Order: " + orderId + ") completed in MariaDB: " + wasSuccessful);
        System.out.println(ACTION_TYPE.toString() + " Event in MariaDB took: " + timeTaken + " nanoseconds");
    }

    public void doQuery(String query) {
        try {
            Statement statement = connection.createStatement();
//            statement.setQueryTimeout(1);
            statement.execute(query);
            connection.commit();
            wasSuccessful = true;
        } catch (SQLException e) {
            e.printStackTrace();
            wasSuccessful = false;
        }
    }
}
