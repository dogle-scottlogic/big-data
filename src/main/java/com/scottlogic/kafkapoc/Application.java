package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Application.class).profiles(args).run(args);
		if (Arrays.asList(args).contains("producer")) {
			appContext.getBean(Producer.class).sendMessages();
		}
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
