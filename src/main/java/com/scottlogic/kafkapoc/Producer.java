package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

class Producer implements DisposableBean{

    ProducerClient producerClient;
    private int counter;
    private int amountToSend;

    Producer(ProducerClient producerClient, int amountToSend){
        this.producerClient = producerClient;
        this.counter = 0;
        this.amountToSend = amountToSend;
    }

    public void sendMessages(){
        while (counter < amountToSend) {
            producerClient.send(Integer.toString(counter++));
        }
        outputStats();
        counter = 0;
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