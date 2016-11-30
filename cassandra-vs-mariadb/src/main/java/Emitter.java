import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import entities.Event;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class Emitter {
    private final static String QUEUE_NAME = "event-queue";
    private final static String HOST_NAME = "localhost";

    private static ConnectionFactory factory;
    private static Channel channel;
    private static Connection connection;

    public static void initialize() {
        try {
            Emitter.factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);
            Emitter.channel = connection.createChannel();
            Emitter.connection = Emitter.factory.newConnection();
            Emitter.channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void end() {
        try {
            Emitter.channel.close();
            Emitter.connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void emitEvent(Event e) {
        String jsonOrder = Serializer.Serialize(e);
        System.out.println(" SENT: " + jsonOrder);

        try {
            Emitter.channel.basicPublish("", QUEUE_NAME, null, jsonOrder.getBytes());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
