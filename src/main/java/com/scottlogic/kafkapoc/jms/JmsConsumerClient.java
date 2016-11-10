package com.scottlogic.kafkapoc.jms;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

class JmsConsumerClient implements ConsumerClient, MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConsumerClient.class);
    private MessageConsumer consumer;
    private Session session;
    private Connection connection;
    private Listener listener;

    JmsConsumerClient(String name, boolean persistent, boolean topic, String clientId){
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = topic ? session.createTopic(name) : session.createQueue(name);
            if (topic && persistent) {
                consumer = session.createDurableSubscriber((Topic) destination, name);
            } else {
                consumer = session.createConsumer(destination);
            }
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
        try {
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            e.printStackTrace();
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

    @Override
    public void onMessage(Message message) {
        try {
            message.acknowledge();
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                listener.onMessage(textMessage.getText());
            }
        } catch (JMSException e) {
            LOG.error("Caught: " + e);
            e.printStackTrace();
        }
    }
}