package Conveyor;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.entities.Event;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import dataGenerator.transmission.Serializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import storers.CSVLogger;
import storers.storers.Storer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 08/12/2016.
 */
public class Conveyor {

    private static int seed = Settings.getIntSetting("SEED");
    private static int numClients = Settings.getIntSetting("NUM_CLIENTS");
    private static Random random;
    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static dataGenerator.generators.ClientGenerator clientGen;
    private static String filePath = "\\testLogs";
    private static String logName = "";


    public static void processEventsWithLog(int numberOfEventsToProcess, Enums.EventType[] events, Storer storer, String logFileName) {
        try {
            logName = logFileName;
            processEvents(numberOfEventsToProcess, events, storer, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processEventsWithoutLog(int numberOfEventsToProcess, Enums.EventType[] events, Storer storer) {
        try {
            processEvents(numberOfEventsToProcess, events, storer, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void processEvents(int numberOfEventsToProcess, Enums.EventType[] events, Storer storer, boolean log) throws IOException, ParseException {
        random = new Random(seed);
        String absPath = new File("").getAbsolutePath().concat(filePath);
        CSVLogger logger = null;
        if(log) logger = new CSVLogger(absPath, logName);
        clientGen = new dataGenerator.generators.ClientGenerator(random);
        clients = clientGen.getClients(numClients);
        EventGenerator eventGenerator = new EventGenerator(clients, random, events);

        for (int i = 0; i < numberOfEventsToProcess; i++) {
            Event event = eventGenerator.getNextEvent();
            String jsonStringOrder = Serializer.Serialize(event);
            JSONParser parser = new JSONParser();
            JSONObject jsonOrder = (JSONObject) parser.parse(jsonStringOrder);
            if (log) logger.logEvent(storer.messageHandler(jsonOrder), false);
        }
    }
}
