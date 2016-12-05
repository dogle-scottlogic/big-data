package storers;

import org.json.simple.JSONObject;

/**
 * Created by lcollingwood on 05/12/2016.
 */

public class MariaDBStorer {
    public static void messageHandler(JSONObject message) {
        String type = (String) message.get("type");

        if (type.equals("UPDATE")) {
            update(message);
        }

        if (type.equals("CREATE")) {
            create(message);
        }

        if (type.equals("DELETE")) {
            delete(message);
        }
    }

    private static void create(JSONObject message) {
        System.out.println("create From Switch");
    }

    private static void update(JSONObject message) {
        System.out.println("update From Switch");
    }

    private static void delete(JSONObject message) {
        System.out.println("delete From Switch");
    }

}
