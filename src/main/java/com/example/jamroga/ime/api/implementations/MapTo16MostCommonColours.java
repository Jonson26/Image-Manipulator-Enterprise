package com.example.jamroga.ime.api.implementations;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.jamroga.ime.api.interfaces.PixelProcessor;

import lombok.extern.slf4j.Slf4j;

import static java.util.Map.*;

@Component
@Slf4j
public class MapTo16MostCommonColours implements PixelProcessor {
    private static final int[][] DITHER_MATRIX = new int[][]{
        { 0, 32,  8, 40,  2, 34, 10, 42},
        {48, 16, 56, 24, 50, 18, 58, 26},
        {12, 44,  4, 36, 14, 46,  6, 38},
        {60, 28, 52, 20, 62, 30, 54, 22},
        { 3, 35, 11, 43,  1, 33,  9, 41},
        {51, 19, 59, 27, 49, 17, 57, 25},
        {15, 47,  7, 39, 13, 45,  5, 37},
        {63, 31, 55, 23, 61, 29, 53, 21}
    };
    
    private final ArrayList<Pixel> palette = new ArrayList<>();
    
    private BufferedImage lastSampledImage;
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
        updatePalette(image);
        int argb = image.getRGB(x, y);

        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        int alpha = (argb >> 24) & 0xFF;

        int offset = dither(x, y);

        Pixel element = nearestNeigbour(red+offset, green+offset, blue+offset);
        red = element.red();
        green = element.green();
        blue = element.blue();

        return new Color(red,green,blue,alpha);
    }

    private Pixel nearestNeigbour(int red, int green, int blue) {
        int lowestIndex = 0;
        double lowestDistance = Double.MAX_VALUE;
        for(int i=0;i<palette.size();i++){
            Pixel element = palette.get(i);
            double distance = pythagoras3d(red, green, blue, element.red(), element.green(), element.blue());
            if(distance < lowestDistance){
                lowestDistance = distance;
                lowestIndex = i;
            }
        }
        return palette.get(lowestIndex);
    }

    private double pythagoras3d (int x1, int y1, int z1, int x2, int y2,int z2){
        double l1 = (x1-x2)*(x1-x2)*1.0 + (y1-y2)*(y1-y2)*1.0;
        return Math.sqrt((z1-z2)*(z1-z2)+l1);
    }

    private int dither(int x, int y){
        x = x%8;
        y = y%8;

        return DITHER_MATRIX[x][y];
    }
    
    private Pixel trimPixel(int argb){
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        
        int bitmask = 0b11111100;
        red   &= bitmask;
        green &= bitmask;
        blue  &= bitmask;
        
        return new Pixel(red,green,blue);
    }
    
    private synchronized void updatePalette(BufferedImage image){
        if(image == lastSampledImage) return;
        palette.clear();
        
        HashMap<Pixel, Integer> colorMap = new HashMap<>();
        for(int x=0; x<image.getWidth(); x++){
            for(int y=0; y<image.getHeight(); y++){
                Pixel p = trimPixel(image.getRGB(x,y));
                if(colorMap.containsKey(p)){
                    colorMap.put(p, colorMap.get(p)+1);
                }else{
                    colorMap.put(p, 1);
                }
            }
        }
        HashMap<Pixel, Integer> sortedColorMap = sortByValue(colorMap);
        
        for (Map.Entry<Pixel, Integer> en : sortedColorMap.entrySet()) {
            palette.add(en.getKey());
            if(palette.size() == 16) break;
        }
        lastSampledImage = image;
    }
    
    private static HashMap<Pixel, Integer> sortByValue(HashMap<Pixel, Integer> hm) {
        List<Map.Entry<Pixel, Integer> > list = new LinkedList<>(hm.entrySet());

        list.sort(Entry.comparingByValue());

        HashMap<Pixel, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Pixel, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    @Override
    public String getName() {
        return "mapto16mostc";
    }

    @Override
    public String getDescription() {
        return "Map image to 16 most common colours";
    }

    private record Pixel(int red, int green, int blue) {
    }
}
