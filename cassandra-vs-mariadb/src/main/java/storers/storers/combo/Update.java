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
import java.util.HashMap;

/**
 * Created by dogle on 15/12/2016.
 */
public class Update extends ComboQuery {

    public Update(Session cassandraConnection, PreparedStatement orderPreparedStatement, PreparedStatement lineItemPreparedStatement, Connection mariaConnection, CSVLogger logger, Order order, DBType type) throws SQLException {
        super(cassandraConnection, orderPreparedStatement, lineItemPreparedStatement, mariaConnection, logger, DBEventType.UPDATE, type);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {

        if (getDbtype() == DBType.MARIA_DB) {
            getMariaBatch().addBatch("UPDATE orders.`order` " +
                    "SET " +
                    "client_id='" + order.getClientId() + "', " +
                    "created='" + order.getDate() + "' " +
                    "WHERE id='" + order.getOrderId() + "';");
        }

        for (int i = 0; i < order.getLineItems().size(); i++) {
            HashMap<String, String> lineItem = order.getLineItems().get(i);
            addLineItemToBatch(lineItem, order);
        }

        if (getDbtype() == DBType.CASSANDRA) {
            getCassandraBatch().add(getOrderPreparedStatement().bind(order.getDate(), order.getStatus(), order.getSubTotal(), order.getOrderId()));
        }
    }

    public void addLineItemToBatch(HashMap<String, String> lineItem, Order order) throws SQLException {
        String lineItemId = lineItem.get("id");
        int quantity = Integer.parseInt(lineItem.get("quantity"));
        double linePrice = Double.parseDouble(lineItem.get("linePrice"));

        if (getDbtype() == DBType.CASSANDRA) {
            getCassandraBatch().add(getLineItemPreparedStatement().bind(quantity, linePrice, lineItemId, order.getOrderId()));
        }

        // Maria
        if (getDbtype() == DBType.MARIA_DB) {
            getMariaBatch().addBatch("UPDATE orders.line_item " +
                    "SET " +
                    "order_id='" + order.getOrderId() + "', " +
                    "product_id='" + lineItem.get("productId") + "', " +
                    "quantity=" + lineItem.get("quantity") + " " +
                    "WHERE order_id='" + order.getOrderId() + "'" +
                    "AND id='" + lineItemId + "';");
        }
    }
}
