package showdown;

import Conveyor.Conveyor;
import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.enums.Enums;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 08/12/2016.
 */
public class randomEventTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void FiveHundredTotalRandomEvents() throws Exception {
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        int numOfEvents = 500;

        // Cassandra
        CassandraDBStorer cdbs = new CassandraDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, cdbs, "fiveHundredRandomEventsCassandra");

        //Maria
        MariaDBStorer mdbs = new MariaDBStorer();
        Conveyor.processEventsWithLog(numOfEvents, new Enums.EventType[]{}, mdbs, "fiveHundredRandomEventsMariaDB");

    }

    @Test
    public void FiveHundredCreateEvents() throws Exception {
//        int numClients = Settings.getIntSetting("NUM_CLIENTS");
//        clients = clientGen.getClients(numClients);
//        int numOfEvents = 500;
//        storers.consumers.InboundMessageHandlerRabbitMQ.on();
//        Enums.EventType[] events = {Enums.EventType.CREATE};
//        dataGenerator.generators.EventGenerator eg = new dataGenerator.generators.EventGenerator(clients, random, numOfEvents, events);
//        Emitter.initialize();
//        eg.run();
    }

}
