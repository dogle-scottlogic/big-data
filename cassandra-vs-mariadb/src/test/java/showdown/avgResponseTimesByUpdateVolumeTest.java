package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;
import storers.storers.Timer;

import java.io.File;

/**
 * Created by lcollingwood on 12/12/2016.
 */
public class avgResponseTimesByUpdateVolumeTest {
    public void avgResponseTimesByUpdateVolumeTestHelper(int nUpdates, Enums.EventType updateType) throws Exception {
        int nCreates = 50;
        Timer timer = new Timer();
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");


        CassandraDBStorer cdbs;
        MariaDBStorer mdbs;

        // Do not log
        // Cassandra
//        CSVLogger cassandraLogger = new CSVLogger(true);
//        cdbs = new CassandraDBStorer(cassandraLogger);
//        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(nCreates, cdbs, eventGenerator);
//
//        cassandraLogger = new CSVLogger(absPath, "cassandraUpdateVolumeTest_" + Integer.toString(nUpdates)  + updateType.name());
//        cdbs.setLogger(cassandraLogger);
//        eventGenerator.setEvents(new Enums.EventType[]{updateType});
//        timer.startTimer();
//        Conveyor.processEvents(nUpdates, cdbs, eventGenerator);
//        System.out.println("cassandraUpdateVolumeTest_" + Integer.toString(nUpdates) + " total time: " + Long.toString(timer.stopTimer()));
//
//
//        //Maria
//        CSVLogger mariaLogger = new CSVLogger(true);
//        mdbs = new MariaDBStorer(true, mariaLogger);
//        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(nCreates, mdbs, eventGenerator);
//
//        mariaLogger = new CSVLogger(absPath, "mariaUpdateVolumeTest_" + Integer.toString(nUpdates)  + updateType.name());
//        mdbs.setLogger(mariaLogger);
//        eventGenerator.setEvents(new Enums.EventType[]{updateType});
//        timer.startTimer();
//        Conveyor.processEvents(nUpdates, mdbs, eventGenerator);
//        System.out.println("mariaUpdateVolumeTest_" + Integer.toString(nUpdates) + " total time: " + Long.toString(timer.stopTimer()));
    }

    public void testAllVolumes(Enums.EventType updateType) throws Exception {
        Timer t = new Timer();
        t.startTimer();
        avgResponseTimesByUpdateVolumeTestHelper(1000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(2000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(3000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(4000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(5000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(6000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(7000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(8000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(9000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(10000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(11000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(12000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(13000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(14000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(15000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(16000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(17000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(18000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(19000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(20000, updateType);
//        avgResponseTimesByUpdateVolumeTestHelper(21000, updateType);
        System.out.println("Update Test total time: " + Long.toString(t.stopTimer()));
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