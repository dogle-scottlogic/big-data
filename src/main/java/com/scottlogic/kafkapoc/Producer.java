package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

class Producer implements DisposableBean{

    private ProducerClient producerClient;
    private int counter;
    private int amountToSend;
    private int ratePerSecond;
    private long lastNanos;

    Producer(ProducerClient producerClient, int amountToSend, int ratePerSecond){
        this.producerClient = producerClient;
        this.counter = 0;
        this.amountToSend = amountToSend;
        this.ratePerSecond = ratePerSecond;
    }

    void sendMessages(){
        while (counter < amountToSend) {
            maybeSleep();
            producerClient.send(Integer.toString(counter++));
            lastNanos = System.nanoTime();
        }
        outputStats();
        counter = 0;
    }

    private void maybeSleep() {
        long nanosToWait = 1000000000/ratePerSecond;
        long currentNanos = System.nanoTime();
        System.out.println(String.format("Waiting %s nanos", lastNanos - (currentNanos - nanosToWait)));
        while (currentNanos < lastNanos + nanosToWait) {
            currentNanos = System.nanoTime();
        }
    }

    public void destroy() {
        producerClient.destroy();
        if (counter > 0) {
            outputStats();
        }
    }

    private void outputStats() {
        System.out.println(String.format("Messages sent: %s", counter));
    }
}