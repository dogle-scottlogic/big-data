package storers.storers.combo;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;
import storers.storers.maria.enums.DBType;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by lcollingwood on 15/12/2016.
 */
public class UpdateStatus extends ComboQuery {

    public UpdateStatus(Session cassandraConnection, PreparedStatement orderPreparedStatement, PreparedStatement lineItemPreparedStatement, Connection mariaConnection, CSVLogger logger, Order order, DBType type) throws SQLException {
        super(cassandraConnection, orderPreparedStatement, lineItemPreparedStatement, mariaConnection, logger, DBEventType.UPDATE_STATUS, type);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {
        String status = order.getStatus();
        String orderId = order.getOrderId();

        if (getDbtype() == DBType.CASSANDRA) {
            // Cassandra
            getCassandraBatch().add(prepareBoundStatement(status, orderId));
        }
        if (getDbtype() == DBType.MARIA_DB) {
            // Maria
            getMariaBatch().addBatch(prepareSQL(status, orderId));
        }
    }

    private BoundStatement prepareBoundStatement(String status, String orderId) {
        return getOrderPreparedStatement().bind(status, orderId);
    }

    private String prepareSQL(String status, String orderId) {
        return "UPDATE orders.`order` SET status='" + status + "' WHERE id='" + orderId + "';";
    }
}
