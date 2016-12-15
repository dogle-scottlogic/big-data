package storers.storers.combo;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by lcollingwood on 15/12/2016.
 */
public class UpdateStatus extends ComboQuery {

    public UpdateStatus(Session cassandraConnection, Connection mariaConnection, CSVLogger logger, Order order)  throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.UPDATE_STATUS);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {
        String status = order.getStatus();
        String orderId = order.getOrderId();

        // Cassandra
        getCassandraBatch().add(prepareBoundStatement(status, orderId));

        // Maria
        getMariaBatch().addBatch(prepareSQL(status, orderId));
    }

    private BoundStatement prepareBoundStatement(String status, String orderId) {
        String keyspaceName = getCassandraConnection().getLoggedKeyspace();
        return getCassandraConnection()
                .prepare(CQL_Querys.updateOrderStatus(keyspaceName))
                .bind(status, orderId);
    }

    private String prepareSQL(String status, String orderId) {
        return "UPDATE orders.`order` SET status='" + status + "' WHERE id='" + orderId + "';";
    }
}
