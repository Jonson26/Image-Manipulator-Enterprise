package com.example.jamroga.ime.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("magentaDeepFry")
@Slf4j
public class ImageManipulator{
    
    public Color processPixel(int x, int y, BufferedImage image){
        return new Color((image.getRGB(x, y)+ Color.MAGENTA.getRGB())/2);
    }
    
    public OutputContainer processImage(BufferedImage image, String filename){
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB), 
            filename);
        
        Thread t = new Thread(() -> {
            log.atInfo().log(
                String.format("Processing %d by %d image without multithreading", image.getWidth(), image.getHeight()));
            
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    output.getImage().setRGB(x, y, processPixel(x, y, image).getRGB());
                }
            }
            
            output.finish();
        });
        
        t.start();
        
        return output;
    }

    public OutputContainer processImageWithExecutorService(BufferedImage image, String filename){
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB),
            filename);
        
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
                            output.getImage().setRGB(finalX, finalY, processPixel(finalX, finalY, image).getRGB());
                            return 0;
                        };
                        tasks.add(c);
                    }
                }
                executorService.invokeAll(tasks);
                output.finish();
            } catch(Exception e) {
                log.atError().log(e.getMessage());
            }
        });

        t.start();
        
        return output;
    }

    public OutputContainer processImageWithThreads(BufferedImage image, String filename){
        OutputContainer output = new OutputContainer(
            new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB),
            filename);
        log.atInfo().log(
            String.format("Processing %d by %d image with multithreading", image.getWidth(), image.getHeight()));

        Thread t = new Thread(() -> {
            try {
                for (int x = 0; x < image.getWidth(); x++) {
                    new ImageManipulatorThread(image, output, x).start();
                }
            } catch(Exception e) {
                log.atError().log(e.getMessage());
            }
        });

        t.start();

        return output;
    }
    
    public int setParameter(String parameter, String value){
        log.atWarn().log(String.format("Attempted to set parameter %s to %s with unimplemented setParameter method", parameter, value));
        return -1;
    }

    public static boolean isWithinBounds(int x, int y, BufferedImage image) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }
    
    @AllArgsConstructor
    private class ImageManipulatorThread extends Thread{
        private final BufferedImage image;
        private final OutputContainer output;
        private final int x;
        
        @Override
        public void run() {
            for (int y=0; y < image.getHeight(); y++) {
                output.getImage().setRGB(x, y, processPixel(x, y, image).getRGB());
            }
            output.incrementProcessCounter();
            if(output.getProcessCounter().get()==image.getWidth()){
                output.finish();
            }
        }
    }
}
