package com.scottlogic.kafkapoc.jms;

import com.scottlogic.kafkapoc.ProducerClient;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

class JmsProducerClient implements ProducerClient {

    private static final Logger LOG = LoggerFactory.getLogger(JmsProducerClient.class);
    private Session session;
    private Connection connection;
    private MessageProducer producer;

    JmsProducerClient(boolean persistent, boolean topic, boolean async){
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connectionFactory.setUseAsyncSend(async);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            // Create the destination (Topic or Queue)
            Destination destination = topic ? session.createTopic("TEST.FOO") : session.createQueue("TEST.FOO");

            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
        }
        catch (Exception e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }
    
    public void send(String content) {
        try {
            TextMessage message = session.createTextMessage(content);
            producer.send(message);
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }

    public void destroy() {
        LOG.info("Producer client killed");
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }
}