package com.scottlogic.kafkapoc.jms;

import com.scottlogic.kafkapoc.ProducerClient;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

class JmsProducerClient implements ProducerClient {

    private Session session;
    private MessageProducer producer;

    JmsProducerClient(){
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");

            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            String text = "Hello world!";

            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
    
    public void send(String content) {
        try {
            TextMessage message = null;
            message = session.createTextMessage(content);

            // Tell the producer to send the message
            System.out.println("Sent message: " + message.getText());
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}