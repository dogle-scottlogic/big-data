package storers;

import org.json.simple.JSONObject;
import storers.cassandra.Cassandra;

/**
 * Created by dogle on 05/12/2016.
 */
public class CassandraDBStorer {

    public static void messageHandler(JSONObject message) {

        //Test
        Cassandra cassandra = new Cassandra("0.0.0.0");
        cassandra.connect();

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

    public static void create(JSONObject message) {

    }

    public static void update(JSONObject message) {


    }

    public static void delete(JSONObject message) {


    }
}
