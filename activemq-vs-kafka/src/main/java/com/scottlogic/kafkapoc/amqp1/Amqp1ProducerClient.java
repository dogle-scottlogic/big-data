package com.scottlogic.kafkapoc.amqp1;

import com.scottlogic.kafkapoc.ProducerClient;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Header;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.messenger.Messenger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Lazy
@Profile("producer")
class Amqp1ProducerClient implements ProducerClient {

    private Messenger messenger;
    @Value("${persistent}")
    private boolean persistent;
    @Value("${async}")
    private boolean async;
    @Value("${name}")
    private String name;
    @Value("${topic}")
    private boolean topic;

    Amqp1ProducerClient(){
        messenger = Messenger.Factory.create();
        try {
            messenger.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void send(String content) {
        Message msg = Message.Factory.create();
        String prefix = topic ? (persistent ? "dsub://" : "topic://") : "queue://";
        msg.setAddress("amqp://localhost/" + prefix + name);
        msg.setBody(new AmqpValue(content));
        Header header = new Header();
        header.setDurable(persistent);
        msg.setHeader(header);
        messenger.put(msg);
        messenger.send();
    }

    public void destroy() {
        messenger.stop();
    }
}
