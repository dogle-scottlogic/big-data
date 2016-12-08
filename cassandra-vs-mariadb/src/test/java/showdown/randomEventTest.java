package showdown;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.enums.Enums;
import dataGenerator.transmission.Emitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 08/12/2016.
 */
public class randomEventTest {

    private  static int seed = Settings.getIntSetting("SEED");
    private static Random random;
    private static ArrayList<Client> clients;
    private static dataGenerator.generators.ClientGenerator clientGen;

    @Before
    public void setUp() throws Exception {
        random = new Random(seed);
        clients = new ArrayList<Client>();
        clientGen = new dataGenerator.generators.ClientGenerator(random);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void FiveHundredTotalRandomEvents() throws Exception {
        int numClients = Settings.getIntSetting("NUM_CLIENTS");
        Settings.setStringSetting("EVENT_GEN_MODE", "random");
        clients = clientGen.getClients(numClients);
        int numOfEvents = 500;
        storers.consumers.InboundMessageHandlerRabbitMQ.on();
        dataGenerator.generators.EventGenerator eg = new dataGenerator.generators.EventGenerator(clients, random, numOfEvents, new Enums.EventType[]{});
        Emitter.initialize();
        eg.run();
    }

    @Test
    public void FiveHundredCreateEvents() throws Exception {
        int numClients = Settings.getIntSetting("NUM_CLIENTS");
        clients = clientGen.getClients(numClients);
        int numOfEvents = 500;
        storers.consumers.InboundMessageHandlerRabbitMQ.on();
        Enums.EventType[] events = {Enums.EventType.CREATE};
        dataGenerator.generators.EventGenerator eg = new dataGenerator.generators.EventGenerator(clients, random, numOfEvents, events);
        Emitter.initialize();
        eg.run();
    }

}
