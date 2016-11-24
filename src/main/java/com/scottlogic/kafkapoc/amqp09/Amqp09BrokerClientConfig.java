package com.scottlogic.kafkapoc.amqp09;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.scottlogic.kafkapoc.BrokerClientConfig;
import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.ProducerClient;

@Component
@Profile("amqp09")
public class Amqp09BrokerClientConfig implements BrokerClientConfig {

    @Autowired(required = false)
    private Amqp09ProducerClient producerClient;
    @Autowired(required = false)
    private Amqp09ConsumerClient consumerClient;

    @Override
    public ProducerClient producerClient() {
        return producerClient;
    }

    @Override
    public ConsumerClient consumerClient() {
        return consumerClient;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue myQueue() {
        return new Queue("AMQP.QUEUE");
    }
}
