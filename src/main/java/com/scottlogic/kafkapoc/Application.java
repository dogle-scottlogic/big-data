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
	private static final String DEFAULT_PRODUCER_RATE_PER_SECOND = "3000";
	private static final String DEFAULT_CONSUMER_RATE_PER_SECOND = "10000";
	private static final String DEFAULT_BATCH_SIZE = "100";
	private static final String DEFAULT_CONSUMER_TIMEOUT = "5000";
	private static int messages;
	private static int rate;
	private static int batchSize;
	private static int consumerTimeout;
	private static boolean isProducer;

	public static void main(String[] args) {
		// Set up variables
		if (Arrays.asList(args).contains("producer")) {
			isProducer = true;
		}
		messages = Integer.valueOf(System.getProperty("kafka.messages", DEFAULT_NUMBER_OF_MESSAGES));
		final String DEFAULT_RATE_PER_SECOND = isProducer ? DEFAULT_PRODUCER_RATE_PER_SECOND : DEFAULT_CONSUMER_RATE_PER_SECOND;
		rate = Integer.valueOf(System.getProperty("kafka.rate", DEFAULT_RATE_PER_SECOND));
		batchSize = Integer.valueOf(System.getProperty("kafka.batchSize", DEFAULT_BATCH_SIZE));
		consumerTimeout = Integer.valueOf(System.getProperty("kafka.consumerTimeoutRate", DEFAULT_CONSUMER_TIMEOUT));

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

	@Autowired
	private BrokerClientConfig brokerClientConfig;

	@Bean
	@Profile("producer")
	public Producer producer() {
		return new Producer(brokerClientConfig.producerClient(), messages, rate, batchSize);
	}

	@Bean
	@Profile("consumer")
	public Consumer consumer() {
		return new Consumer(brokerClientConfig.consumerClient(), messages, consumerTimeout, rate, batchSize);
	}
}
