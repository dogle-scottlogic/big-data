package storers.consumers;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import storers.CSVLogger;
import storers.storers.CassandraDBStorer;
import storers.storers.MariaDBStorer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

/**
 * Created by lcollingwood on 30/11/2016.
 */
public class InboundMessageHandlerRabbitMQ {
    private final static Logger LOG = Logger.getLogger(InboundMessageHandlerRabbitMQ.class);
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
            connection = InboundMessageHandlerRabbitMQ.connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            consumer = new DefaultConsumer(channel) {
                JSONParser parser = new JSONParser();

                // Init storers
                CSVLogger mariaLogger = new CSVLogger(true);
                CSVLogger cassandraLogger = new CSVLogger(true);

                MariaDBStorer mariaDBStorer = new MariaDBStorer(true, mariaLogger);
                CassandraDBStorer cassandraDBStorer = new CassandraDBStorer(cassandraLogger);

                public void handleDelivery(
                    String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body
                ) throws UnsupportedEncodingException {
                    String message = new String(body, "UTF-8");
                    Object obj;
                    try {
                        obj = parser.parse(message);
                        JSONObject jsonObject = (JSONObject) obj;
                        // Pass To Storers
                        mariaDBStorer.messageHandler(jsonObject);
                        cassandraDBStorer.messageHandler(jsonObject);
                    } catch (ParseException e) {
                        LOG.warn("Failed to parse message", e);
                    }
                }
            };
        } catch (IOException | SQLException | TimeoutException e) {
            LOG.warn("Failed to initialise", e);
        }
    }

    public static void main(String[] args) {
        on();
    }

    public static void on() {
        InboundMessageHandlerRabbitMQ.initialise();
        LOG.info("Listening...");

        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (java.io.IOException e) {
            LOG.warn("Failed to handle message", e);
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
