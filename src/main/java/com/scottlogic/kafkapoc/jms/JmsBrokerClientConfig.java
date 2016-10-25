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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConsumerClient consumerClient() {
		// TODO Auto-generated method stub
		return null;
	}

}
