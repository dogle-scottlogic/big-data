package storers.storers.maria;

import storers.storers.Timer;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public abstract class QueryEvent {
    private Timer timer;
    private long timeTaken;
    private boolean wasSuccessful;
    private String errorMessage;

    public DBEventType ACTION_TYPE;
    public Connection connection;
    public String orderId;

    public QueryEvent(Connection connection, String orderId, DBEventType eventType) {
        this.connection = connection;
        this.orderId = orderId;
        this.ACTION_TYPE = eventType;
        this.timer = new Timer();
        this.timeTaken = 0;
        this.wasSuccessful = false;
        this.errorMessage = "No Error";
    }

    public abstract String[] runQuery();

    public String[] start() {
        timer.startTimer();
        return runQuery();
    }

    public String[] end() {
        timeTaken = timer.stopTimer();

        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println(ACTION_TYPE.toString() + " Event (Order: " + orderId + ") completed in MariaDB: " + wasSuccessful);
//        System.out.println(ACTION_TYPE.toString() + " Event in MariaDB took: " + timeTaken + " nanoseconds");
        String timeTakenString = Long.valueOf(timeTaken).toString();
        return new String[]{"MariaDB", ACTION_TYPE.toString(), timeTakenString, Boolean.toString(wasSuccessful), errorMessage, String.valueOf(System.nanoTime())};
    }

    public void doQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            connection.commit();
            wasSuccessful = true;
        } catch (SQLException e) {
            e.printStackTrace();
            wasSuccessful = false;
            errorMessage = e.getMessage();
        }
    }
}
