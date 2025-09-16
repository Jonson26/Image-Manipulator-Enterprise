package com.example.jamroga.ime.api;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component("blur")
public class BlurSimpleAverage extends ImageManipulator{
    private int intensity = 4;
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        
        int counter = 0;
        
        for(int i=-intensity; i<=intensity; i++){
            for(int j=-intensity; j<=intensity; j++){
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
        
        if(counter==0) counter=1;
        
        red = red / counter;
        green = green / counter;
        blue = blue / counter;
        alpha = alpha / counter;
        
        return new Color(red, green, blue, alpha);
    }
    
    @Override
    public int setParameter(String parameter, String value){
        parameter = parameter.toLowerCase();
        if (parameter.equals("intensity")) {
            intensity = Integer.parseInt(value);
            return 0;
        }
        return -1;
    }
}
