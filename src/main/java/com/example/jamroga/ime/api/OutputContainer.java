package com.example.jamroga.ime.api;

import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class OutputContainer {
    private final BufferedImage image;
    private final String filename;
    private final String effect;
    private boolean finished;
    private final AtomicInteger processCounter = new AtomicInteger(0);
    
    public OutputContainer(BufferedImage image, String filename, String effect) {
        this.image = image;
        this.filename = filename;
        this.effect = effect;
        finished = false;
    }
    
    public void finish() {
        finished = true;
    }
    
    public void incrementProcessCounter() {
        processCounter.incrementAndGet();
    }
}
