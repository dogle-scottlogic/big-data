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
        int numOfEvents = 50000;


        EventGenerator eventGenerator;

        CSVLogger log = new CSVLogger(true);
        Combo cs = new Combo(log, DBType.CASSANDRA);
        Combo ms = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(500, cs, eventGenerator);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(500, ms, eventGenerator);


        // COMBO!!
        log = new CSVLogger(absPath, "TenThousandRandomEvents");
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        cs = new Combo(log, DBType.CASSANDRA);
        ms = new Combo(log, DBType.MARIA_DB);
        eventGenerator.setEvents(new Enums.EventType[]{ });

        Conveyor.processEvents(numOfEvents, cs, eventGenerator);
        Conveyor.processEvents(numOfEvents, ms, eventGenerator);
    }
}
