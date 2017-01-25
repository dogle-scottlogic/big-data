package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.Combo;
import storers.storers.maria.enums.DBType;

/**
 * Created by lcollingwood on 16/12/2016.
 */
public class ResponseTimesByTableSize {
    private void testHelper(int nCreates, int nUpdates, Enums.EventType updateType, CSVLogger logger) throws Exception {
        EventGenerator eventGenerator;

        // Init
        Settings.setIntSetting("ORDER_CACHE_SIZE", nCreates);
        CSVLogger log = new CSVLogger(true);
        Combo cs = new Combo(log, DBType.CASSANDRA);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(nCreates, cs, eventGenerator);
        cs.setLogger(logger);
        cs.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEvents(nUpdates, cs, eventGenerator);

        Combo ms = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(nCreates, ms, eventGenerator);
        ms.setLogger(logger);
        ms.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEvents(nUpdates, ms, eventGenerator);
    }

    private void testAllVolumes(Enums.EventType updateType) throws Exception {
        CSVLogger log = new CSVLogger("updateResponseByTableSize");

        for(int i = 1; i < 21; i++) {
            log.setTestID(Integer.toString(i * 2000));
            testHelper((i * 2000), 10000, updateType, log);
        }
    }

    @Test
    public void updateStatusTest() throws Exception {
        testAllVolumes(Enums.EventType.UPDATE_STATUS);
    }

    @Test
    public void updateTest() throws Exception {
        testAllVolumes(Enums.EventType.UPDATE);
    }
}
