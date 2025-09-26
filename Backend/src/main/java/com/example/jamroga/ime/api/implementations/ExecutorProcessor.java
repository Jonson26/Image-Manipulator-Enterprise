package com.example.jamroga.ime.api.implementations;

import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ExecutorProcessor implements ImageProcessor {
    @Override
    public OutputContainer processImage(BufferedImage image, PixelProcessor pixelProcessor, String filename) {
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB),
            filename,
            pixelProcessor.getDescription());

        log.atInfo().log(
            String.format("Processing %d by %d image with ExecutorService", image.getWidth(), image.getHeight()));

        Thread t = new Thread(() -> {
            try {
                ExecutorService executorService = Executors.newCachedThreadPool();
                List<Callable<Integer>> tasks = new ArrayList<>();
                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        final int finalX = x;
                        final int finalY = y;
                        Callable<Integer> c = () -> {
                            output.getImage().setRGB(
                                finalX, finalY, pixelProcessor.processPixel(finalX, finalY, image).getRGB());
                            return 0;
                        };
                        tasks.add(c);
                    }
                }
                executorService.invokeAll(tasks);
                output.finish();
            } catch(Exception e) {
                log.atError().log(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        t.start();

        return output;
    }

    @Override
    public String getName() {
        return "executor";
    }

    @Override
    public String getDescription() {
        return "ExecutorService";
    }
}
