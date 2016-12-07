package storers.MariaDB;

import storers.MariaDB.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public abstract class RunnableDBQuery implements Runnable {
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
   }

    public boolean doQuery(String query) {
        boolean wasSuccessful = false;
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(1);
            statement.execute(query);
            connection.commit();
            wasSuccessful = true;
        } catch (SQLException e) {
            e.printStackTrace();
            wasSuccessful = false;
        } finally {
            if (wasSuccessful) {
                System.out.println("SUCCESS: " + query);
            } else {
                System.out.println("FAILED: " + query);
            }
            return wasSuccessful;
        }
    }
}
