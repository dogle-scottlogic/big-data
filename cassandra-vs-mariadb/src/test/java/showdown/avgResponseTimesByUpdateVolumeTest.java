package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import org.junit.Test;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

import static org.junit.Assert.*;

/**
 * Created by lcollingwood on 12/12/2016.
 */
public class avgResponseTimesByUpdateVolumeTest {
    public void avgResponseTimesByUpdateVolumeTestHelper(int nUpdates, Enums.EventType updateType) throws Exception {
        int nCreates = 5000;

        storers.storers.Timer timer = new storers.storers.Timer();

        CassandraDBStorer cdbs;
        MariaDBStorer mdbs;

        // Do not log
        // Cassandra
        cdbs = new CassandraDBStorer();
        Conveyor.setEvents(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.initialiseEventsGenerator();
        Conveyor.processEventsWithoutLog(nCreates, cdbs);

        Conveyor.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEventsWithLog(nUpdates, cdbs,  nUpdates +"UpdatesAgainst5000Records");

        //Maria
//        mdbs = new MariaDBStorer();
//        Conveyor.setEvents(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.initialiseEventsGenerator();
//        Conveyor.processEventsWithoutLog(nCreates, mdbs);
//
//        Conveyor.setEvents(new Enums.EventType[]{updateType});
//        Conveyor.processEventsWithLog(nUpdates, mdbs, nUpdates +"UpdatesAgainst5000Records");
    }

    public void testAllVolumes(Enums.EventType updateType) throws Exception {
        avgResponseTimesByUpdateVolumeTestHelper(1000, updateType);
        avgResponseTimesByUpdateVolumeTestHelper(2000, updateType);
        avgResponseTimesByUpdateVolumeTestHelper(3000, updateType);
        avgResponseTimesByUpdateVolumeTestHelper(4000, updateType);
    }

    @Test
    public void updateStatusTest() throws Exception {
        testAllVolumes(Enums.EventType.UPDATE_STATUS);
    }

    @Test
    public void updateTest() throws Exception {
        testAllVolumes(Enums.EventType.UPDATE_STATUS);
    }
}