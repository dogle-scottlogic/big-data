package data_handlers;
import com.typesafe.config.*;

/**
 * Created by dogle on 02/12/2016.
 */
public class Settings {

    private static final String path = "data-generator.";
    private static final String hatPath = "hat.";
    private static final String queuePath = "queue-settings.";
    private static Config conf = ConfigFactory.load();

    public static String getStringSetting(String setting) {
        return conf.getString(path.concat(setting));
    }

    public static int getIntSetting(String setting) {
        return conf.getInt(path.concat(setting));
    }

    public static int getIntHatSetting(String setting) {
        return conf.getInt(hatPath.concat(setting));
    }

    public static String getStringQueueSetting(String setting) {
        return conf.getString(queuePath.concat(setting));
    }

    public static boolean getBoolQueueSetting(String setting) {
        return conf.getBoolean(queuePath.concat(setting));
    }

}
