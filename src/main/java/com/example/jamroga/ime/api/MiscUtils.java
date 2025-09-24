package com.example.jamroga.ime.api;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.Objects;

import static java.awt.Transparency.TRANSLUCENT;

@Slf4j
public class MiscUtils {
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
    
    private MiscUtils() {}

    public static String imgToBase64String(final RenderedImage img) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static boolean isWithinBounds(int x, int y, BufferedImage image) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }

    public static List<Pixel> loadPalette(InputStream is){
        ArrayList<Pixel> out = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            assert Objects.equals(line, "JASC-PAL");
            line = br.readLine();
            assert Objects.equals(line, "0100");
            line = br.readLine();
            int limit = Integer.parseInt(line);

            for(int i=0; i<limit; i++){
                line = br.readLine();
                String[] values = line.split("\\s+");
                int red = Integer.parseInt(values[0]);
                int green = Integer.parseInt(values[1]);
                int blue = Integer.parseInt(values[2]);
                out.add(new Pixel(red,green,blue));
            }
        }catch (Exception e){
            log.error(e.toString());
        }
        return out;
    }

    public static double pythagoras3d (int x1, int y1, int z1, int x2, int y2,int z2){
        double l1 = (x1-x2)*(x1-x2)*1.0 + (y1-y2)*(y1-y2)*1.0;
        return Math.sqrt((z1-z2)*(z1-z2)+l1);
    }

    public static Pixel nearestNeigbour(List<Pixel> palette, int red, int green, int blue) {
        int lowestIndex = -1;
        double lowestDistance = Double.MAX_VALUE;
        for(int i=0;i<palette.size();i++){
            Pixel element = palette.get(i);
            double distance = MiscUtils.pythagoras3d(red, green, blue, element.red(), element.green(), element.blue());
            if(distance < lowestDistance){
                lowestDistance = distance;
                lowestIndex = i;
            }
        }
        return palette.get(lowestIndex);
    }

    public static int dither(int x, int y){
        x = x%8;
        y = y%8;

        return DITHER_MATRIX[x][y];
    }

    public static BufferedImage getScaledImage(BufferedImage src, int w, int h){
        int finalw = w;
        int finalh = h;
        double factor;
        if(src.getWidth() > src.getHeight()){
            factor = ((double)src.getHeight()/(double)src.getWidth());
            finalh = (int)(finalw * factor);
        }else{
            factor = ((double)src.getWidth()/(double)src.getHeight());
            finalw = (int)(finalh * factor);
        }

        BufferedImage resizedImg = new BufferedImage(finalw, finalh, TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, finalw, finalh, null);
        g2.dispose();
        return resizedImg;
    }
    
    public static BufferedImage generateFallbackImage(){
        BufferedImage out = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        
        int curX = 0;
        int curY = 0;
        for (int rVal = 0; rVal < 16; rVal++){
            for (int gVal = 0; gVal < 16; gVal++){
                for (int bVal = 0; bVal < 16; bVal++){
                    Color curColor = new Color(rVal*16, gVal*16, bVal*16);
                    out.setRGB(curX, curY, curColor.getRGB());
                    curX++;
                    if (curX >= 64){
                        curX = 0;
                        curY++;
                    }
                }
            }
        }
        
        return out;
    }
}
