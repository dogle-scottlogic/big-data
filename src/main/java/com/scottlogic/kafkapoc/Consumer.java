package com.scottlogic.kafkapoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.List;

class Consumer implements DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);
    private ConsumerClient consumerClient;
    private List<Integer> numbers;
    private int expected;
    private int ratePerSecond;
    private int timeout;
    private int batchSize;
    private int chunkSize;

    private long lastMillis;
    private long startTime;
    private List<Long> chunkTimes;
    private boolean interrupted;

    Consumer(ConsumerClient consumerClient, int expected, int timeout, int ratePerSecond, int batchSize) {
        this.consumerClient = consumerClient;
        numbers = new ArrayList<>();
        this.expected = expected;
        this.timeout = timeout;
        this.ratePerSecond = ratePerSecond;
        this.chunkTimes = new ArrayList<>();
        this.batchSize = batchSize;
        this.chunkSize = expected / 10;
    }

    void startListening() {
        LOG.info("Listening for {} messages at a rate of {}/sec, with a timeout of {}", expected, ratePerSecond, timeout);
        startTime = System.currentTimeMillis();
        chunkTimes.add(startTime);
        lastMillis = startTime;
        while ((numbers.size() + 1) <= expected && !interrupted) {
            String message = listen();
            if (message != null) {
                numbers.add(Integer.valueOf(message));
            }
            if ((numbers.size() + 1) % batchSize == 0) {
                maybeSleep();
                lastMillis = System.currentTimeMillis();
            }
            if ((numbers.size() + 1) % chunkSize == 0) {
                chunkTimes.add(System.currentTimeMillis());
            }
        }
        outputStats();
        numbers = new ArrayList<>();
    }

    private String listen() {
        String message = null;
        try {
            message = consumerClient.listen(timeout);
        } catch (TimeoutException e) {
            interrupted = true;
        }
        return message;
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

    @Override
    public void destroy() throws Exception {
        interrupted = true;
        this.consumerClient.destroy();
        if (!numbers.isEmpty()) {
            outputStats();
        }
    }

    private long getRate(long startTime, long endTime, int number) {
        long elapsed = (endTime - startTime);
        return number*1000/elapsed;
    }

    private void outputStats() {
        long endTime = System.currentTimeMillis();
        LOG.info("Messages received: {}", numbers.size());
        boolean ordered = true;
        for (int i = 0; i < numbers.size(); i++) {
            if (i + 1 != numbers.get(i)) {
                ordered = false;
            }
        }
        LOG.info("Received in order: {}", ordered);
        LOG.info("Time to receive: {} milliseconds", endTime - startTime);
        LOG.info("Overall real rate: {}/sec", getRate(startTime, endTime, numbers.size()));
        LOG.info("Chunk rates: ");
        for (int i = 1; i < chunkTimes.size(); i++) {
            long chunkRate = getRate(chunkTimes.get(i-1), chunkTimes.get(i), chunkSize);
            LOG.info("Chunk {}: {}/sec", i, chunkRate);
        }
    }
}
