package storers;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class CSVLogger {

    private final static Logger LOG = Logger.getLogger(CSVLogger.class);
    private static final String DEFAULT_FOLDER = "./testLogs";
    private boolean doNotLog;
    private String folderName;
    private String[] headers;
    private BufferedWriter bufferedWriter = null;
    private FileWriter fileWriter = null;
    private String fileName;
    private String testID;

    public CSVLogger(String fileName) throws IOException {
        this(fileName, fileName);
    }

    public CSVLogger(String folderName, String fileName, String testID) throws IOException {
        this.headers = new String[]{"TestID", "DatabaseType", "EventType", "TimeTaken", "Success", "ErrorMessage", "TimeStamp"};
        this.folderName = folderName;
        this.fileName = fileName;
        this.testID = quoteIfContiansComma(testID);
        setUpLogFile();
    }

    /**
     * Returns the string if it does not contain a comma, but if it does contain a comma it is surrounded with quotes to
     * prevent the value being interpreted as separate columns.
     */
    private String quoteIfContiansComma(String string) {
        if (string.indexOf(',') != -1) {
            return "\"" + string + "\"";
        } else {
            return string;
        }
    }

    public CSVLogger(boolean doNotLog) throws IOException {
        this.doNotLog = doNotLog;
    }

    public CSVLogger(String fileName, String id) throws IOException {
        this(DEFAULT_FOLDER, fileName, id);
    }

    public void logEvent(String[] eventData, boolean header) {
        synchronized (this) {
            if (!doNotLog) {
                try {
                    this.fileWriter = new FileWriter(this.folderName + "/" + this.fileName + ".csv", true);
                    this.bufferedWriter = new BufferedWriter(fileWriter);
                    String logLine = eventDataToLogLine(eventData, header);
                    this.bufferedWriter.write(logLine);
                } catch (IOException e) {
                    LOG.warn("Failed to write log line", e);
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
            logLine = logLine + quoteIfContiansComma(eventDatum) + ", ";
        }
        return logLine + "\n";
    }

    private void closeLog() {
        try {
            if (this.bufferedWriter != null) this.bufferedWriter.close();
            if (this.fileWriter != null) this.fileWriter.close();
        } catch (IOException e) {
            LOG.warn("Failed to close writers", e);
        }
    }

    private void setUpLogFile() {
        logEvent(this.headers, true);
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }
}
