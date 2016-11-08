package com.scottlogic.kafkapoc.amqp09;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.scottlogic.kafkapoc.ProducerClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Lazy
@Profile("producer")
class Amqp09ProducerClient implements ProducerClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String content) {
        rabbitTemplate.convertAndSend("AMQP.QUEUE", content);
    }

    public void destroy() {
        // no action required, I don't think
    }
}