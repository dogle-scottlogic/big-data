package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

import java.sql.SQLException;

/**
 * Created by dogle on 08/12/2016.
 */
public class CreateEvent {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void Create500() throws SQLException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        int numOfEvents = 500;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, cdbs, "fiveHundredCreateEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, mdbs, "fiveHundredCreateEventsMariaDB");
    }

    @Test
    public void Create1000() throws SQLException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        int numOfEvents = 1000;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, cdbs, "OneThousandCreateEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, mdbs, "OneThousandCreateEventsMariaDB");
    }

    @Test
    public void Create10000() throws SQLException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        int numOfEvents = 10000;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, cdbs, "TenThousandCreateEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, mdbs, "TenThousandCreateEventsMariaDB");
    }
}
