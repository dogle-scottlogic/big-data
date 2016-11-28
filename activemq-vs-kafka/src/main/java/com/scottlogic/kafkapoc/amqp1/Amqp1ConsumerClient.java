package com.scottlogic.kafkapoc.amqp1;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;
import org.apache.qpid.proton.InterruptException;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.messenger.Messenger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Lazy
@Profile("consumer")
class Amqp1ConsumerClient implements ConsumerClient {

    private boolean interrupted = false;
    @Value("${persistent}")
    private boolean persistent;
    @Value("${clientId}")
    private String clientId;
    @Value("${name}")
    private String name;
    @Value("${topic}")
    private boolean topic;

    private Messenger messenger;

    @PostConstruct
    public void init() {
        messenger = Messenger.Factory.create();
        String prefix = topic ? (persistent ? "dsub://" : "topic://") : "queue://";
        try {
            messenger.start();
            messenger.subscribe("amqp://localhost/" + prefix + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void destroy() {
        interrupted = true;
        messenger.interrupt();
        messenger.stop();
    }

    @Override
    public void setListener(Listener listener) {
        while(!interrupted) {
            try {
                messenger.recv(1);
                if (messenger.incoming() > 0) {
                    Message msg = messenger.get();
                    if (msg != null && msg.getBody() != null) {
                        AmqpValue body = ((AmqpValue) msg.getBody());
                        listener.onMessage(body.getValue().toString());
                    }
                }
            } catch (InterruptException e) {
                interrupted = true;
            }
        }
    }
}
