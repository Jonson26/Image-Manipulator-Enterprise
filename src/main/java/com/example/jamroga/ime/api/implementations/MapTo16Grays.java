package com.example.jamroga.ime.api.implementations;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.example.jamroga.ime.api.MiscUtils;
import com.example.jamroga.ime.api.Pixel;
import org.springframework.stereotype.Component;

import com.example.jamroga.ime.api.interfaces.PixelProcessor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MapTo16Grays implements PixelProcessor {
    private final List<Pixel> palette = new ArrayList<>();

    public MapTo16Grays() {
        generatePalette();
    }
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
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

    private void generatePalette(){
        for(int i=0; i<16; i++){
            int c = i*17;
            palette.add(new Pixel(c, c, c));
        }
    }

    @Override
    public String getName() {
        return "mapto16g";
    }

    @Override
    public String getDescription() {
        return "Map image to 16 shades of gray palette";
    }
}
