package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
@Slf4j
public class ClassicProcessor implements ImageProcessor {
    @Override
    public OutputContainer processImage(BufferedImage image, PixelProcessor pixelProcessor, String filename) {
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB),
            filename);

        Thread t = new Thread(() -> {
            log.atInfo().log(
                String.format("Processing %d by %d image without multithreading", image.getWidth(), image.getHeight()));

            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    output.getImage().setRGB(x, y, pixelProcessor.processPixel(x, y, image).getRGB());
                }
            }

            output.finish();
        });

        t.start();

        return output;
    }

    @Override
    public String getName() {
        return "classic";
    }

    @Override
    public String getDescription() {
        return "Linear";
    }
}
