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
import storers.storers.Combo;
import storers.storers.MariaDBStorer;
import storers.storers.Storer;
import storers.storers.maria.enums.DBType;

import java.io.File;

/**
 * Created by dogle on 08/12/2016.
 */
public class randomEventTest {
    @Test
    public void TenThousandTotalRandomEvents() throws Exception {
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        int numOfEvents = 50000;

        EventGenerator eventGenerator;

        // COMBO!!
        CSVLogger log = new CSVLogger(absPath, "TenThousandRandomEvents");
        Combo storer = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ });
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);

        storer = new Combo(log, DBType.CASSANDRA);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ });
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }
}
