package storers.consumers;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.cassandra.Cassandra;
import storers.storers.MariaDBStorer;

import java.io.UnsupportedEncodingException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class InboundMessageHandlerRabbitMQ {
    private final static String QUEUE_NAME = "event-queue";
    private final static String HOST_NAME = "localhost";

    private static ConnectionFactory connectionFactory;
    private static Connection connection;
    private static Channel channel;
    private static Consumer consumer;
    private static CSVLogger logger;
    private static CSVLogger mariaLogger;

    public static void initialise() {
        try {
            logger = new CSVLogger("C:\\dev\\big-data-bench-projects\\cassandra-vs-mariadb\\testLogs");
            mariaLogger = new CSVLogger("C:\\dev\\big-data-bench-projects\\cassandra-vs-mariadb\\testLogs");
            connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(HOST_NAME);
            connection = InboundMessageHandlerRabbitMQ.connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            consumer = new DefaultConsumer(channel) {
                JSONParser parser = new JSONParser();

                // Init storers
                MariaDBStorer mariaDBStorer = new MariaDBStorer();
                CassandraDBStorer cassandraDBStorer = new CassandraDBStorer();

                public void handleDelivery(
                    String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body
                ) throws UnsupportedEncodingException {
                    String message = new String(body, "UTF-8");
                    Object obj = null;
                    try {
                        obj = parser.parse(message);
                        JSONObject jsonObject = (JSONObject) obj;
                        // Pass To Storers
                        mariaLogger.logEvent(mariaDBStorer.messageHandler(jsonObject), false);
                        logger.logEvent(cassandraDBStorer.messageHandler(jsonObject), false);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
        InboundMessageHandlerRabbitMQ.initialise();
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
