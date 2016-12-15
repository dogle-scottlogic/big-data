package storers.storers.combo;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import storers.CSVLogger;
import storers.storers.Order;
import storers.storers.cassandra.CQL_Querys;
import storers.storers.maria.enums.DBEventType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by dogle on 15/12/2016.
 */
public class Update extends ComboQuery {

    public Update(Session cassandraConnection, Connection mariaConnection, CSVLogger logger, Order order) throws SQLException {
        super(cassandraConnection, mariaConnection, logger, DBEventType.UPDATE);
        addToBatch(order);
    }

    public void addToBatch(Order order) throws SQLException {
        String keyspaceName = getCassandraConnection().getLoggedKeyspace();
        BatchStatement batchStatement = new BatchStatement();

        getMariaBatch().addBatch("UPDATE orders.`order` " +
                "SET " +
                "client_id='" + order.getClientId() + "', " +
                "created='" + Long.valueOf(order.getDate()).toString() + "' " +
                "WHERE id='" + order.getOrderId() + "';");

        for (int i = 0; i < order.getLineItems().size(); i++) {
            HashMap<String, String> lineItem = order.getLineItems().get(i);
            String lineItemId = lineItem.get("id");
            int quantity = Integer.parseInt(lineItem.get("quantity"));
            double linePrice = Double.parseDouble(lineItem.get("linePrice"));
            PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.updateLineItem(keyspaceName));
            batchStatement.add(p.bind(quantity, linePrice, lineItemId, order.getOrderId()));
            // Maria
            getMariaBatch().addBatch("UPDATE orders.line_item " +
                    "SET " +
                    "order_id='" + order.getOrderId() + "', " +
                    "product_id='" + lineItem.get("productId") + "', " +
                    "quantity=" + lineItem.get("quantity") + " " +
                    "WHERE order_id='" + order.getOrderId() + "';");
        }
        PreparedStatement p = getCassandraConnection().prepare(CQL_Querys.updateOrder(keyspaceName));
        batchStatement.add(p.bind(order.getDate(), order.getStatus(), order.getSubTotal(), order.getOrderId()));
    }
}
