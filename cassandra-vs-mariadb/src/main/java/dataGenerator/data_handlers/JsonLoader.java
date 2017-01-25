package dataGenerator.data_handlers;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dogle on 01/12/2016.
 */
public class JsonLoader {

    private final static Logger LOG = Logger.getLogger(JsonLoader.class);
    private String filePath = Settings.getStringSetting("DATA_FILE_PATH");

    public JsonLoader(String filePath) {
        this.filePath = filePath;
    }

    public JsonLoader() {

    }

    public JSONObject loadJson() {
        String absPath = new File("").getAbsolutePath().concat(this.filePath);
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(absPath));
            return (JSONObject) obj;
        } catch (IOException | ParseException e) {
            LOG.warn("Failed to parse json", e);
        }
        return null;
    }

    public JSONObject getJsonField(String field) {
            JSONObject jsonObject = loadJson();
        return (JSONObject) jsonObject.get(field);
    }

}
