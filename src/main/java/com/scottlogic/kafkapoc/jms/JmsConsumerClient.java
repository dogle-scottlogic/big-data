package com.scottlogic.kafkapoc.jms;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
@Lazy
@Profile("consumer")
class JmsConsumerClient implements ConsumerClient {

    private static final Logger LOG = LoggerFactory.getLogger(JmsConsumerClient.class);
    private Listener listener;

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void destroy() {
    }

    @JmsListener(destination = "TEST.FOO", containerFactory = "myFactory")
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