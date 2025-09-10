package com.example.Jamroga.IME.API;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ImageManipulator {
    public Color processPixel(int x, int y, BufferedImage image){
        return new Color((image.getRGB(x, y)+ Color.MAGENTA.getRGB())/2);
    }
    
    public BufferedImage processImage(BufferedImage image){
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                output.setRGB(x, y, processPixel(x, y, image).getRGB());
            }
        }
        
        return output;
    }
}
