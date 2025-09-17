package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class BlurSimpleAverage implements PixelProcessor {
    private final int intensity = 4;
    
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
    public String getName() {
        return "blur";
    }

    @Override
    public String getDescription() {
        return "Blur Image";
    }

    public static boolean isWithinBounds(int x, int y, BufferedImage image) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }
}
