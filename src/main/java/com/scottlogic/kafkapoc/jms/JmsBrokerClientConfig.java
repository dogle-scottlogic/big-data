package com.scottlogic.kafkapoc.jms;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.scottlogic.kafkapoc.BrokerClientConfig;
import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.ProducerClient;

@Component
@Profile("jms")
public class JmsBrokerClientConfig implements BrokerClientConfig {

	@Override
	public ProducerClient producerClient() {
		return new JmsProducerClient();
	}

	@Override
	public ConsumerClient consumerClient() {
		return new JmsConsumerClient();
	}

}
