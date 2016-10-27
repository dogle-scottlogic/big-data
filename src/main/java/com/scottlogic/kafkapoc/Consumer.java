package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

class Consumer implements Listener, DisposableBean {

    ConsumerClient consumerClient;

    Consumer(ConsumerClient consumerClient) {
        this.consumerClient = consumerClient;
        consumerClient.setListener(this);
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void destroy() throws Exception {
        this.consumerClient.destroy();
    }
}
