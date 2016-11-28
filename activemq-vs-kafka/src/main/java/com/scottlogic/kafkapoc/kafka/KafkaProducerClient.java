package com.scottlogic.kafkapoc.kafka;

import com.scottlogic.kafkapoc.ProducerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Lazy
@Profile("producer")
class KafkaProducerClient implements ProducerClient {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public void send(String content) {
        kafkaTemplate.sendDefault(content);
    }

    public void destroy() {
        // no action required
    }
}