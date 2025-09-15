package com.example.jamroga.ime.api;

import lombok.Getter;

import java.awt.image.BufferedImage;

@Getter
public class OutputContainer {
    private final BufferedImage image;
    private final String filename;
    private boolean finished;
    private int processCounter = 0;
    
    public OutputContainer(BufferedImage image, String filename) {
        this.image = image;
        this.filename = filename;
        finished = false;
    }

    public OutputContainer(BufferedImage image, String filename, boolean finished) {
        this.image = image;
        this.filename = filename;
        this.finished = finished;
    }
    
    public void finish() {
        finished = true;
    }
    
    public void incrementProcessCounter() {
        processCounter++;
    }
}
