package com.scottlogic.kafkapoc;

public interface BrokerClientConfig {

	ProducerClient producerClient(boolean persistent, boolean topic, boolean async);

	ConsumerClient consumerClient(boolean topic);

}
