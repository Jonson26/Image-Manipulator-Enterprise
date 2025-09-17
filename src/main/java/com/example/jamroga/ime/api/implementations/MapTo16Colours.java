package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@Component
@Slf4j
public class MapTo16Colours implements PixelProcessor {
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
    
    private final Pixel[] palette = {
        new Pixel(  0,   0,  0), //  1 - Black
        new Pixel(255, 255,255), //  2 - White
        new Pixel(255,   0,  0), //  3 - Red
        new Pixel(  0, 255,  0), //  4 - Lime
        new Pixel(  0,   0,255), //  5 - Blue
        new Pixel(255, 255,  0), //  6 - Yellow
        new Pixel(  0, 255,255), //  7 - Cyan
        new Pixel(255,   0,255), //  8 - Magenta
        new Pixel(192, 192,192), //  9 - Silver
        new Pixel(128, 128,128), // 10 - Gray
        new Pixel(128,   0,  0), // 11 - Maroon
        new Pixel(128, 128,  0), // 12 - Olive
        new Pixel(  0, 128,  0), // 13 - Green
        new Pixel(128,   0,128), // 14 - Purple
        new Pixel(  0, 128,128), // 15 - Teal
        new Pixel(  0,   0,128)  // 16 - Navy
    };

    public MapTo16Colours() {
        loadPalette();
    }
    
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
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
        int lowestIndex = -1;
        double lowestDistance = Double.MAX_VALUE;
        for(int i=0;i<palette.length;i++){
            Pixel element = palette[i];
            double distance = pythagoras3d(red, green, blue, element.red(), element.green(), element.blue());
            if(distance < lowestDistance){
                lowestDistance = distance;
                lowestIndex = i;
            }
        }
        return palette[lowestIndex];
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

    private void loadPalette(){
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("static/default.pal");
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            assert Objects.equals(line, "JASC-PAL");
            line = br.readLine();
            assert Objects.equals(line, "0100");
            line = br.readLine();
            assert Objects.equals(line, "16");

            for(int i=0; i<16; i++){
                line = br.readLine();
                String[] values = line.split("\\s+");
                int red = Integer.parseInt(values[0]);
                int green = Integer.parseInt(values[1]);
                int blue = Integer.parseInt(values[2]);
                palette[i] = new Pixel(red,green,blue);
            }
        }catch (Exception e){
            log.error(e.toString());
        }
    }

    @Override
    public String getName() {
        return "mapto16c";
    }

    @Override
    public String getDescription() {
        return "Map image to 16 colour palette";
    }

    private record Pixel(int red, int green, int blue) {
    }
}
