package com.example.jamroga.ime.api.interfaces;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface PixelProcessor {
    Color processPixel(int x, int y, BufferedImage image);
    
    String getName();
    
    String getDescription();
}
