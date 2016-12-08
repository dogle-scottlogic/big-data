package Conveyor;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.entities.Event;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import dataGenerator.transmission.Serializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import storers.CSVLogger;
import storers.storers.Storer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 08/12/2016.
 */
public class Conveyor {

    private static int seed = Settings.getIntSetting("SEED");
    private static int numClients = Settings.getIntSetting("NUM_CLIENTS");
    private static Random random = new Random(seed);
    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static dataGenerator.generators.ClientGenerator clientGen;

    public static void processEvents(int numberOfEventsToProcess, Enums.EventType[] events, Storer storer) throws IOException {
        CSVLogger logger = new CSVLogger("C:\\dev\\big-data-bench\\cassandra-vs-mariadb\\testLogs");
        clientGen = new dataGenerator.generators.ClientGenerator(random);
        clients = clientGen.getClients(numClients);
        EventGenerator eventGenerator = new EventGenerator(clients, random, events);

        for(int i = 0; i < numberOfEventsToProcess; i++) {
            Event event = eventGenerator.getNextEvent();
            String jsonStringOrder = Serializer.Serialize(event);
            JSONParser parser = new JSONParser();
            JSONObject jsonOrder = new JSONObject();
            try {
                jsonOrder = (JSONObject) parser.parse(jsonStringOrder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.logEvent(storer.messageHandler(jsonOrder), false);
        }
    }
}
