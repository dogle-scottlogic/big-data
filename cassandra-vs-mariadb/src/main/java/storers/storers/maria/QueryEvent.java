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
    private Timer timer;
    private boolean useASync;
    private CSVLogger csvLogger;

    public boolean wasSuccessful;
    public String errorMessage;

    public DBEventType ACTION_TYPE;
    public Connection connection;
    public String orderId;

    public QueryEvent(boolean useASync, Connection connection, String orderId, DBEventType eventType, CSVLogger csvLogger) {
        this.useASync = useASync;
        this.connection = connection;
        this.orderId = orderId;
        this.ACTION_TYPE = eventType;
        this.csvLogger = csvLogger;
        this.timer = new Timer();
        this.wasSuccessful = false;
        this.errorMessage = "No Error";
    }

    public abstract void runQuery();

    public void start() {
        timer.startTimer();
        if (useASync) {
            Thread t = new Thread(this);
            t.start();
        } else {
            runQuery();
        }
    }

    public void end() {
        try {
            connection.commit();
            if (useASync) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] logLine = new String[] {
            "MariaDB", ACTION_TYPE.toString(),
            Long.valueOf(timer.stopTimer()).toString(),
            Boolean.toString(wasSuccessful),
            errorMessage, String.valueOf(System.nanoTime())
        };
        csvLogger.logEvent(logLine, false);
    }

    public void run() {
        runQuery();
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
