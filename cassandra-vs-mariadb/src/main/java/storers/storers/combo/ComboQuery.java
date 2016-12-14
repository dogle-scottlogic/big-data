package storers.storers.combo;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import storers.storers.maria.enums.DBEventType;
import org.json.simple.JSONObject;
import storers.CSVLogger;
import storers.storers.Timer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public abstract class ComboQuery implements Runnable{
    private DBEventType type;
    private CSVLogger logger;

    // Cassandra
    private Session cassandraConnection;
    private BatchStatement cassandraBatch;

    //Maria
    private Connection mariaConnection;
    private Statement mariaBatch;

    public ComboQuery(
            Session cassandraConnection,
            Connection mariaConnection,
            CSVLogger logger,
            DBEventType type
    ) throws SQLException {

        this.cassandraConnection = cassandraConnection;
        this.cassandraBatch = new BatchStatement();

        this.mariaConnection = mariaConnection;
        this.mariaBatch = mariaConnection.createStatement();

        this.logger = logger;
        this.type = type;
    }

    public abstract void addToBatch(JSONObject data) throws SQLException;

    public void run() {
        // Maria
        Timer mariaTimer = new Timer();
        mariaTimer.startTimer();
        try {
            getMariaBatch().executeBatch();
            getMariaConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            String[] log = new String[]{"Maria", type.toString(), String.valueOf(mariaTimer.stopTimer()), String.valueOf(true), "No Error", String.valueOf(System.nanoTime())};
            logger.logEvent(log, false);
        }

        // Cassandra
        Timer cassandraTimer = new Timer();
        cassandraTimer.startTimer();
        ResultSetFuture futureOrders =  getCassandraConnection().executeAsync(getCassandraBatch());
        queryHandler(futureOrders, type.toString(), cassandraTimer);

        // Cleanup
        try {
            getMariaBatch().close();
            getMariaConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void queryHandler(ResultSetFuture future, final String type, final Timer timer) {
        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            public void onSuccess(ResultSet result) {
                String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(true), "No Error", String.valueOf(System.nanoTime())};
                logger.logEvent(log, false);
            }

            public void onFailure(Throwable t) {
                String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(false), t.getMessage(), String.valueOf(System.nanoTime())};
                logger.logEvent(log, false);
            }
        });
    }

    public BatchStatement getCassandraBatch() {
        return this.cassandraBatch;
    }

    public Statement getMariaBatch() {
        return this.mariaBatch;
    }

    public Session getCassandraConnection() {
        return this.cassandraConnection;
    }

    public Connection getMariaConnection() {
        return mariaConnection;
    }
}
