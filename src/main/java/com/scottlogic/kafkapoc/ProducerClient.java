package com.scottlogic.kafkapoc;

public interface ProducerClient {

    void send(String content);

    void destroy();

}
