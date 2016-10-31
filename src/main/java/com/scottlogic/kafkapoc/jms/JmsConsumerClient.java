package com.scottlogic.kafkapoc.jms;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.TimeoutException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

class JmsConsumerClient implements ConsumerClient {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConsumerClient.class);
    private MessageConsumer consumer;
    private Session session;
    private Connection connection;

    JmsConsumerClient(){
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public String listen(int timeout) throws TimeoutException {
        try {
            Message message = consumer.receive(timeout);
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                return textMessage.getText();
            } else if (message == null) {
                throw new TimeoutException();
            } else {
                return null;
            }
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
            throw new TimeoutException();
        }
    }

    @Override
    public void destroy() {
        try {
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }
}