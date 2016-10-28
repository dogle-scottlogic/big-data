package com.scottlogic.kafkapoc;

import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.List;

class Producer implements DisposableBean{

    private ProducerClient producerClient;
    private int amountToSend;
    private int ratePerSecond;
    private int batchSize;
    private int chunkSize;

    private int counter;
    private long lastMillis;
    private long startTime;
    private List<Long> chunkTimes;
    private boolean interrupted;

    Producer(ProducerClient producerClient, int amountToSend, int ratePerSecond, int batchSize){
        this.producerClient = producerClient;
        this.amountToSend = amountToSend;
        this.ratePerSecond = ratePerSecond;
        this.batchSize = batchSize;
        this.chunkSize = amountToSend / 10;

        this.counter = 1;
        this.chunkTimes = new ArrayList<>();
        this.interrupted = false;
    }

    void sendMessages(){
        System.out.println(String.format("Sending %s messages at a rate of ~%s/sec in batches of %s", amountToSend, ratePerSecond, batchSize));
        startTime = System.currentTimeMillis();
        chunkTimes.add(startTime);
        lastMillis = startTime;
        while (counter <= amountToSend && !interrupted) {
            producerClient.send(Integer.toString(counter));
            counter++;
            if (counter % batchSize == 0) {
                maybeSleep();
                lastMillis = System.currentTimeMillis();
            }
            if (counter % chunkSize == 0) {
                chunkTimes.add(System.currentTimeMillis());
            }
        }
        outputStats();
        counter = 0;
    }

    private void maybeSleep() {
        long millisToWait = (long) Math.ceil((1000d/ratePerSecond) * batchSize);
        long currentMillis = System.currentTimeMillis();
        if (currentMillis < lastMillis + millisToWait) {
            sleep(lastMillis + millisToWait - currentMillis);
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        interrupted = true;
        producerClient.destroy();
        if (counter > 0) {
            outputStats();
        }
    }

    private void outputStats() {
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Messages sent: %s", counter));
        System.out.println(String.format("Time to send: %s milliseconds", endTime - startTime));
        System.out.println(String.format("Overall real rate: %s/sec", getRate(startTime, endTime, counter)));
        System.out.println("Chunk rates: ");
        for (int i = 1; i < chunkTimes.size(); i++) {
            long chunkRate = getRate(chunkTimes.get(i-1), chunkTimes.get(i), chunkSize);
            System.out.println(String.format("Chunk %s: %s/sec", i, chunkRate));
        }
    }

    private long getRate(long startTime, long endTime, int number) {
        long elapsed = (endTime - startTime);
        return number*1000/elapsed;
    }
}