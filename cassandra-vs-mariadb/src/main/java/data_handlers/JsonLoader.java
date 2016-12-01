package data_handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by dogle on 01/12/2016.
 */
public class JsonLoader {

    private String filePath = "\\src\\main\\resources\\data.json";

    public JsonLoader(String filePath) {
        this.filePath = filePath;
    }

    public JsonLoader() {

    }

    public JSONObject loadJson() {
        this.filePath = new File("").getAbsolutePath().concat(this.filePath);

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(this.filePath));

            JSONObject jsonObject = (JSONObject) obj;

            return jsonObject;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    return null;
    }

    public JSONObject getJsonField(String field) {
            JSONObject jsonObject = loadJson();
            JSONObject jsonField = (JSONObject) jsonObject.get(field);
            return jsonField;
    }

}
