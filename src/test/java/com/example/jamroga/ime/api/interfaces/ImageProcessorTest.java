package com.example.jamroga.ime.api.interfaces;

import com.example.jamroga.ime.api.MiscUtils;
import com.example.jamroga.ime.api.OutputContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class ImageProcessorTest {
    @Autowired
    List<ImageProcessor> imageProcessors;
    
    @Autowired
    List<PixelProcessor> pixelProcessors;
    
    BufferedImage referenceImage = MiscUtils.generateFallbackImage();
    
    @Test
    void testIfAllImageProcessorsBehaveTheSameForAllPixelProcessors() {
        assertFalse(imageProcessors.isEmpty());
        assertFalse(pixelProcessors.isEmpty());
        for(PixelProcessor pixelProcessor : pixelProcessors) {
            log.info("Testing with PixelProcessor {}", pixelProcessor);
            List<OutputContainer> images = new ArrayList<>();
            for (ImageProcessor imageProcessor : imageProcessors) {
                log.info("Applying ImageProcessor {}", imageProcessor);
                images.add(imageProcessor.processImage(referenceImage, pixelProcessor, ""));
            }
            try {
                while (!images.stream().allMatch(OutputContainer::isFinished)) Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("Interrupted", e);
                fail("There should not be any exception");
            }
            assertTrue(images.stream().allMatch(
                image -> compareImages(image.getImage(), images.getFirst().getImage())
            ));
        }
    }

    /**
     * Compares two images pixel by pixel.
     *
     * @param imgA the first image.
     * @param imgB the second image.
     * @return whether the images are both the same or not.
     */
    public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
        // The images must be the same size.
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width  = imgA.getWidth();
        int height = imgA.getHeight();

        // Loop over every pixel.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Compare the pixels for equality.
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }
}
