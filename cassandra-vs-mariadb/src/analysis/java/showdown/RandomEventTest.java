package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.Combo;
import storers.storers.Timer;
import storers.storers.maria.enums.DBType;

/**
 * Created by dogle on 08/12/2016.
 */
public class RandomEventTest {
    private final static Logger LOG = Logger.getLogger(RandomEventTest.class);
    @Test
    public void FiftyThousandTotalRandomEvents() throws Exception {
        int numOfEvents = 50000;
        Timer t = new Timer();

        EventGenerator eventGenerator;
        int orderCacheSize = 5000;
        Settings.setIntSetting("ORDER_CACHE_SIZE", orderCacheSize);
        CSVLogger log = new CSVLogger(true);
        Combo cs = new Combo(log, DBType.CASSANDRA);
        Combo ms = new Combo(log, DBType.MARIA_DB);
        LOG.info("Starting inserting " + orderCacheSize + " orders into Cassandra database.");
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(orderCacheSize, cs, eventGenerator);
        LOG.info("Starting inserting " + orderCacheSize + " orders into Maria database.");
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(orderCacheSize, ms, eventGenerator);


        // COMBO!!
        log = new CSVLogger("FiftyThousandTotalRandomEvents");
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        cs.setLogger(log);
        cs.reinitThreadPool();
        ms.setLogger(log);
        ms.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{ });

        LOG.info("Starting running " + numOfEvents + " random events into Cassandra database.");
        t.startTimer();
        Conveyor.processEvents(numOfEvents, cs, eventGenerator);
        LOG.info("Completed processing Cassandra events in " + t.stopTimer() + "ns");
        LOG.info("Starting running " + numOfEvents + " random events into Maria database.");
        t.startTimer();
        Conveyor.processEvents(numOfEvents, ms, eventGenerator);
        LOG.info("Completed processing Maria DB events in " + t.stopTimer() + "ns");
    }
}
