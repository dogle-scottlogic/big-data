//import org.junit.Test;
//import storers.CSVLogger;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.Iterator;
//
//import static org.junit.Assert.*;
//
///**
// * Created by lcollingwood on 07/12/2016.
// */
//public class CSVLoggerTest {
//
//    final String[] TEST_COLUMNS = new String[] { "THESE", "ARE", "COLUMN", "NAMES" };
//    final String[] TEST_ROW = new String[] { "This", "is", "a", "row" };
//
//    @Test
//    public void logEvent() throws Exception {
//        String filePath = "C:\\dev\\test.csv";
//        CSVLogger csvLogger = new CSVLogger(filePath, TEST_COLUMNS);
//
//        csvLogger.logEvent(TEST_ROW);
//        csvLogger.closeLog();
//
//        File file = new File(filePath);
//        FileReader fileReader = new FileReader(filePath);
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//        String openingLine = bufferedReader.readLine();
//        String followUp = bufferedReader.readLine();
//        assertTrue(file.exists());
//        assertTrue(openingLine.equals("THESE, ARE, COLUMN, NAMES, "));
//        assertTrue(followUp.equals("This, is, a, row, "));
//    }
//
//    @SuppressWarnings("Since15")
//    @Test
//    public void logTenMillionEvents() throws Exception {
//        String filePath = "C:\\dev\\test.csv";
//        CSVLogger csvLogger = new CSVLogger(filePath, TEST_COLUMNS);
//
//        for (int i = 0; i < 10000000; i++) {
//            csvLogger.logEvent(TEST_ROW);
//        }
//        csvLogger.closeLog();
//
//        File file = new File(filePath);
//        FileReader fileReader = new FileReader(filePath);
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//        assertTrue(file.exists());
//
//        int j = 0;
//        Iterator<String> lines = bufferedReader.lines().iterator();
//        String openingLine = lines.next();
//        assertTrue(openingLine.equals("THESE, ARE, COLUMN, NAMES, "));
//
//        while (lines.hasNext()) {
//            j++;
//            String followUp = lines.next();
//            assertTrue(followUp.equals("This, is, a, row, "));
//        }
//        assertTrue(j == 10000000);
//    }
//
//}