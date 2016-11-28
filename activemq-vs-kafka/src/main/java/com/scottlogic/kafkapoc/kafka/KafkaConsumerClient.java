package com.scottlogic.kafkapoc.kafka;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Lazy
@Profile("consumer")
class KafkaConsumerClient implements ConsumerClient {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    private Listener listener;
    private final CountDownLatch latch1 = new CountDownLatch(1);

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    @KafkaListener(id = "foo2", topics = "KAFKA.FOO2")
    public void onMessage(String message) {
        this.latch1.countDown();
        listener.onMessage(message);
    }
}