package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.MiscUtils;
import com.example.jamroga.ime.api.Pixel;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
public class MapTo16Colours implements PixelProcessor {
    private final List<Pixel> palette;

    public MapTo16Colours() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("static/default.pal");
        palette = MiscUtils.loadPalette(is);
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

    @Override
    public String getName() {
        return "mapto16c";
    }

    @Override
    public String getDescription() {
        return "Map image to 16 colour palette";
    }
}
