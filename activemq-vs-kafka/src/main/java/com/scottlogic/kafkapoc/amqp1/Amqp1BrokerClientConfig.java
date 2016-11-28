package com.scottlogic.kafkapoc.amqp1;

import com.scottlogic.kafkapoc.BrokerClientConfig;
import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.ProducerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("amqp1")
public class Amqp1BrokerClientConfig implements BrokerClientConfig {

	@Autowired(required = false)
	Amqp1ProducerClient producerClient;
	@Autowired(required = false)
	Amqp1ConsumerClient consumerClient;


	@Override
	public ProducerClient producerClient() {
		return producerClient;
	}

	@Override
	public ConsumerClient consumerClient() {
		return consumerClient;
	}

}
