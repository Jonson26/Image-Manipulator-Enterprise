package com.example.jamroga.ime.interfaces;

import com.example.jamroga.ime.api.MiscUtils;
import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.example.jamroga.ime.interfaces.ProcessorTestUtil.*;
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
}
