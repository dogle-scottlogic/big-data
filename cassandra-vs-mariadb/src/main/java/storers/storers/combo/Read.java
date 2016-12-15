package storers.storers.combo;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by lcollingwood on 15/12/2016.
 */
public class Read extends ComboQuery {
    public Read(Session cassandraConnection, Connection mariaConnection, CSVLogger logger, Order order) throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.READ);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {
        String keyspaceName = getCassandraConnection().getLoggedKeyspace();
        String orderId = order.getOrderId();
        addToMariaQueryQueue("SELECT * FROM orders.`order` WHERE id='" + orderId + "';");
        addToMariaQueryQueue("SELECT * FROM orders.`line_item` WHERE order_id='" + orderId + "';");

        addToCassandraQueryQueue(
            getCassandraConnection()
                .prepare(CQL_Querys.selectAllLineItems(keyspaceName))
                .bind(orderId));

        addToCassandraQueryQueue(
            getCassandraConnection()
                .prepare(CQL_Querys.selectAllOrders(keyspaceName))
                .bind(orderId));
    }


}
