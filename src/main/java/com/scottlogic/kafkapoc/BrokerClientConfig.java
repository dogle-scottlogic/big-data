package com.scottlogic.kafkapoc;

public interface BrokerClientConfig {

	ProducerClient producerClient(boolean persistent, boolean topic);

	ConsumerClient consumerClient(boolean topic);

}
