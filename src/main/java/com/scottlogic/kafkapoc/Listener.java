package com.scottlogic.kafkapoc;

public interface Listener {

    void onMessage(String message);

    void onTimeout();

}
