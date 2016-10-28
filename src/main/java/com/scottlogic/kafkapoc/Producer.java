package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

class Producer implements DisposableBean{

    private ProducerClient producerClient;
    private int counter;
    private int amountToSend;
    private int ratePerSecond;
    private long lastMillis;

    Producer(ProducerClient producerClient, int amountToSend, int ratePerSecond){
        this.producerClient = producerClient;
        this.counter = 0;
        this.amountToSend = amountToSend;
        this.ratePerSecond = ratePerSecond;
    }

    void sendMessages(){
        while (counter < amountToSend) {
            maybeSleep();
            lastMillis = System.currentTimeMillis();
            producerClient.send(Integer.toString(counter++));
        }
        outputStats();
        counter = 0;
    }

    private void maybeSleep() {
        long millisToWait = 1000/ratePerSecond;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis < lastMillis + millisToWait) {
            try {
                Thread.sleep(millisToWait - (currentMillis - lastMillis));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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