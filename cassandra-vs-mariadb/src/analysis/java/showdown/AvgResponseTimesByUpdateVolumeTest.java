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
 * Created by lcollingwood on 12/12/2016.
 */
public class AvgResponseTimesByUpdateVolumeTest {
    private void avgResponseTimesByUpdateVolumeTestHelper(int nUpdates, Enums.EventType updateType, CSVLogger logger) throws Exception {
        EventGenerator eventGenerator;

        // Init
        Settings.setIntSetting("ORDER_CACHE_SIZE", 5000);
        CSVLogger log = new CSVLogger(true);
        Combo cs = new Combo(log, DBType.CASSANDRA);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(5000, cs, eventGenerator);
        cs.setLogger(logger);
        cs.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEvents(nUpdates, cs, eventGenerator);

        Combo ms = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(5000, ms, eventGenerator);
        ms.setLogger(logger);
        ms.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEvents(nUpdates, ms, eventGenerator);
    }

    private void testAllVolumes(Enums.EventType updateType) throws Exception {
        CSVLogger log = new CSVLogger("UpdateTests_f");

        log.setTestID(Integer.toString(100000));
        avgResponseTimesByUpdateVolumeTestHelper(100000, updateType, log);

        log.setTestID(Integer.toString( 200000));
        avgResponseTimesByUpdateVolumeTestHelper(200000, updateType, log);

        log.setTestID(Integer.toString(300000));
        avgResponseTimesByUpdateVolumeTestHelper(300000, updateType, log);

        log.setTestID(Integer.toString(400000));
        avgResponseTimesByUpdateVolumeTestHelper(400000, updateType, log);
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