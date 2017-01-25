package storers.storers.combo;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.apache.log4j.Logger;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.Timer;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.DBType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public abstract class ComboQuery implements Runnable {
    private final static Logger LOG = Logger.getLogger(ComboQuery.class);
    private DBEventType type;
    private DBType dbtype;
    private CSVLogger logger;

    // Cassandra
    private Session cassandraConnection;
    private final PreparedStatement orderPreparedStatement;
    private final PreparedStatement lineItemPreparedStatement;
    private BatchStatement cassandraBatch;
    private ArrayList<BoundStatement> cassandraQueryQueue;

    //Maria
    private Connection mariaConnection;
    private Statement mariaBatch;
    private ArrayList<String> mariaQueryQueue;

    // Read Count Handler
    private final boolean[] readEventHappened = new boolean[]{false};

    public ComboQuery(
            Session cassandraConnection,
            PreparedStatement orderPreparedStatement,
            PreparedStatement lineItemPreparedStatement,
            Connection mariaConnection,
            CSVLogger logger,
            DBEventType type,
            DBType dbtype
    ) throws SQLException {
        this.cassandraConnection = cassandraConnection;
        this.orderPreparedStatement = orderPreparedStatement;
        this.lineItemPreparedStatement = lineItemPreparedStatement;
        this.cassandraBatch = new BatchStatement();
        this.cassandraQueryQueue = new ArrayList<BoundStatement>();
        this.mariaConnection = mariaConnection;
        this.mariaBatch = mariaConnection.createStatement();
        this.mariaQueryQueue = new ArrayList<String>();
        this.logger = logger;
        this.type = type;
        this.dbtype = dbtype;
    }

    public abstract void addToBatch(Order order) throws SQLException;

    public void run() {
        // Maria
        boolean success = true;
        String mariaErrorMessage = "No Error";

        if (dbtype == DBType.MARIA_DB) {
            Timer mariaTimer = new Timer();
            mariaTimer.startTimer();
            try {
                if (type == DBEventType.READ) {
                    for (final String mariaQuery : mariaQueryQueue) {
                        java.sql.ResultSet r = getMariaBatch().executeQuery(mariaQuery);
                        //noinspection StatementWithEmptyBody
                        while (r.next()) { /* Iterate Over Results */ }
                    }


                } else {
                    getMariaBatch().executeBatch();
                }
                getMariaConnection().commit();
            } catch (SQLException e) {
                LOG.warn("Failed to apply database event", e);
                mariaErrorMessage = e.getMessage();
                mariaErrorMessage = mariaErrorMessage.replace("\n", " ");
                mariaErrorMessage = mariaErrorMessage.replace(',', ' ');
                success = false;
            } finally {
                String timeTaken = String.valueOf(mariaTimer.stopTimer());
                String[] log = new String[]{"Maria", type.toString(), timeTaken, String.valueOf(success), mariaErrorMessage, String.valueOf(System.currentTimeMillis())};
                logger.logEvent(log, false);
            }
        }
        if (dbtype == DBType.CASSANDRA) {
            // Cassandra
            Timer cassandraTimer = new Timer();
            cassandraTimer.startTimer();
            ResultSetFuture futureOrders = cassandraConnection.executeAsync(getCassandraBatch());
            if (type == DBEventType.READ) {
                for (BoundStatement cassandraQuery : cassandraQueryQueue) {
                    cassandraReadHandler(cassandraConnection.executeAsync(cassandraQuery), "READ", cassandraTimer);
                }
            } else {
                queryHandler(futureOrders, type.toString(), cassandraTimer);
            }
        }
        // Cleanup
        try {
            getMariaBatch().close();
            getMariaConnection().close();
        } catch (SQLException e) {
            LOG.warn("Failed to close connections", e);
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

    public Connection getMariaConnection() {
        return mariaConnection;
    }

    public DBType getDbtype() {
        return dbtype;
    }

    protected PreparedStatement getOrderPreparedStatement() {
        return orderPreparedStatement;
    }

    protected PreparedStatement getLineItemPreparedStatement() {
        return lineItemPreparedStatement;
    }
}
