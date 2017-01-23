package dataGenerator.transmission;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Event;

import java.io.IOException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class Emitter {
    private final static String QUEUE_NAME = Settings.getStringQueueSetting("QUEUE_NAME");
    private final static String HOST_NAME = Settings.getStringQueueSetting("HOST_NAME");

    private static ConnectionFactory factory;
    private static Channel channel;
    private static Connection connection;

    private static boolean durable = Settings.getBoolQueueSetting("DURABLE");
    private static boolean exclusive = Settings.getBoolQueueSetting("EXCLUSIVE");
    private static boolean autoDelete = Settings.getBoolQueueSetting("AUTO_DELETE");


    public static void initialize() {
        try {
            factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, durable, exclusive, autoDelete, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void end() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String emitEvent(Event e) {
        String jsonOrder = Serializer.Serialize(e);
        try {
            channel.basicPublish(Settings.getStringQueueSetting("EXCHANGE"), QUEUE_NAME, null, jsonOrder.getBytes());
            return " SENT: " + jsonOrder;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        catch (NullPointerException npe) {
           System.out.println(npe.getMessage());
           System.out.println("Exception: The queue may not be running.");
        }
        return "";
    }
}
