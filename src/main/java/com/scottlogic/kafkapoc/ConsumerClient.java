package com.scottlogic.kafkapoc;

public interface ConsumerClient {

    String listen(int timeout) throws TimeoutException;

    void destroy();
}
