package Conveyor;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Event;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import dataGenerator.transmission.Serializer;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import storers.storers.Storer;

import java.io.IOException;
import java.util.Random;

/**
 * Created by dogle on 08/12/2016.
 */
public class Conveyor {
    private final static Logger LOG = Logger.getLogger(Conveyor.class);

    private static int seed = Settings.getIntSetting("SEED");

    public static EventGenerator initialiseEventsGenerator(Enums.EventType[] events) {
        Random random = new Random(seed);
        return new EventGenerator(random, events);
    }

    public static void processEvents(int numberOfEventsToProcess, Storer storer, EventGenerator eventGenerator) throws IOException, ParseException {
        processEvents(numberOfEventsToProcess, storer, eventGenerator, numberOfEventsToProcess / 10);
    }

    public static void processEvents(int numberOfEventsToProcess, Storer storer, EventGenerator eventGenerator, int loggingFrequency) throws IOException, ParseException {
        for (int i = 1; i <= numberOfEventsToProcess; i++) {
            Event event = eventGenerator.getNextEvent();
            String jsonStringOrder = Serializer.Serialize(event);
            JSONParser parser = new JSONParser();
            JSONObject jsonOrder = (JSONObject) parser.parse(jsonStringOrder);
            storer.messageHandler(jsonOrder);
            if (loggingFrequency != 0 && i % loggingFrequency == 0) {
                LOG.info("Processed " + i + " events out of " + numberOfEventsToProcess);
            }
        }
        storer.shutdown();
    }
}
