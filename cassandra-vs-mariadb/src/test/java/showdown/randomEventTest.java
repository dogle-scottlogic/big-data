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
    @Test
    public void TenThousandTotalRandomEvents() throws Exception {
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        int numOfEvents = 10000;

        EventGenerator eventGenerator;

        //Maria
        boolean useASync = false;
        CSVLogger mariaLogger = new CSVLogger(absPath, "TenThousandRandomEventsMaria");
        MariaDBStorer mdbs = new MariaDBStorer(useASync, mariaLogger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);

        // Cassandra
        CSVLogger cassandraLogger = new CSVLogger(absPath, "TenThousandRandomEventsCassandra");
        CassandraDBStorer cdbs = new CassandraDBStorer(cassandraLogger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{});
        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator);
    }
}
