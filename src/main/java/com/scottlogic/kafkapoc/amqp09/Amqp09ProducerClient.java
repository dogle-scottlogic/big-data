package com.scottlogic.kafkapoc.amqp09;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.scottlogic.kafkapoc.ProducerClient;

class Amqp09ProducerClient implements ProducerClient {
	
	/**
	 * This can probably be autowired, but I'm also happy if it is injected from the broker.
	 */
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
    Amqp09ProducerClient(){
        System.out.println("Producerclient started up");
    }
    
    public void send(String content) {
        rabbitTemplate.convertAndSend(content);
    }

    public void destroy() {
        System.out.println("Producer client killed");
        // no action required, I don't think
    }
}