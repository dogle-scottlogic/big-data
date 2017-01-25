package dataGenerator.consumers;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class ConsoleLogger {
    private final static Logger LOG = Logger.getLogger(ConsoleLogger.class);
    private final static String QUEUE_NAME = "event-queue";
    private final static String HOST_NAME = "localhost";

    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    private static Channel channel;
    private static Consumer consumer;

    private static void initialise() {
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
                    LOG.info("Received '" + message + "'");
                }
            };
        } catch (TimeoutException | IOException e) {
            LOG.warn("Failed to initialise", e);
        }
    }

    public static void main(String[] args) {
        on();
    }

    public static void on() {
        ConsoleLogger.initialise();
        LOG.info("Listening...");

        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (java.io.IOException e) {
            LOG.warn("Failed to consume channel", e);
        }
    }

    public static void off() {
        try {
            channel.close();
            connection.close();
        } catch (TimeoutException | IOException e) {
            LOG.warn("Failed to close channel", e);
        }
    }
}
