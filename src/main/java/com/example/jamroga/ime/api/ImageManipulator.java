package com.example.jamroga.ime.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ImageManipulator {
    private static final Logger log = LoggerFactory.getLogger(ImageManipulator.class);
    
    public Color processPixel(int x, int y, BufferedImage image){
        return new Color((image.getRGB(x, y)+ Color.MAGENTA.getRGB())/2);
    }
    
    public BufferedImage processImage(BufferedImage image){
        log.atInfo().log(
            String.format("Processing %d by %d image without multithreading", image.getWidth(), image.getHeight()));
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                output.setRGB(x, y, processPixel(x, y, image).getRGB());
            }
        }
        
        return output;
    }
    
    public int setParameter(String parameter, String value){
        log.atWarn().log(String.format("Attempted to set parameter %s to %s with unimplemented setParameter method", parameter, value));
        return -1;
    }

    public static boolean isWithinBounds(int x, int y, BufferedImage image) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }
}
