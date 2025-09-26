package com.example.jamroga.ime.api.interfaces;

import com.example.jamroga.ime.api.OutputContainer;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public interface ImageProcessor {
    OutputContainer processImage(BufferedImage image, PixelProcessor pixelProcessor, String filename);

    String getName();

    String getDescription();
}
