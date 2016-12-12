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
        int nCreates = 1000;

        storers.storers.Timer timer = new storers.storers.Timer();

        CassandraDBStorer cdbs;
        MariaDBStorer mdbs;

        // Do not log
        // Cassandra
        cdbs = new CassandraDBStorer();
        Conveyor.setEvents(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.initialiseEventsGenerator();
        Conveyor.processEventsWithoutLog(nCreates, cdbs);

        timer.startTimer();
        Conveyor.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEventsWithoutLog(nUpdates, cdbs);
        System.out.println("Cassandra Avg Update-Status Time of " + nUpdates + " Updates: " + timer.stopTimer() / nUpdates);

        //Maria
        mdbs = new MariaDBStorer();
        Conveyor.setEvents(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.initialiseEventsGenerator();
        Conveyor.processEventsWithoutLog(nCreates, mdbs);

        timer.startTimer();
        Conveyor.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEventsWithoutLog(nUpdates, mdbs);
        System.out.println("MariaDB Avg Update-Status Time: " + timer.stopTimer() / nUpdates);
    }

    public void testAllVolumes(Enums.EventType updateType) throws Exception {
        avgResponseTimesByUpdateVolumeTestHelper(10000, updateType);
        avgResponseTimesByUpdateVolumeTestHelper(20000, updateType);
        avgResponseTimesByUpdateVolumeTestHelper(30000, updateType);
        avgResponseTimesByUpdateVolumeTestHelper(40000, updateType);
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