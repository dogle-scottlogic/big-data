package showdown;

import Conveyor.Conveyor;
import com.datastax.driver.core.ConsistencyLevel;
import dataGenerator.data_handlers.Settings;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.Combo;
import storers.storers.Timer;
import storers.storers.maria.enums.DBType;

import java.io.IOException;

/**
 * Created by dogle on 08/12/2016.
 */
public class RandomEventTest {
    private final static Logger LOG = Logger.getLogger(RandomEventTest.class);
    private static final int NUMBER_OF_EVENTS = 50000;
    private static final int ORDER_CACHE_SIZE = 5000;
    private CSVLogger log;

    @Before
    public void setUp() throws IOException {
        log = new CSVLogger(true);
    }

    @Test
    public void FiftyThousandRandomEvents() throws Exception {
        String fileName = "FiftyThousandTotalRandomEvents";

        ConsistencyLevel consistencyLevel = ConsistencyLevel.ONE;
        int replicationFactor = 1;

        Combo cassandraCombo = new Combo(log, DBType.CASSANDRA, consistencyLevel, replicationFactor);
        initialiseAndRunEvents(fileName, cassandraCombo, DBType.CASSANDRA, "5 Nodes");

        Combo mariaCombo = new Combo(log, DBType.MARIA_DB);
        initialiseAndRunEvents(fileName, mariaCombo, DBType.MARIA_DB, "5 Nodes, 1 replica, 5 fragments");
    }

    @Test
    public void CassandraReplicationFactor() throws Exception {
        String fileName = "CassandraReplicationFactor";

        int maxReplication = 5;
        for (int i = 1; i <= maxReplication; i++) {
            Combo cassandraCombo = new Combo(log, DBType.CASSANDRA, ConsistencyLevel.ONE, i, 1);
            initialiseAndRunEvents(fileName, cassandraCombo, DBType.CASSANDRA, Integer.toString(i));
        }

        Combo mariaCombo = new Combo(log, DBType.MARIA_DB);
        initialiseAndRunEvents(fileName, mariaCombo, DBType.MARIA_DB, "ALL");
    }

    @Test
    public void CassandraConsistencyLevel() throws Exception {
        String fileName = "CassandraConsistencyLevel";

        ConsistencyLevel[] consistencyValuesToTest= {ConsistencyLevel.ANY, ConsistencyLevel.ONE, ConsistencyLevel.TWO, ConsistencyLevel.THREE, ConsistencyLevel.QUORUM, ConsistencyLevel.ALL};
        for (ConsistencyLevel level: consistencyValuesToTest) {
            Combo cassandraCombo = new Combo(log, DBType.CASSANDRA, level, 3, 1);
            initialiseAndRunEvents(fileName, cassandraCombo, DBType.CASSANDRA, level.toString());
        }

        Combo mariaCombo = new Combo(log, DBType.MARIA_DB);
        initialiseAndRunEvents(fileName, mariaCombo, DBType.MARIA_DB, "ALL");
    }

    private void initialiseAndRunEvents(String fileName, Combo combo, DBType dbType, String id) throws IOException, ParseException {
        Settings.setStringSetting("EVENT_GEN_MODE", "fixed");
        Settings.setIntSetting("ORDER_CACHE_SIZE", ORDER_CACHE_SIZE);
        EventGenerator eventGenerator = insertInitialData(dbType, combo);
        runRandomEvents(dbType, eventGenerator, combo, fileName, id);
    }

    private void runRandomEvents(DBType type, EventGenerator eventGenerator, Combo combo, String fileName, String id) throws IOException, ParseException {
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        CSVLogger csvLogger = new CSVLogger(fileName, id);
        combo.setLogger(csvLogger);
        combo.reinitThreadPool();
        eventGenerator.setEvents(new Enums.EventType[]{ });

        Timer t = new Timer();
        LOG.info("Starting running " + NUMBER_OF_EVENTS + " random events into " + type + " database.");
        t.startTimer();
        Conveyor.processEvents(NUMBER_OF_EVENTS, combo, eventGenerator);
        LOG.info("Completed processing " + type + " events in " + t.stopTimer() + "ns");
    }

    private EventGenerator insertInitialData(DBType type, Combo combo) throws IOException, ParseException {
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});
        LOG.info("Starting inserting " + ORDER_CACHE_SIZE + " orders into " + type + " database.");
        Conveyor.processEvents(ORDER_CACHE_SIZE, combo, eventGenerator);
        return eventGenerator;
    }
}
