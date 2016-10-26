package com.scottlogic.kafkapoc.jms;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;
import com.scottlogic.kafkapoc.ProducerClient;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

class JmsConsumerClient implements ConsumerClient {

    private Listener listener;

    JmsConsumerClient(){
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // This whole section needs to happen repeatedly.
            // Wait for a message (no timeout)
            Message message = consumer.receive();

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                System.out.println("Received message: " + text);
                if (this.listener != null) {
                    listener.onReceiveMessage(text);
                }
            } else {
                System.out.println("Received message: " + message);
            }
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }
}