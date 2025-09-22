package com.example.jamroga.ime.api;

import com.example.jamroga.ime.api.implementations.BlurSimpleAverage;
import com.example.jamroga.ime.api.implementations.ClassicProcessor;
import com.example.jamroga.ime.api.interfaces.ImageProcessor;
import com.example.jamroga.ime.api.interfaces.PixelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProcessorService {
    private final List<ImageProcessor> imageProcessors;
    
    private final List<PixelProcessor> pixelProcessors;
    
    private final ArrayList<OutputContainer> convertedImages = new ArrayList<>();
    
    @Autowired
    public ProcessorService(List<ImageProcessor> imageProcessors, List<PixelProcessor> pixelProcessors) {
        this.imageProcessors = imageProcessors;
        this.pixelProcessors = pixelProcessors;
        
        try {
            URL url = ProcessorService.class.getClassLoader().getResource("static/moka_mini.png");
            assert url != null;
            BufferedImage img = ImageIO.read(url);
            OutputContainer out = new ClassicProcessor()
                .processImage(img, new BlurSimpleAverage(), "moka_mini.png");
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

        log.atInfo().log("Converting file "+url.getFile());
        log.atInfo().log("Effect is: " + effect);
        log.atInfo().log("Option is: " + options);

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
            MenuElement me = new MenuElement(""+i, String.format("%s [%s]", img.getFilename(), img.getEffect()));
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
}
