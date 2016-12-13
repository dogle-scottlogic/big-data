package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

import java.io.File;

/**
 * Created by lcollingwood on 12/12/2016.
 */
public class avgResponseTimesByUpdateVolumeTest {
    public void avgResponseTimesByUpdateVolumeTestHelper(int nUpdates, Enums.EventType updateType) throws Exception {
        int nCreates = 5000;

        storers.storers.Timer timer = new storers.storers.Timer();

        String absPath = new File("").getAbsolutePath().concat("\\testLogs");


        CassandraDBStorer cdbs;
        MariaDBStorer mdbs;

        // Do not log
        // Cassandra
        CSVLogger cassandraLogger = new CSVLogger(absPath, "cassandraUpdateVolumeTest");
        cdbs = new CassandraDBStorer(cassandraLogger);
        //Conveyor.setEvents();
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(nCreates, cdbs, eventGenerator);

        eventGenerator.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEvents(nUpdates, cdbs, eventGenerator);


        //Maria
        CSVLogger mariaLogger = new CSVLogger(absPath, "mariaUpdateVolumeTest");
        mdbs = new MariaDBStorer(true, mariaLogger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(nCreates, mdbs, eventGenerator);

        eventGenerator.setEvents(new Enums.EventType[]{updateType});
        Conveyor.processEvents(nUpdates, mdbs, eventGenerator);
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
        testAllVolumes(Enums.EventType.UPDATE);
    }
}