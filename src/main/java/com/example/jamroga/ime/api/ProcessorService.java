package com.example.jamroga.ime.api;

import com.example.jamroga.ime.api.implementations.BlurSimpleAverage;
import com.example.jamroga.ime.api.implementations.ClassicProcessor;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
public class ProcessorService {
    public static final String CONVERTING_FILE_MESSAGE = "Converting file %s [Effect is: %s, Option is: %s]";
    public static final String CONVERTED_FILE_TAG = "%s [%s]";
    public static final String MOKA_LOCATION = "static/moka_mini.png";
    public static final String MOKA_FILENAME = "moka_mini.png";
    
    private final List<ImageProcessor> imageProcessors;
    
    private final List<PixelProcessor> pixelProcessors;
    
    private final ArrayList<OutputContainer> convertedImages = new ArrayList<>();
    
    @Autowired
    public ProcessorService(List<ImageProcessor> imageProcessors, List<PixelProcessor> pixelProcessors) {
        this.imageProcessors = imageProcessors;
        this.pixelProcessors = pixelProcessors;
        
        try {
            URL url = ProcessorService.class.getClassLoader().getResource(MOKA_LOCATION);
            assert url != null;
            BufferedImage img = ImageIO.read(url);
            OutputContainer out = new ClassicProcessor()
                .processImage(img, new BlurSimpleAverage(), MOKA_FILENAME);
            out.makePermanent();
            convertedImages.add(out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    public List<MenuElement> getImageProcessorMenuElements() {
        return imageProcessors.stream()
            .map(ip -> new MenuElement(ip.getName(), ip.getDescription()))
            .toList();
    }

    public List<MenuElement> getPixelProcessorMenuElements() {
        return pixelProcessors.stream()
            .map(ip -> new MenuElement(ip.getName(), ip.getDescription()))
            .toList();
    }

    public int processImage(String dir, String filename, String effect, String options) throws IOException {
        int index = 0;
        URL url = Path.of(dir+"/"+filename).toUri().toURL();

        log.atInfo().log(String.format(CONVERTING_FILE_MESSAGE, url.getFile(), effect, options));

        try{
            BufferedImage img = ImageIO.read(url);

            OutputContainer out = findImageProcessor(options).processImage(
                img,
                findPixelProcessor(effect),
                filename
            );

            convertedImages.add(out);
            index = convertedImages.indexOf(out);
        } catch (IOException e) {
            log.atWarn().log(e.toString());
        }
        return index;
    }
    
    public List<MenuElement> getProcessedImageListing(){
        List<MenuElement> listing = new ArrayList<>();
        for(int i=0; i<convertedImages.size(); i++){
            OutputContainer img = convertedImages.get(i);
            MenuElement me = new MenuElement(
                Integer.toString(i), 
                String.format(CONVERTED_FILE_TAG, img.getFilename(), img.getEffect()));
            listing.add(me);
        }
        return listing;
    }

    public OutputContainer getConvertedImage(int index) {
        if(index>=0 && index<convertedImages.size()) {
            return convertedImages.get(index);
        }else{
            return convertedImages.getFirst();
        }
    }
    
    private ImageProcessor findImageProcessor(String name) {
        return imageProcessors.stream().filter(ip -> ip.getName().equals(name)).findFirst().orElse(null);
    }

    private PixelProcessor findPixelProcessor(String name) {
        return pixelProcessors.stream().filter(pp -> pp.getName().equals(name)).findFirst().orElse(null);
    }
    
    @Scheduled(fixedDelay = 5*60*1000)
    private void removeExpiredImages() {
        log.atInfo().log("Checking for expired images");
        for(OutputContainer img : new ArrayList<>(convertedImages)) {
            if(img.isExpired()){
                log.atInfo().log(String.format("Removing "+CONVERTED_FILE_TAG, img.getFilename(), img.getEffect()));
                convertedImages.remove(img);
            }
        }
    }
}
