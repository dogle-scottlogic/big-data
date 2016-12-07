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

    public void doQuery(String query) {
        try {
            Statement s = connection.createStatement();
            s.execute(query);
            System.out.println(query);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
