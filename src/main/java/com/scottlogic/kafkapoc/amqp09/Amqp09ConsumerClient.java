package com.scottlogic.kafkapoc.amqp09;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Lazy
@Profile("consumer")
class Amqp09ConsumerClient implements ConsumerClient {

    private Listener listener;

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void destroy() {
        // nothing to do, I don't think
    }

    @RabbitListener(queues = "AMQP.QUEUE")
    public void onMessage(String message) {
        if (listener != null) {
            listener.onMessage(message);
        }
    }
}