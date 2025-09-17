package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

@Component
@Slf4j
public class StreamProcessor implements ImageProcessor {
    @Override
    public OutputContainer processImage(BufferedImage image, PixelProcessor pixelProcessor, String filename) {
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB),
            filename);

        Thread t = new Thread(() -> {
            log.atInfo().log(
                String.format("Processing %d by %d image with parallel streams", image.getWidth(), image.getHeight()));

            IntStream.range(0, image.getWidth()).parallel().forEach(
                x -> IntStream.range(0, image.getHeight()).parallel().forEach(
                    y -> {
                        output.getImage().setRGB(x, y, pixelProcessor.processPixel(x, y, image).getRGB());
                        output.incrementProcessCounter();
                        if(image.getWidth()*image.getHeight() == output.getProcessCounter().get()) output.finish();
                    }
                )
            );
        });

        t.start();

        return output;
    }

    @Override
    public String getName() {
        return "stream";
    }

    @Override
    public String getDescription() {
        return "Parallel Stream";
    }
}
