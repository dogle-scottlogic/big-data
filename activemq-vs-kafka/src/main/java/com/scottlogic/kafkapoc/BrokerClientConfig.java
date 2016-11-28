package com.scottlogic.kafkapoc;

public interface BrokerClientConfig {

	ProducerClient producerClient();

	ConsumerClient consumerClient();

}
