package com.scottlogic.kafkapoc.amqp09;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.scottlogic.kafkapoc.ConsumerClient;
import com.scottlogic.kafkapoc.Listener;

class Amqp09ConsumerClient implements ConsumerClient {

	private Listener listener;

	Amqp09ConsumerClient() {
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void destroy() {
		// nothing to do, I don't think
	}

	/**
	 * The queue needs creating somewhere I think.
	 */
	@RabbitListener(queues="MY_QUEUE")
	public void onMessage(String message) {
		listener.onMessage(message);
	}
}