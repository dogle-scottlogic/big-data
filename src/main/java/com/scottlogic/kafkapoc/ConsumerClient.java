package com.scottlogic.kafkapoc;

public interface ConsumerClient {

    void setListener(Listener listener);

    void destroy();
}
