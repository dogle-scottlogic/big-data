package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

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
        int numOfEvents = 100;

        storers.storers.Timer timer = new storers.storers.Timer();
        timer.startTimer();
        EventGenerator eventGenerator;

//        // Cassandra
//        CassandraDBStorer cdbs = new CassandraDBStorer();
//        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator);
//
//        System.out.println("Cassandra: " + timer.stopTimer());


        CSVLogger dummyLogger = new CSVLogger(true);

        //Maria
        MariaDBStorer mdbs;

        // Sync
        timer.startTimer();
        mdbs = new MariaDBStorer(false, dummyLogger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);
        System.out.println("MariaDB Sync: " + timer.stopTimer());

        // A-Sync
        timer.startTimer();
        mdbs = new MariaDBStorer(true, dummyLogger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);
        System.out.println("MariaDB A-Sync: " + timer.stopTimer());
    }

//    @Test
//    public void loggingDoesNotDetrimentTesting() throws Exception {
//        int numOfEvents = 10000;
//
//        storers.storers.Timer timer = new storers.storers.Timer();
//
//        CassandraDBStorer cdbs;
//        MariaDBStorer mdbs;
//        // Do not log
//        // Cassandra
//        timer.startTimer();
//        cdbs = new CassandraDBStorer();
//        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator);
//
//        long cassandraUnlogged = timer.stopTimer();
//        System.out.println("Cassandra Unlogged Time: " + cassandraUnlogged);
//
//        //Maria
//        timer.startTimer();
//        mdbs = new MariaDBStorer(true, "\\testLogs", "testtestest");
//        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);
//        long mariaUnlogged = timer.stopTimer();
//        System.out.println("MariaDB Unlogged Time: " + mariaUnlogged);
//
//        // Log
//        // Cassandra
//        timer.startTimer();
//        cdbs = new CassandraDBStorer();
//        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator, "timingTestCassandra");
//        long cassandraLogged = timer.stopTimer();
//        System.out.println("Cassandra Logged Time: " + cassandraLogged);
//
//        //Maria
//        timer.startTimer();
//        mdbs = new MariaDBStorer(true, "\\testLogs", "testtestest");
//        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
//        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator, "timingTestMaria");
//        long mariaLogged = timer.stopTimer();
//        System.out.println("MariaDB Logged Time:" + mariaLogged);
//
//        // Summary
//        System.out.println("Cassandra Log Effect: " + (cassandraLogged - cassandraUnlogged));
//        System.out.println("Maria Log Effect: " + (mariaLogged - mariaUnlogged));
//    }
}
