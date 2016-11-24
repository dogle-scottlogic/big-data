package com.scottlogic.kafkapoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("producer")
class Producer implements DisposableBean{

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
    private ProducerClient producerClient;
    @Value("${messages}")
    private int amountToSend;
    @Value("${rate}")
    private int ratePerSecond;
    @Value("${batchSize}")
    private int batchSize;
    private int chunkSize;

    private int counter;
    private long lastMillis;
    private long startTime;
    private List<Long> chunkTimes;
    private boolean interrupted;


    Producer(ProducerClient producerClient) {
        this.producerClient = producerClient;
    }

    @PostConstruct
    public void init() {
        this.chunkSize = amountToSend / 10;

        this.counter = 1;
        this.chunkTimes = new ArrayList<>();
        this.interrupted = false;
    }

    void sendMessages(){
        LOG.info(String.format("Sending %s messages at a rate of ~%s/sec in batches of %s", amountToSend, ratePerSecond, batchSize));
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
        counter--;
        outputStats();
        counter = 1;
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
        if (counter > 1) {
            outputStats();
        }
    }

    private void outputStats() {
        long endTime = System.currentTimeMillis();
        LOG.info("Messages sent: {}", counter);
        LOG.info("Time to send: {} milliseconds", endTime - startTime);
        LOG.info("Overall real rate: {}/sec", getRate(startTime, endTime, counter));
        LOG.info("Chunk rates: ");
        for (int i = 1; i < chunkTimes.size(); i++) {
            long chunkRate = getRate(chunkTimes.get(i-1), chunkTimes.get(i), chunkSize);
            LOG.info("Chunk {}: {}/sec", i, chunkRate);
        }
    }

    private long getRate(long startTime, long endTime, int number) {
        long elapsed = (endTime - startTime);
        if (elapsed == 0) {
            elapsed = 1;
        }
        return number*1000/elapsed;
    }
}