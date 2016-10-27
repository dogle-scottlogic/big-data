package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.List;

class Consumer implements Listener, DisposableBean {

    ConsumerClient consumerClient;
    private List<Integer> numbers;
    private int expected;

    Consumer(ConsumerClient consumerClient, int expected) {
        this.consumerClient = consumerClient;
        numbers = new ArrayList<>();
        this.expected = expected;
        consumerClient.setListener(this);
    }

    @Override
    public void onMessage(String message) {
        numbers.add(Integer.valueOf(message));
        if (numbers.size() == expected) {
            outputStats();
            numbers = new ArrayList<>();
        }
    }

    @Override
    public void destroy() throws Exception {
        this.consumerClient.destroy();
        outputStats();
    }

    private void outputStats() {
        System.out.println(String.format("Messages received: %s", numbers.size()));
        boolean ordered = true;
        for (int i = 0; i < numbers.size(); i++) {
            if (i != numbers.get(i)) {
                ordered = false;
            }
        }
        System.out.println(String.format("Received in order: %s", ordered));
    }
}
