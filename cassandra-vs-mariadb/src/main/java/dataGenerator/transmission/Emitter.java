package dataGenerator.transmission;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Event;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class Emitter {
    private final static Logger LOG = Logger.getLogger(Emitter.class);
    private final static String QUEUE_NAME = Settings.getStringQueueSetting("QUEUE_NAME");
    private final static String HOST_NAME = Settings.getStringQueueSetting("HOST_NAME");

    private static Channel channel;
    private static Connection connection;

    private static boolean durable = Settings.getBoolQueueSetting("DURABLE");
    private static boolean exclusive = Settings.getBoolQueueSetting("EXCLUSIVE");
    private static boolean autoDelete = Settings.getBoolQueueSetting("AUTO_DELETE");


    public static void initialize() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, durable, exclusive, autoDelete, null);
        } catch (TimeoutException | IOException e) {
            LOG.warn("Failed to initialise", e);
        }
    }

    public static void end() {
        try {
            channel.close();
            connection.close();
        } catch (TimeoutException | IOException e) {
            LOG.warn("Failed to close channel", e);
        }
    }

    public static String emitEvent(Event event) {
        String jsonOrder = Serializer.Serialize(event);
        try {
            channel.basicPublish(Settings.getStringQueueSetting("EXCHANGE"), QUEUE_NAME, null, jsonOrder.getBytes());
            return " SENT: " + jsonOrder;
        } catch (IOException | NullPointerException e) {
            LOG.warn("Exception: The queue may not be running.", e);
        }
        return "";
    }
}
