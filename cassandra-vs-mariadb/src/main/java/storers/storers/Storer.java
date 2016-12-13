package storers.storers;

import org.json.simple.JSONObject;

/**
 * Created by dogle on 08/12/2016.
 */
public interface Storer {
    void messageHandler(JSONObject message);
};
