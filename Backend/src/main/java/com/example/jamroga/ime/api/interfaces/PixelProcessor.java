package com.example.jamroga.ime.api.interfaces;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public interface PixelProcessor {
    Color processPixel(int x, int y, BufferedImage image);
    
    String getName();
    
    String getDescription();
}
