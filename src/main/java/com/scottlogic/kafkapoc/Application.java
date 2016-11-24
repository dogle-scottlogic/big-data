package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

	private static final int DEFAULT_NUMBER_OF_MESSAGES = 50000;
	private static final int DEFAULT_RATE_PER_SECOND = 3000;
	private static final int DEFAULT_BATCH_SIZE = 100;
	private static final String DEFAULT_CHANNEL_NAME = "TEST.FOO";
	private static final String DEFAULT_CLIENT_ID = "client123";

	public static void main(String[] args) {
		// Set up variables
		Map<String, Object> properties = new HashMap<>();
		properties.put("messages", getSystemProperty("kafka.messages", DEFAULT_NUMBER_OF_MESSAGES));
		properties.put("rate", getSystemProperty("kafka.rate", DEFAULT_RATE_PER_SECOND));
		properties.put("batchSize", getSystemProperty("kafka.batchSize", DEFAULT_BATCH_SIZE));
		properties.put("clientId", getSystemProperty("kafka.clientId", DEFAULT_CLIENT_ID));
		properties.put("persistent", getSystemProperty("kafka.persistent", false));
		properties.put("topic", getSystemProperty("kafka.topic", false));
		properties.put("async", getSystemProperty("kafka.async", false));
		properties.put("name", getSystemProperty("kafka.name", DEFAULT_CHANNEL_NAME));

		// Create Spring app
		ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Application.class)
				.properties(properties)
				.profiles(args)
				.run(args);

		boolean isProducer = Arrays.asList(args).contains("producer");
		// Start producer/consumer running
		if (isProducer) {
			appContext.getBean(Producer.class).sendMessages();

			// Shutdown
			appContext.close();
		} else {
			appContext.getBean(Consumer.class).startListening();
		}
	}

	private static boolean getSystemProperty(String name, Boolean defaultValue) {
		return Boolean.valueOf(System.getProperty(name, defaultValue.toString()));
	}

	private static String getSystemProperty(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}

	private static Integer getSystemProperty(String name, Integer defaultValue) {
		return Integer.valueOf(System.getProperty(name, defaultValue.toString()));
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
