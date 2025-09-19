package com.example.jamroga.ime.api.implementations;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import com.example.jamroga.ime.MiscUtils;
import com.example.jamroga.ime.api.Pixel;
import org.springframework.stereotype.Component;

import com.example.jamroga.ime.api.interfaces.PixelProcessor;

import lombok.extern.slf4j.Slf4j;

import static java.util.Map.*;

@Component
@Slf4j
public class MapTo16MostCommonColours implements PixelProcessor {
    private final List<Pixel> palette = new ArrayList<>();
    
    private BufferedImage lastSampledImage;
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
        updatePalette(image);
        int argb = image.getRGB(x, y);

        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        int alpha = (argb >> 24) & 0xFF;

        int offset = MiscUtils.dither(x, y);

        Pixel element = MiscUtils.nearestNeigbour(palette,red+offset, green+offset, blue+offset);
        red = element.red();
        green = element.green();
        blue = element.blue();

        return new Color(red,green,blue,alpha);
    }
    
    private Pixel trimPixel(int argb){
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        
        int bitmask = 0b11111111;
        red   &= bitmask;
        green &= bitmask;
        blue  &= bitmask;
        
        return new Pixel(red,green,blue);
    }
    
    private synchronized void updatePalette(BufferedImage image){
        if(image == lastSampledImage) return;
        log.atInfo().log("Reloading palette!");
        BufferedImage img = MiscUtils.getScaledImage(image, 64, 64);
        palette.clear();
        
        HashMap<Pixel, Integer> colorMap = new HashMap<>();
        for(int x=0; x<img.getWidth(); x++){
            for(int y=0; y<img.getHeight(); y++){
                Pixel p = trimPixel(img.getRGB(x,y));
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
}
