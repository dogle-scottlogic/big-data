package storers.storers.maria;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.MariaDBStorer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class MariaDBStorerTest {
    @Test
    public void testMariaStorer() throws IOException, ParseException, SQLException {
        int numOfEvents = 10;
        String absPath = new File("").getAbsolutePath().concat("\\testLogs");
        CSVLogger logger = new CSVLogger(absPath, "test");
        MariaDBStorer storer = new MariaDBStorer(false, logger);
        EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{});
        Conveyor.processEvents(numOfEvents, storer, eventGenerator);
    }
}