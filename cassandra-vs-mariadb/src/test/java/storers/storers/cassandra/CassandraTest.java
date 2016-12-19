package storers.storers.cassandra;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;

import java.io.File;
import java.io.IOException;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class CassandraTest {

        @Test
        public void testCassandraStorer() throws IOException, ParseException {
            int numOfEvents = 10;
                String absPath = new File("").getAbsolutePath().concat("\\testLogs");
                CSVLogger logger = new CSVLogger(absPath, "test");
                CassandraDBStorer storer = new CassandraDBStorer(logger);
                EventGenerator eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{});
                Conveyor.processEvents(numOfEvents, storer, eventGenerator);
        }
}