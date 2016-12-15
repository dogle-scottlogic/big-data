package storers.storers;

import org.json.simple.JSONObject;

import java.util.concurrent.Future;

/**
 * Created by dogle on 08/12/2016.
 */
public interface Storer {
    void messageHandler(JSONObject message);
    Future<?> messageHandlerAsync(JSONObject message);
}
