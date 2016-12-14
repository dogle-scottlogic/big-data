package storers.storers.combo;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.Combo;
import storers.storers.MariaDBStorer;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class CreateTest {

    @Test
    public void ten() throws Exception {
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");
        int numOfEvents = 10000;

        EventGenerator eventGenerator;

        // COMBO!!
        CSVLogger log = new CSVLogger(absPath, "ten");
        Combo storer = new Combo(log);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }
}