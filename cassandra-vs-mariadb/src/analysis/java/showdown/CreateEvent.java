package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by dogle on 08/12/2016.
 */
public class CreateEvent {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void Create500() throws SQLException, IOException, ParseException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        int numOfEvents = 500;
        CSVLogger logger = new CSVLogger("Create500");
        logger.setTestID("Cassandra_500");


        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer(logger);
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator);

        //Maria
        logger.setTestID("Maria_500");
        MariaDBStorer mdbs = new MariaDBStorer(true, logger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);
    }

    @Test
    public void Create1000() throws SQLException, IOException, ParseException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        int numOfEvents = 1000;
        CSVLogger logger = new CSVLogger("Create1000");
        logger.setTestID("Cassandra_1000");


        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer(logger);
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator);

        //Maria
        logger.setTestID("Maria_1000");
        MariaDBStorer mdbs = new MariaDBStorer(true, logger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);
    }

    @Test
    public void Create10000() throws SQLException, IOException, ParseException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        int numOfEvents = 10000;
        CSVLogger logger = new CSVLogger("Create10000");
        logger.setTestID("Cassandra_10000");


        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer(logger);
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, cdbs, eventGenerator);

        //Maria
        logger.setTestID("Maria_10000");
        MariaDBStorer mdbs = new MariaDBStorer(true, logger);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, mdbs, eventGenerator);
    }
}
