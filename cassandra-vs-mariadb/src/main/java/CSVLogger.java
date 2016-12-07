import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class CSVLogger {

    BufferedWriter bufferedWriter;
    FileWriter fileWriter;

    public CSVLogger(String filename, String[] columns) throws IOException {
        fileWriter = new FileWriter(filename);
        bufferedWriter = new BufferedWriter(fileWriter);
        logEvent(columns);
    }

    public void logEvent(String[] eventData) throws IOException {
        String logLine = eventDataToLogLine(eventData);
        bufferedWriter.write(logLine);
    }

    private String eventDataToLogLine(String[] eventData) {
        String logLine = "";
        for (String eventDatum : eventData) {
            logLine = logLine + eventDatum + ", ";
        }
        return logLine + "\n";
    }

    public void closeLog() throws IOException {
        bufferedWriter.close();
        fileWriter.close();
    }
}
