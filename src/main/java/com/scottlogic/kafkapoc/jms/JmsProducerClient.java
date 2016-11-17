package com.scottlogic.kafkapoc.jms;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.scottlogic.kafkapoc.ProducerClient;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
@Lazy
@Profile("producer")
class JmsProducerClient implements ProducerClient {

    private static final Logger LOG = LoggerFactory.getLogger(JmsProducerClient.class);
    @Autowired
    JmsTemplate jmsTemplate;
    
    public void send(String content) {
        jmsTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        jmsTemplate.convertAndSend("TEST.FOO23", content);
    }

    public void destroy() {
    }
}