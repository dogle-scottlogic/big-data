package com.scottlogic.kafkapoc.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.scottlogic.kafkapoc.BrokerClientConfig;
import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.ProducerClient;

@Component
@Profile("jms")
public class JmsBrokerClientConfig implements BrokerClientConfig {

	@Autowired(required = false)
	JmsProducerClient producerClient;
	@Autowired(required = false)
	JmsConsumerClient consumerClient;

	@Override
	public ProducerClient producerClient() {
		return producerClient;
	}

	@Override
	public ConsumerClient consumerClient() {
		return consumerClient;
	}

}
