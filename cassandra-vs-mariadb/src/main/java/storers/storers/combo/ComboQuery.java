package storers.storers.combo;

import com.datastax.driver.core.*;
import com.datastax.driver.core.ResultSet;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import io.netty.util.concurrent.Future;
import storers.storers.Order;
import storers.storers.maria.enums.DBEventType;
import storers.CSVLogger;
import storers.storers.Timer;

import java.sql.*;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public abstract class ComboQuery implements Runnable{
    private DBEventType type;
    private CSVLogger logger;

    // Cassandra
    private Session cassandraConnection;
    private BatchStatement cassandraBatch;
    private ArrayList<BoundStatement> cassandraQueryQueue;

    //Maria
    private Connection mariaConnection;
    private Statement mariaBatch;
    private ArrayList<String> mariaQueryQueue;

    // Read Count Handler
    final boolean[] readEventHappened = new boolean[]{ false };

    public ComboQuery(
            Session cassandraConnection,
            Connection mariaConnection,
            CSVLogger logger,
            DBEventType type
    ) throws SQLException {
        this.cassandraConnection = cassandraConnection;
        this.cassandraBatch = new BatchStatement();
        this.cassandraQueryQueue = new  ArrayList<BoundStatement>();
        this.mariaConnection = mariaConnection;
        this.mariaBatch = mariaConnection.createStatement();
        this.mariaQueryQueue = new  ArrayList<String>();
        this.logger = logger;
        this.type = type;
    }

    public abstract void addToBatch(Order order) throws SQLException;

    public void run() {
        // Maria
        String mariaErrorMessage = "No Error";
        Timer mariaTimer = new Timer();
        mariaTimer.startTimer();
        ExecutorService executor = Executors.newCachedThreadPool();

        try {
                if (type == DBEventType.READ) {
                    ArrayList<java.util.concurrent.Future<?>> futures = new ArrayList<java.util.concurrent.Future<?>>();
                    for (final String mariaQuery : mariaQueryQueue) {
                        futures.add(executor.submit(new Runnable() {
                            public void run() {
                                java.sql.ResultSet r = null;
                                try {
                                    r = getMariaBatch().executeQuery(mariaQuery);
                                    //noinspection StatementWithEmptyBody
                                    while (r.next()) { /* Iterate Over Results */ }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));
                        //noinspection StatementWithEmptyBody
                        while (!allFuturesComplete(futures)) { /* Waiting...*/  }
                    }

                } else {
                    getMariaBatch().executeBatch();
                }
                getMariaConnection().commit();
            } catch (SQLException e) {
                e.printStackTrace();
                mariaErrorMessage = e.getMessage();
            } finally {
                String timeTaken = String.valueOf(mariaTimer.stopTimer());
                String[] log = new String[]{ "Maria", type.toString(), timeTaken, String.valueOf(true), mariaErrorMessage, String.valueOf(System.nanoTime()) };
                logger.logEvent(log, false);
            }

            // Cassandra
            Timer cassandraTimer = new Timer();
            cassandraTimer.startTimer();
            ResultSetFuture futureOrders =  getCassandraConnection().executeAsync(getCassandraBatch());
            if (type == DBEventType.READ) {
                for (BoundStatement cassandraQuery : cassandraQueryQueue) {
                    cassandraReadHandler(getCassandraConnection().executeAsync(cassandraQuery), "READ", cassandraTimer);
                }
            } else {
                queryHandler(futureOrders, type.toString(), cassandraTimer);
            }

            // Cleanup
            try {
                getMariaBatch().close();
                getMariaConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private boolean allFuturesComplete(ArrayList<java.util.concurrent.Future<?>> futures) {
        for (java.util.concurrent.Future<?> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    private void cassandraReadHandler(ResultSetFuture future, final String type, final Timer timer) {
        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            public void onSuccess(ResultSet result) {
                result.all();
                if (readEventHappened[0]) {
                    readEventHappened[0] = false;
                    String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(true), "No Error", String.valueOf(System.nanoTime())};
                    logger.logEvent(log, false);
                } else {
                    readEventHappened[0] = true;
                }
            }

            public void onFailure(Throwable t) {
                String[] log = new String[]{"Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(false), t.getMessage(), String.valueOf(System.nanoTime())};
                logger.logEvent(log, false);
            }
        });
    }


    private void queryHandler(ResultSetFuture future, final String type, final Timer timer) {
        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            public void onSuccess(ResultSet result) {
                String[] log = new String[]{
                    "Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(true), "No Error", String.valueOf(System.nanoTime())
                };
                logger.logEvent(log, false);
            }

            public void onFailure(Throwable t) {
                String[] log = new String[]{
                    "Cassandra", type, String.valueOf(timer.stopTimer()), String.valueOf(false), t.getMessage(), String.valueOf(System.nanoTime())
                };
                logger.logEvent(log, false);
            }
        });
    }

    public void addToMariaQueryQueue(String sqlQuery) {
        this.mariaQueryQueue.add(sqlQuery);
    }

    public void addToCassandraQueryQueue(BoundStatement boundStatement) {
        this.cassandraQueryQueue.add(boundStatement);
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
