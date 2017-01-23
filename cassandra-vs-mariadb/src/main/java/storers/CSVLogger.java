package storers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class CSVLogger {

    private static final String DEFAULT_FOLDER = "./testLogs";
    private boolean doNotLog;
    private String folderName;
    private String[] headers;
    private BufferedWriter bufferedWriter = null;
    private FileWriter fileWriter = null;
    private String fileName;
    private String testID;

    public CSVLogger(String fileName) throws IOException {
        this(DEFAULT_FOLDER, fileName, fileName);
    }

    public CSVLogger(String folderName, String fileName, String testID) throws IOException {
        this.headers = new String[]{"TestID", "DatabaseType", "EventType", "TimeTaken", "Success", "ErrorMessage", "TimeStamp"};
        this.folderName = folderName;
        this.fileName = fileName;
        this.testID = testID;
        setUpLogFile();
    }

    public CSVLogger(boolean doNotLog) throws IOException {
        this.doNotLog = doNotLog;
    }

    public void logEvent(String[] eventData, boolean header) {
        String logLine = "";
        synchronized (this) {
            if (!doNotLog) {
                try {
                    this.fileWriter = new FileWriter(this.folderName + "/" + this.fileName + ".csv", true);
                    this.bufferedWriter = new BufferedWriter(fileWriter);
                    logLine = eventDataToLogLine(eventData, header);
                    this.bufferedWriter.write(logLine);
                } catch (IOException e) {
                    System.out.println("Failed to write log line");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } finally {
                    closeLog();
                }
            }
        }
    }

    private String eventDataToLogLine(String[] eventData, boolean header) {
        String logLine = "";
        if (!header) logLine = this.testID + ", ";
        for (String eventDatum : eventData) {
            logLine = logLine + eventDatum + ", ";
        }
        return logLine + "\n";
    }

    public void closeLog() {
        try {
            if (this.bufferedWriter != null) this.bufferedWriter.close();
            if (this.fileWriter != null) this.fileWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to close writers");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUpLogFile() {
        logEvent(this.headers, true);
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }
}
