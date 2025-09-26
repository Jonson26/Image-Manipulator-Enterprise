package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class MagentaDeepFry implements PixelProcessor {
    @Override
    public Color processPixel(int x, int y, BufferedImage image) {
        return new Color((image.getRGB(x, y)+ Color.MAGENTA.getRGB())/2);
    }

    @Override
    public String getName() {
        return "magenta";
    }

    @Override
    public String getDescription() {
        return "Magenta Deep Fry";
    }
}
