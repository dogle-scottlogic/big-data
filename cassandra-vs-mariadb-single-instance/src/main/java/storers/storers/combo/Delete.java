package storers.storers.combo;

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
 * Created by dogle on 15/12/2016.
 */
public class Delete extends ComboQuery {

    public Delete(Session cassandraConnection, Connection mariaConnection, CSVLogger logger, Order order, DBType type) throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.DELETE, type);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {

        if (getDbtype() == DBType.CASSANDRA) {
            String keyspaceName = getCassandraConnection().getLoggedKeyspace();
            PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.deleteLineItem(keyspaceName));
            getCassandraBatch().add(p.bind(order.getOrderId()));
            p = getCassandraConnection().prepare(CQL_Querys.deleteOrder(keyspaceName));
            getCassandraBatch().add(p.bind(order.getOrderId()));
        }

        if (getDbtype() == DBType.MARIA_DB) {
            // Maria
            getMariaBatch().addBatch(deleteString("orders.`line_item`", "order_id", order.getOrderId()));
            getMariaBatch().addBatch(deleteString("orders.`order`", "id", order.getOrderId()));
        }
    }

    private String deleteString(String table, String idField, String orderId) {
        return "DELETE FROM " + table + " WHERE " + idField + "='" + orderId + "';";
    }
}
