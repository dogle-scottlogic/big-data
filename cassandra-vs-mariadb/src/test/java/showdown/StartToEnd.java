package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.Combo;
import storers.storers.MariaDBStorer;
import storers.storers.maria.enums.DBType;

import java.io.File;

/**
 * Created by dogle on 09/12/2016.
 */
public class StartToEnd {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }
    @Test
    public void end2end10000() throws Exception {
        int numOfEvents = 10000;

        storers.storers.Timer timer = new storers.storers.Timer();
        timer.startTimer();
        EventGenerator eventGenerator;
        CSVLogger dummyLogger = new CSVLogger(true);

        //Maria
        Combo combo;
        System.out.println("DB, Connection Pool Max Size, Total Time in Nanoseconds, Number of Operations,");

        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});

        for (int i = 1; i < 50; i++) {
            for (int j = 0; j < 5; j++) {
                combo = new Combo(dummyLogger, DBType.MARIA_DB, i);
                timer.startTimer();
                Conveyor.processEvents(numOfEvents, combo, eventGenerator);
                System.out.println("MariaDB, " + i + ", " + timer.stopTimer() + ", " + numOfEvents + ",");
            }
        }


    }
}
