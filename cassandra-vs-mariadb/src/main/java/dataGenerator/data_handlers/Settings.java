package dataGenerator.data_handlers;

import com.typesafe.config.*;

/**
 * Created by dogle on 02/12/2016.
 */
public class Settings {

    private static final String path = "data-generator.";
    private static final String hatPath = "hat.";
    private static final String queuePath = "queue-settings.";
    private static final String vmPath = "db-vm-settings.";
    private static Config conf = ConfigFactory.load();

    public static String getStringSetting(String setting) {
        return conf.getString(path.concat(setting));
    }

    public static boolean setStringSetting(String setting, String value) {
        try {
            conf = conf.withValue(path.concat(setting), ConfigValueFactory.fromAnyRef(value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setIntSetting(String setting, int value) {
        try {
            conf = conf.withValue(path.concat(setting), ConfigValueFactory.fromAnyRef(value));
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public static String getStringVmSetting(String setting) { return  conf.getString(vmPath.concat(setting)); }
}
