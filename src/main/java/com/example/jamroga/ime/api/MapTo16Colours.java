package com.example.jamroga.ime.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component("palette16")
public class MapTo16Colours extends ImageManipulator{
    private static final Logger log = LoggerFactory.getLogger(MapTo16Colours.class);
    private static final PaletteElement[] palette = {
        new PaletteElement(  0,   0,  0), //  1 - Black
        new PaletteElement(255, 255,255), //  2 - White
        new PaletteElement(255,   0,  0), //  3 - Red
        new PaletteElement(  0, 255,  0), //  4 - Lime
        new PaletteElement(  0,   0,255), //  5 - Blue
        new PaletteElement(255, 255,  0), //  6 - Yellow
        new PaletteElement(  0, 255,255), //  7 - Cyan
        new PaletteElement(255,   0,255), //  8 - Magenta
        new PaletteElement(192, 192,192), //  9 - Silver
        new PaletteElement(128, 128,128), // 10 - Gray
        new PaletteElement(128,   0,  0), // 11 - Maroon
        new PaletteElement(128, 128,  0), // 12 - Olive
        new PaletteElement(  0, 128,  0), // 13 - Green
        new PaletteElement(128,   0,128), // 14 - Purple
        new PaletteElement(  0, 128,128), // 15 - Teal
        new PaletteElement(  0,   0,128)  // 16 - Navy
    };
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image){
        int argb = image.getRGB(x, y);

        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        int alpha = (argb >> 24) & 0xFF;
        
        int lowestIndex = -1;
        double lowestDistance = Double.MAX_VALUE;
        for(int i=0;i<palette.length;i++){
            PaletteElement element = palette[i];
            double distance = pythagoras3d(red, green, blue, element.red, element.green, element.blue);
            if(distance < lowestDistance){
                lowestDistance = distance;
                lowestIndex = i;
            }
        }
        
        PaletteElement element = palette[lowestIndex];
        red = element.red;
        green = element.green;
        blue = element.blue;
        
        return new Color(red,green,blue,alpha);
    }
    
    private double pythagoras3d (int x1, int y1, int z1, int x2, int y2,int z2){
        double l1 = (x1-x2)*(x1-x2)*1.0 + (y1-y2)*(y1-y2)*1.0;
        return Math.sqrt((z1-z2)*(z1-z2)+l1);
    }
    
    private static class PaletteElement {
        final int red;
        final int green;
        final int blue;

        public PaletteElement(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
