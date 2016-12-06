package storers.MariaDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lcollingwood on 06/12/2016.
 */
public class RunnableQuery implements Runnable {
    private Thread thread;
    private String query;
    private Connection connection;

    public RunnableQuery(Connection connection, String query){
        this.query = query;
    }

    public void run() {
        try {
            Statement s = connection.createStatement();
            s.execute(query);
            System.out.println(query);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        thread = new Thread(this, query);
        thread.start();
    }
}
