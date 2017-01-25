package showdown;

import Conveyor.Conveyor;
import dataGenerator.enums.Enums;
import dataGenerator.generators.EventGenerator;
import org.apache.log4j.Logger;
import org.junit.Test;
import storers.CSVLogger;
import storers.storers.Combo;
import storers.storers.maria.enums.DBType;

/**
 * Created by dogle on 09/12/2016.
 */
public class StartToEnd {
    private final static Logger LOG = Logger.getLogger(StartToEnd.class);

    @Test
    public void end2end10000() throws Exception {
        int numOfEvents = 10000;

        storers.storers.Timer timer = new storers.storers.Timer();
        timer.startTimer();
        EventGenerator eventGenerator;
        CSVLogger dummyLogger = new CSVLogger(true);

        //Maria
        Combo combo;
        LOG.info("DB, Connection Pool Max Size, Total Time in Nanoseconds, Number of Operations,");

        eventGenerator = Conveyor.initialiseEventsGenerator(new Enums.EventType[]{Enums.EventType.CREATE});

        for (int i = 1; i < 50; i++) {
            for (int j = 0; j < 5; j++) {
                combo = new Combo(dummyLogger, DBType.MARIA_DB, i);
                timer.startTimer();
                Conveyor.processEvents(numOfEvents, combo, eventGenerator);
                LOG.info("MariaDB, " + i + ", " + timer.stopTimer() + ", " + numOfEvents + ",");
            }
        }


    }
}
