package com.scottlogic.kafkapoc.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.stereotype.Component;

import com.scottlogic.kafkapoc.BrokerClientConfig;
import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.ProducerClient;

@Component
@EnableJms
@EnableConfigurationProperties(JmsBrokerClientConfig.JmsTemplateProperties.class)
@Profile("jms")
public class JmsBrokerClientConfig implements BrokerClientConfig {

	@Autowired
	private JmsTemplateProperties config;

	private String name = "TEST.FOO";
	@Autowired(required = false)
	JmsProducerClient producerClient;
	@Autowired(required = false)
	JmsConsumerClient consumerClient;

	@Override
	public ProducerClient producerClient(boolean persistent, boolean topic, boolean async) {
		return producerClient;
	}

	@Override
	public ConsumerClient consumerClient(boolean persistent, boolean topic, String clientId) {
		return consumerClient;
	}

	@Bean
	public JmsListenerContainerFactory<?> myFactory(ActiveMQConnectionFactory connectionFactory,
													DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all boot's default to this factory, including the message converter
		connectionFactory.setUseAsyncSend(true);
		connectionFactory.setBrokerURL("tcp://localhost:61616");
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@ConfigurationProperties(prefix = "spring.jms")
	public static class JmsTemplateProperties {

		private boolean pubSubDomain = false;

		public boolean isPubSubDomain() {
			return this.pubSubDomain;
		}

		public void setPubSubDomain(boolean pubSubDomain) {
			this.pubSubDomain = pubSubDomain;
		}

	}

}
