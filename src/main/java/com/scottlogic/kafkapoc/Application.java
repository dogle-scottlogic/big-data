package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).profiles(args).run(args);
	}
	
	@Autowired
	private BrokerClientConfig brokerClientConfig;
	
	@Bean
	@Profile("producer")
	public Producer producer() {
		return new Producer(brokerClientConfig.producerClient());
	}

	@Bean
	@Profile("consumer")
	public Consumer consumer() {
		return new Consumer(brokerClientConfig.consumerClient());
	}
}
