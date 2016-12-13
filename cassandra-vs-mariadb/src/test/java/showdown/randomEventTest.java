package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;
import storers.storers.Storer;

import java.io.File;

/**
 * Created by dogle on 08/12/2016.
 */
public class RandomEventTest {
    // Helpers
    public void nRandomEvents(int n, Storer storer, String logName) {
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(n, storer, eventGenerator, logName);
    }

    public void preSeed(int n, Storer storer) {
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(n, storer, eventGenerator);
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
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");
        CSVLogger logger = null;
        logger = new CSVLogger(absPath, "TenThousandRandomEventsCassandra");


        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        int numOfEvents = 10000;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer(logger);
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{});
        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator, "TenThousandRandomEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator, "TenThousandRandomEventsMariaDB");
    }

    @Test
    public void TenThousandEventsAgainstVaryingWorkingSetSizes() throws Exception {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        Settings.setIntSetting("ORDER_CACHE_SIZE", 1000);

        CassandraDBStorer cassandra;
        MariaDBStorer maria;

        // Not pre-seeded
        cassandra = new CassandraDBStorer();
        maria = new MariaDBStorer();

        nEventsWithNPreseeded("Maria", maria, 10000, 0);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 0);

        // Pre Seeded with 10,000 events
        cassandra = new CassandraDBStorer();
        maria = new MariaDBStorer();

        nEventsWithNPreseeded("Maria", maria, 10000, 10000);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 10000);

        // Pre Seeded with 20,000 events
        cassandra = new CassandraDBStorer();
        maria = new MariaDBStorer();

        nEventsWithNPreseeded("Maria", maria, 10000, 20000);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 20000);

        // Pre Seeded with 30,000 events
        nEventsWithNPreseeded("Maria", maria, 10000, 30000);
        nEventsWithNPreseeded("Cassandra", cassandra, 10000, 30000);
    }
}
