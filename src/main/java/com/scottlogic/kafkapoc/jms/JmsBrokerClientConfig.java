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
	public ProducerClient producerClient(boolean persistent, boolean topic) {
		return new JmsProducerClient(persistent, topic);
	}

	@Override
	public ConsumerClient consumerClient(boolean topic) {
		return new JmsConsumerClient(topic);
	}

}
