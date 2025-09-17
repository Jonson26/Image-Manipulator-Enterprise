package com.example.jamroga.ime.api.interfaces;

import com.example.jamroga.ime.api.OutputContainer;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
    OutputContainer processImage(BufferedImage image, PixelProcessor pixelProcessor, String filename);

    String getName();

    String getDescription();
}
