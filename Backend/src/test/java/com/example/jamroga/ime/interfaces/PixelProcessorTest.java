package com.example.jamroga.ime.interfaces;

import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.implementations.*;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.image.BufferedImage;

import static com.example.jamroga.ime.interfaces.ProcessorTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
public class PixelProcessorTest {
    public static final String IMAGE_BASE = "testdata/moka_mini.png";
    public static final String IMAGE_BLUR_REFERENCE = "testdata/moka_mini_blur.png";
    public static final String IMAGE_MAGENTA_REFERENCE = "testdata/moka_mini_magenta.png";
    public static final String IMAGE_MAPTO16C_REFERENCE = "testdata/moka_mini_mapto16c.png";
    public static final String IMAGE_MAPTO16G_REFERENCE = "testdata/moka_mini_mapto16g.png";
    public static final String IMAGE_MAPTO16MOSTC_REFERENCE = "testdata/moka_mini_mapto16mostc.png";
    public static final String DUMMY_FILENAME = "dummy.png";

    @Autowired
    ClassicProcessor classicProcessor;

    @Autowired
    BlurSimpleAverage blurSimpleAverage;

    @Autowired
    MagentaDeepFry magentaDeepFry;

    @Autowired
    MapTo16Colours mapTo16Colours;

    @Autowired
    MapTo16Grays mapTo16Grays;

    @Autowired
    MapTo16MostCommonColours mapTo16MostCommonColours;

    BufferedImage baseImage = loadImage(IMAGE_BASE);

    @Test
    void blurSimpleAverageTest() {
        BufferedImage expected = loadImage(IMAGE_BLUR_REFERENCE);
        BufferedImage actual = processBaseImage(blurSimpleAverage);
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(compareImages(expected, actual));
    }

    @Test
    void magentaDeepFryTest() {
        BufferedImage expected = loadImage(IMAGE_MAGENTA_REFERENCE);
        BufferedImage actual = processBaseImage(magentaDeepFry);
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(compareImages(expected, actual));
    }

    @Test
    void mapTo16ColoursTest() {
        BufferedImage expected = loadImage(IMAGE_MAPTO16C_REFERENCE);
        BufferedImage actual = processBaseImage(mapTo16Colours);
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(compareImages(expected, actual));
    }

    @Test
    void mapTo16GraysTest() {
        BufferedImage expected = loadImage(IMAGE_MAPTO16G_REFERENCE);
        BufferedImage actual = processBaseImage(mapTo16Grays);
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(compareImages(expected, actual));
    }

    @Test
    void mapTo16MostCommonColoursTest() {
        BufferedImage expected = loadImage(IMAGE_MAPTO16MOSTC_REFERENCE);
        BufferedImage actual = processBaseImage(mapTo16MostCommonColours);
        assertNotNull(expected);
        assertNotNull(actual);
        assertTrue(compareImages(expected, actual));
    }

    private BufferedImage processBaseImage(PixelProcessor pixelProcessor){
        OutputContainer out = classicProcessor.processImage(baseImage, pixelProcessor, DUMMY_FILENAME);
        while (!out.isFinished()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return out.getImage();
    }
}
