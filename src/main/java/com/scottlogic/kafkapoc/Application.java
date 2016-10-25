package com.scottlogic.kafkapoc;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        if (args[0].equals("--producer")) {
            new Producer();
        } else if (args[0].equals("--consumer")) {
            new Consumer();
        }
    }

}
