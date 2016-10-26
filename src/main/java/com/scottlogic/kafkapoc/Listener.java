package com.scottlogic.kafkapoc;

public interface Listener {

    void onReceiveMessage(String message);
}
