package com.scottlogic.kafkapoc.amqp09;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.scottlogic.kafkapoc.BrokerClientConfig;
import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.ProducerClient;

@Component
@Profile("amqp09")
public class Amqp09BrokerClientConfig implements BrokerClientConfig {
// this all needs creating

	@Override
	public ProducerClient producerClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConsumerClient consumerClient() {
		// TODO Auto-generated method stub
		return null;
	}


}
