package storers.storers.combo;

import com.datastax.driver.core.Session;
import storers.CSVLogger;
import storers.storers.Order;
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
    }

    public void addToBatch(Order order) throws SQLException {
        // Do nothing.
    }


}
