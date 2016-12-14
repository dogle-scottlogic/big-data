package storers.storers.maria;

import storers.CSVLogger;
import storers.storers.Timer;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public abstract class QueryEvent implements Runnable {
    private Statement statement;
    private Timer timer;
    private boolean useASync;
    private CSVLogger csvLogger;
    private boolean wasSuccessful;
    private String errorMessage;

    public DBEventType ACTION_TYPE;
    public Connection connection;
    public String orderId;
    private boolean didBatch;
    private String query;
    private boolean doSingleQuery;

    public QueryEvent(boolean useASync, Connection connection, String orderId, DBEventType eventType, CSVLogger csvLogger) {
        this.useASync = useASync;
        this.connection = connection;
        this.orderId = orderId;
        this.ACTION_TYPE = eventType;
        this.csvLogger = csvLogger;
        this.timer = new Timer();
        this.wasSuccessful = false;
        this.errorMessage = "No Error";

        try {
            this.statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void runQuery();

    public void start() {
        if (useASync) {
            Thread t = new Thread(this);
            t.start();
        } else {
            runQuery();
        }
    }

    public void end() {
        timer.startTimer();
        String timeTaken = "";
        try {
            if (didBatch) {
                statement.executeBatch();
            }

            if (doSingleQuery) {
                try {
                    statement = connection.createStatement();
                    statement.execute(query);
                    wasSuccessful = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    wasSuccessful = false;
                    errorMessage = e.getMessage();
                }
            }

            connection.commit();

            wasSuccessful = true;

            timeTaken =  Long.valueOf(timer.stopTimer()).toString();

            if (useASync) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            wasSuccessful = false;
            errorMessage = e.getMessage();
        }
        String[] logLine = new String[] {
            "MariaDB", ACTION_TYPE.toString(), timeTaken, Boolean.toString(wasSuccessful), errorMessage, String.valueOf(System.nanoTime())
        };
        csvLogger.logEvent(logLine, false);
    }

    public void run() {
        runQuery();
    }

    public void doQueryNoBatch(String query) {
        doSingleQuery = true;
        this.query = query;
    }

    public void doQuery(String query) {
        didBatch = true;
        try {
            statement.addBatch(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
