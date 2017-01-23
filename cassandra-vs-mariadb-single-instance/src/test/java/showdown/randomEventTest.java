package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.*;
import storers.storers.maria.enums.DBType;

import java.io.File;

/**
 * Created by dogle on 08/12/2016.
 */
public class randomEventTest {
    @Test
    public void FiftyThousandTotalRandomEvents() throws Exception {
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");
        int numOfEvents = 50000;
        Timer t = new Timer();

        EventGenerator eventGenerator;
        Settings.setIntSetting("ORDER_CACHE_SIZE", 5000);
        CSVLogger log = new CSVLogger(true);
        Combo cs = new Combo(log, DBType.CASSANDRA);
        Combo ms = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(5000, cs, eventGenerator);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(5000, ms, eventGenerator);


        // COMBO!!
        log = new CSVLogger(absPath, "FiftyThousandTotalRandomEvents");
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        cs.setLogger(log);
        cs.reinitThreadPool();
        ms.setLogger(log);
        ms.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{ });

        t.startTimer();
        Conveyor.processEvents(numOfEvents, cs, eventGenerator);
        System.out.println(t.stopTimer());
        t.startTimer();
        Conveyor.processEvents(numOfEvents, ms, eventGenerator);
        System.out.println(t.stopTimer());
    }
}
