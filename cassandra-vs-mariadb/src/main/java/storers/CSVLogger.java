package storers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class CSVLogger {

    private String folderName;
    private String[] headers;
    private BufferedWriter bufferedWriter = null;
    private FileWriter fileWriter = null;
    private String fileName;

    public CSVLogger(String folderName, String fileName) throws IOException {
        this.headers = new String[]{"TestID", "DatabaseType", "EventType", "TimeTaken", "Success", "ErrorMessage", "TimeStamp"};
        this.folderName = folderName;
        this.fileName = fileName;
        setUpLogFile();
    }

    public void logEvent(String[] eventData, boolean header) {
        try {
            this.fileWriter = new FileWriter(this.folderName + "/" + this.fileName  + ".csv", true);
            this.bufferedWriter = new BufferedWriter(fileWriter);
            String logLine = eventDataToLogLine(eventData, header);
            this.bufferedWriter.write(logLine);
        } catch (IOException e) {
            System.out.println("Failed to write log line");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(this.bufferedWriter != null) bufferedWriter.close();
                if(this.fileWriter != null) fileWriter.close();
            } catch (IOException e) {
                System.out.println("Failed to close writers");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String eventDataToLogLine(String[] eventData, boolean header) {
        String logLine = "";
        if(!header) logLine = this.fileName + ", ";
        for (String eventDatum : eventData) {
            logLine = logLine + eventDatum + ", ";
        }
        return logLine + "\n";
    }

    public void closeLog() throws IOException {
        bufferedWriter.close();
        fileWriter.close();
    }

    public void setUpLogFile() {
        logEvent(this.headers, true);
    }
}
