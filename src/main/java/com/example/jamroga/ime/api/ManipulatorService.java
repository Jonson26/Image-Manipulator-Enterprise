package com.example.jamroga.ime.api;

import com.example.jamroga.ime.FrontendController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

@Component("manipulatorService")
public class ManipulatorService {
    public static final String MODE_EXECUTOR = "executor";
    public static final String MODE_SINGLE = "single";
    public static final String MODE_THREADS = "threads";
    public static final String EFFECT_BLUR = "blur";
    public static final String EFFECT_MAGENTA = "magenta";
    public static final String EFFECT_PALETTE_16 = "palette16";

    @Autowired
    BlurSimpleAverage blur;

    @Autowired
    ImageManipulator magentaDeepFry;

    @Autowired
    MapTo16Colours palette16;
    
    private final ArrayList<BufferedImage> convertedImages = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(ManipulatorService.class);

    public ManipulatorService() {
        try {
            URL url = FrontendController.class.getClassLoader().getResource("static/moka_mini.png");
            BufferedImage img = ImageIO.read(url);
            img = new BlurSimpleAverage().processImageWithExecutorService(img);
            convertedImages.add(img);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public int processImage(URL url, String effect, String options) {
        int index = 0;

        log.atInfo().log("Converting file "+url.getFile());
        log.atInfo().log("Effect is: " + effect);
        log.atInfo().log("Option is: " + options);
        
        try{
            BufferedImage img = ImageIO.read(url);

            img = switch (effect) {
                case EFFECT_BLUR -> switch (options) {
                    case MODE_EXECUTOR -> blur.processImageWithExecutorService(img);
                    case MODE_THREADS -> blur.processImageWithThreads(img);
                    case MODE_SINGLE -> blur.processImage(img);
                    default -> img;
                };
                case EFFECT_MAGENTA -> switch (options) {
                    case MODE_EXECUTOR -> magentaDeepFry.processImageWithExecutorService(img);
                    case MODE_THREADS -> magentaDeepFry.processImageWithThreads(img);
                    case MODE_SINGLE -> magentaDeepFry.processImage(img);
                    default -> img;
                };
                case EFFECT_PALETTE_16 -> switch (options){
                    case MODE_EXECUTOR -> palette16.processImageWithExecutorService(img);
                    case MODE_THREADS -> palette16.processImageWithThreads(img);
                    case MODE_SINGLE -> palette16.processImage(img);
                    default -> img;
                };
                default -> img;
            };

            convertedImages.add(img);
            index = convertedImages.indexOf(img);
        } catch (IOException e) {
            log.atWarn().log(e.toString());
        }
        return index;
    }
    
    public BufferedImage getConvertedImage(int index) {
        return convertedImages.get(index);
    }
}
