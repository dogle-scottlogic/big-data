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

	private static final int DEFAULT_NUMBER_OF_MESSAGES = 50000;
	private static final int DEFAULT_PRODUCER_RATE_PER_SECOND = 3000;
	private static final int DEFAULT_CONSUMER_RATE_PER_SECOND = 10000;
	private static final int DEFAULT_BATCH_SIZE = 100;
	private static final int DEFAULT_TIMEOUT = 5000;
	private static int messages;
	private static int rate;
	private static int batchSize;
	private static int timeout;
	private static String clientId;
	private static boolean persistent;
	private static boolean topic;
	private static boolean async;

	public static void main(String[] args) {
		// Set up variables
		boolean isProducer = Arrays.asList(args).contains("producer");
		messages = getSystemProperty("kafka.messages", DEFAULT_NUMBER_OF_MESSAGES);
		final int DEFAULT_RATE_PER_SECOND = isProducer ? DEFAULT_PRODUCER_RATE_PER_SECOND : DEFAULT_CONSUMER_RATE_PER_SECOND;
		rate = getSystemProperty("kafka.rate", DEFAULT_RATE_PER_SECOND);
		batchSize = getSystemProperty("kafka.batchSize", DEFAULT_BATCH_SIZE);
		timeout = getSystemProperty("kafka.timeout", DEFAULT_TIMEOUT);
		clientId = getSystemProperty("kafka.clientId", "client123");
		persistent = getSystemProperty("kafka.persistent", false);
		topic = getSystemProperty("kafka.topic", false);
		async = getSystemProperty("kafka.async", false);

		// Create Spring app
		ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Application.class).profiles(args).run(args);

		// Start producer running
		if (isProducer) {
			appContext.getBean(Producer.class).sendMessages();
		} else {
			appContext.getBean(Consumer.class).startListening();
		}

		// Shutdown
		appContext.close();
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
		return new Producer(brokerClientConfig.producerClient(persistent, topic, async), messages, rate, batchSize);
	}

	@Bean
	@Profile("consumer")
	public Consumer consumer() {
		return new Consumer(brokerClientConfig.consumerClient(persistent, topic, clientId), messages, timeout, rate, batchSize);
	}
}
