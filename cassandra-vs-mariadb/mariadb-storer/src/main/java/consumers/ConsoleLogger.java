package consumers;

import com.rabbitmq.client.*;

import java.io.UnsupportedEncodingException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class ConsoleLogger {
    private final static String QUEUE_NAME = "event-queue";
    private final static String HOST_NAME = "localhost";

    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    private static Channel channel;
    private static Consumer consumer;

    public static void initialise() {
        try {
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(HOST_NAME);
            connection = ConsoleLogger.connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            consumer = new DefaultConsumer(channel) {
                public void handleDelivery(
                    String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body
                ) throws UnsupportedEncodingException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received '" + message + "'");
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        on();
    }

    public static void on() {
        ConsoleLogger.initialise();
        System.out.println("Listening...");

        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static void off() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
