package com.example.jamroga.ime.api;

import lombok.Getter;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class OutputContainer {
    public static final int SHELFLIFE_IN_MINUTES = 15;
    private final BufferedImage image;
    private final String filename;
    private final String effect;
    private boolean finished;
    private boolean permanent;
    private Instant finishTime;
    private final AtomicInteger processCounter = new AtomicInteger(0);
    
    public OutputContainer(BufferedImage image, String filename, String effect) {
        this.image = image;
        this.filename = filename;
        this.effect = effect;
        finished = false;
        permanent = false;
    }
    
    public void finish() {
        finishTime = Instant.now();
        finished = true;
    }
    
    public void makePermanent() {
        permanent = true;
    }
    
    public boolean isExpired(){
        if(permanent || !finished){
            return false;
        }else{
            Instant now = Instant.now();
            long timeElapsed = Duration.between(finishTime, now).toMinutes();
            return timeElapsed >= SHELFLIFE_IN_MINUTES;
        }
    }
    
    public void incrementProcessCounter() {
        processCounter.incrementAndGet();
    }
}
