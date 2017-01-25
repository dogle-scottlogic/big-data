package storers.storers.combo;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.Combo;
import storers.storers.maria.enums.DBType;

/**
 * Created by lcollingwood on 14/12/2016.
 */
public class CreateTest {
    private final static Logger LOG = Logger.getLogger(CreateTest.class);

    @Test
    public void ten() throws Exception {
        int numOfEvents = 10;

        EventGenerator eventGenerator;

        // COMBO!!
        CSVLogger log = new CSVLogger("ten");
        Combo storer = new Combo(log, DBType.CASSANDRA);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        LOG.info("Start Cassandra");
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);

        storer = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        LOG.info("Start MariaDB");
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }

    @Test
    public void updateTen() throws Exception {
        int numOfEvents = 100;

        EventGenerator eventGenerator;

        // COMBO!!
        CSVLogger log = new CSVLogger(true);
        Combo storer = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);

        log = new CSVLogger("ten");
        storer = new Combo(log, DBType.CASSANDRA);
        eventGenerator.setEvents(new Enums.EventType[]{Enums.EventType.UPDATE});
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }

    @Test
    public void deleteTen() throws Exception {
        int numOfEvents = 10;

        EventGenerator eventGenerator;

        // COMBO!!
        CSVLogger log = new CSVLogger(true);
        Combo storer = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE});
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);

        log = new CSVLogger("ten");
        storer = new Combo(log, DBType.MARIA_DB);
        eventGenerator.setEvents(new Enums.EventType[]{Enums.EventType.DELETE});
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }

    @Test
    public void readTen() throws Exception {
        int numOfEvents = 100;

        EventGenerator eventGenerator;

        // COMBO!!
        CSVLogger log = new CSVLogger("ten");
        Combo storer = new Combo(log, DBType.MARIA_DB);
        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{ Enums.EventType.CREATE, Enums.EventType.READ });
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }
}