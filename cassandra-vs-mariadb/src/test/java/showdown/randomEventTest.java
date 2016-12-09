package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;
import storers.storers.Storer;

/**
 * Created by dogle on 08/12/2016.
 */
public class RandomEventTest {
    // Helpers
    public void nRandomEvents(int n, Storer storer, String logName) {
        Conveyor.processEventsWithLog(n, new Enums.EventType[]{}, storer, logName);
    }

    public void preSeed(int n, Storer storer) {
        Conveyor.processEventsWithoutLog(n, new Enums.EventType[]{ Enums.EventType.CREATE }, storer);
    }

    public void nEventsWithNPreseeded(String label, Storer storer, int nEvents, int nPreseeded) {
        Settings.setIntSetting("SEED", 12345);
        preSeed(nPreseeded, storer);

        Settings.setIntSetting("SEED", 56789);
        nRandomEvents(10000, storer, nEvents +"EventsWith" + nPreseeded + "Preseeded" + label);
    }

    @Before
    public void setUp() throws Exception {    }

    @After
    public void tearDown() throws Exception {    }

    @Test
    public void TenThousandTotalRandomEvents() throws Exception {
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        int numOfEvents = 10000;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, cdbs, "TenThousandRandomEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, mdbs, "TenThousandRandomEventsMariaDB");
    }

    @Test
    public void TenThousandEventsAgainstVaryingWorkingSetSizes() throws Exception {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        Settings.setIntSetting("ORDER_CACHE_SIZE", 1000);

        CassandraDBStorer cassandra = new CassandraDBStorer();
        MariaDBStorer maria = new MariaDBStorer();

        // Not pre-seeded
        nEventsWithNPreseeded("Maria", maria, 10000, 0);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 0);

        // Pre Seeded with 10,000 events
        nEventsWithNPreseeded("Maria", maria, 10000, 10000);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 10000);

        // Pre Seeded with 20,000 events
        nEventsWithNPreseeded("Maria", maria, 10000, 20000);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 20000);

        // Pre Seeded with 30,000 events
        nEventsWithNPreseeded("Maria", maria, 10000, 30000);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 30000);
    }

}
