package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;
/**
 * Created by dogle on 08/12/2016.
 */
public class randomEventTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void FiveHundredTotalRandomEvents() throws Exception {
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        int numOfEvents = 500;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, cdbs, "fiveHundredRandomEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, mdbs, "fiveHundredRandomEventsMariaDB");

    }

    @Test
    public void FiveTotalFixedEvents() throws Exception {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        Settings.setIntSetting("ORDER_CACHE_SIZE", 2500);
        Settings.setIntSetting("NUM_FIXED_EVENTS", 500);
        int numOfEvents = 2500;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, cdbs, "fiveHundredFixedEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, mdbs, "fiveHundredFixedEventsMariaDB");
    }

}
