package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@SpringBootApplication
public class Application {

	private static final String DEFAULT_NUMBER_OF_MESSAGES = "50000";
	private static final String DEFAULT_SEND_RATE_PER_SECOND = "3000";
	private static int messages;
	private static int sendRate;

	public static void main(String[] args) {
		// Set up variables
		messages = Integer.valueOf(System.getProperty("kafka.messages", DEFAULT_NUMBER_OF_MESSAGES));
		sendRate = Integer.valueOf(System.getProperty("kafka.sendrate", DEFAULT_SEND_RATE_PER_SECOND));

		// Create Spring app
		ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Application.class).profiles(args).run(args);

		// Start producer running
		if (Arrays.asList(args).contains("producer")) {
			appContext.getBean(Producer.class).sendMessages();
		}
	}

	@Autowired
	private BrokerClientConfig brokerClientConfig;

	@Bean
	@Profile("producer")
	public Producer producer() {
		return new Producer(brokerClientConfig.producerClient(), messages, sendRate);
	}

	@Bean
	@Profile("consumer")
	public Consumer consumer() {
		return new Consumer(brokerClientConfig.consumerClient(), messages);
	}
}
