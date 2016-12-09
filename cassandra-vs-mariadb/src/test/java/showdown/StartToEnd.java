package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

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


        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithoutLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, cdbs);

        System.out.println("Cassandra: " + timer.stopTimer());
        timer.startTimer();

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithoutLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, mdbs);
        System.out.println("MariaDB " + timer.stopTimer());
    }

    @Test
    public void loggingDoesNotDetrimentTesting() throws Exception {
        int numOfEvents = 10000;

        storers.storers.Timer timer = new storers.storers.Timer();

        CassandraDBStorer cdbs;
        MariaDBStorer mdbs;
        // Do not log
        // Cassandra
        timer.startTimer();
        cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithoutLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, cdbs);

        long cassandraUnlogged = timer.stopTimer();
        System.out.println("Cassandra Unlogged Time: " + cassandraUnlogged);

        //Maria
        timer.startTimer();
        mdbs = new MariaDBStorer();
        Conveyor.processEventsWithoutLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, mdbs);
        long mariaUnlogged = timer.stopTimer();
        System.out.println("MariaDB Unlogged Time: " + mariaUnlogged);

        // Log
        // Cassandra
        timer.startTimer();
        cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, cdbs, "timingTestCassandra");
        long cassandraLogged = timer.stopTimer();
        System.out.println("Cassandra Logged Time: " + cassandraLogged);

        //Maria
        timer.startTimer();
        mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{Enums.EventType.CREATE}, mdbs, "timingTestMaria");
        long mariaLogged = timer.stopTimer();
        System.out.println("MariaDB Logged Time:" + mariaLogged);

        // Summary
        System.out.println("Cassandra Log Effect: " + (cassandraLogged - cassandraUnlogged));
        System.out.println("Maria Log Effect: " + (mariaLogged - mariaUnlogged));

    }
}
