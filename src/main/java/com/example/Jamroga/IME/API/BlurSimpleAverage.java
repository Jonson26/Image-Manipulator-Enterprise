package com.example.Jamroga.IME.API;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component("blur")
public class BlurSimpleAverage extends ImageManipulator{
    public int INTENSITY = 4;
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        
        int counter = 0;
        
        for(int i=-INTENSITY; i<=INTENSITY; i++){
            for(int j=-INTENSITY; j<=INTENSITY; j++){
                if(isWithinBounds(x+i, y+j, image)){
                    int argb = image.getRGB(x+i, y+j);
                    
                    red += (argb >> 16) & 0xFF;
                    green += (argb >> 8) & 0xFF;
                    blue += argb & 0xFF;
                    alpha += (argb >> 24) & 0xFF;

                    counter++;
                }
            }
        }
        
        red = red / counter;
        green = green / counter;
        blue = blue / counter;
        alpha = alpha / counter;
        
        return new Color(red, green, blue, alpha);
    }
    
    private boolean isWithinBounds(int x, int y, BufferedImage image) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }
}
