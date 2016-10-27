package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

class Producer implements DisposableBean{

    ProducerClient producerClient;

    Producer(ProducerClient producerClient){
        this.producerClient = producerClient;
    }

    public void sendMessages(){
        producerClient.send(Integer.toString(1));
    }

    public void destroy() {
        producerClient.destroy();
    }
}