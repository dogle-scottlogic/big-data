package com.scottlogic.kafkapoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.List;

class Consumer implements DisposableBean, Listener {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);
    private ConsumerClient consumerClient;
    private List<Integer> numbers;
    private int expected;
    private int chunkSize;

    private long startTime;
    private List<Long> chunkTimes;
    private boolean interrupted;

    Consumer(ConsumerClient consumerClient, int expected) {
        this.consumerClient = consumerClient;
        numbers = new ArrayList<>();
        this.expected = expected;
        this.chunkTimes = new ArrayList<>();
        this.chunkSize = expected / 10;
    }

    void startListening() {
        LOG.info("Listening for {} messages.", expected);
        startTime = System.currentTimeMillis();
        chunkTimes.add(startTime);
        consumerClient.setListener(this);
    }

    @Override
    public void onMessage(String message) {
        if (message != null) {
            numbers.add(Integer.valueOf(message));
        }
        if ((numbers.size() + 1) % chunkSize == 0) {
            chunkTimes.add(System.currentTimeMillis());
        }
        if ((numbers.size()) >= expected || interrupted) {
            outputStats();
            numbers = new ArrayList<>();
            chunkTimes = new ArrayList<>();
            chunkTimes.add(startTime);
        }
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
        LOG.info("Overall rate: {}/sec", getRate(startTime, endTime, numbers.size()));
        LOG.info("Chunk rates: ");
        for (int i = 1; i < chunkTimes.size(); i++) {
            long chunkRate = getRate(chunkTimes.get(i-1), chunkTimes.get(i), chunkSize);
            LOG.info("Chunk {}: {}/sec", i, chunkRate);
        }
    }

    private long getRate(long startTime, long endTime, int number) {
        long elapsed = (endTime - startTime);
        return number*1000/elapsed;
    }

    @Override
    public void destroy() throws Exception {
        interrupted = true;
        this.consumerClient.destroy();
        if (!numbers.isEmpty()) {
            outputStats();
        }
    }
}
