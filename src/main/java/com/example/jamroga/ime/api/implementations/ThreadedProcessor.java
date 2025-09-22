package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
@Slf4j
public class ThreadedProcessor implements ImageProcessor {
    @Override
    public OutputContainer processImage(BufferedImage image, PixelProcessor pixelProcessor, String filename) {
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB),
            filename,
            pixelProcessor.getDescription());
        log.atInfo().log(
            String.format("Processing %d by %d image with multithreading", image.getWidth(), image.getHeight()));

        Thread t = new Thread(() -> {
            try {
                for (int x = 0; x < image.getWidth(); x++) {
                    new ImageManipulatorThread(image, output, pixelProcessor, x).start();
                }
            } catch(Exception e) {
                log.atError().log(e.getMessage());
            }
        });

        t.start();

        return output;
    }

    @Override
    public String getName() {
        return "threaded";
    }

    @Override
    public String getDescription() {
        return "Threads";
    }

    @AllArgsConstructor
    private static class ImageManipulatorThread extends Thread{
        private final BufferedImage image;
        private final OutputContainer output;
        private final PixelProcessor pixelProcessor;
        private final int x;

        @Override
        public void run() {
            for (int y=0; y < image.getHeight(); y++) {
                output.getImage().setRGB(x, y, pixelProcessor.processPixel(x, y, image).getRGB());
            }
            output.incrementProcessCounter();
            if(output.getProcessCounter().get()==image.getWidth()){
                output.finish();
            }
        }
    }
}
