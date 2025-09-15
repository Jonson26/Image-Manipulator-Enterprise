package com.example.jamroga.ime.api;

import com.example.jamroga.ime.FrontendController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

@Component("manipulatorService")
public class ManipulatorService {
    public static final String MODE_EXECUTOR = "executor";
    public static final String MODE_SINGLE = "single";
    public static final String MODE_THREADS = "threads";
    public static final String EFFECT_BLUR = "blur";
    public static final String EFFECT_MAGENTA = "magenta";
    public static final String EFFECT_PALETTE_16 = "palette16";
    public static final String EFFECT_SCALE_DOWN_2X = "scaleDown2x";
    public static final String EFFECT_SCALE_UP_2X = "scaleUp2x";


    @Autowired
    BlurSimpleAverage blur;

    @Autowired
    ImageManipulator magentaDeepFry;

    @Autowired
    MapTo16Colours palette16;
    
    private final ArrayList<OutputContainer> convertedImages = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(ManipulatorService.class);

    public ManipulatorService() {
        try {
            URL url = FrontendController.class.getClassLoader().getResource("static/moka_mini.png");
            BufferedImage img = ImageIO.read(url);
            OutputContainer out = new BlurSimpleAverage().processImageWithExecutorService(img,"moka_mini.png");
            convertedImages.add(out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public int processImage(String dir, String filename, String effect, String options) throws IOException {
        int index = 0;
        URL url = Path.of(dir+"/"+filename).toUri().toURL();
        
        log.atInfo().log("Converting file "+url.getFile());
        log.atInfo().log("Effect is: " + effect);
        log.atInfo().log("Option is: " + options);
        
        try{
            BufferedImage img = ImageIO.read(url);

            OutputContainer out = switch (effect) {
                case EFFECT_BLUR -> switch (options) {
                    case MODE_EXECUTOR -> blur.processImageWithExecutorService(img, filename);
                    case MODE_THREADS -> blur.processImageWithThreads(img, filename);
                    case MODE_SINGLE -> blur.processImage(img, filename);
                    default -> new OutputContainer(img, filename, true);
                };
                case EFFECT_MAGENTA -> switch (options) {
                    case MODE_EXECUTOR -> magentaDeepFry.processImageWithExecutorService(img, filename);
                    case MODE_THREADS -> magentaDeepFry.processImageWithThreads(img, filename);
                    case MODE_SINGLE -> magentaDeepFry.processImage(img, filename);
                    default -> new OutputContainer(img, filename, true);
                };
                case EFFECT_PALETTE_16 -> switch (options){
                    case MODE_EXECUTOR -> palette16.processImageWithExecutorService(img, filename);
                    case MODE_THREADS -> palette16.processImageWithThreads(img, filename);
                    case MODE_SINGLE -> palette16.processImage(img, filename);
                    default -> new OutputContainer(img, filename, true);
                };
                case EFFECT_SCALE_DOWN_2X -> new OutputContainer(scale(img, 0.5), filename);
                case EFFECT_SCALE_UP_2X -> new OutputContainer(scale(img, 2), filename);
                default -> new OutputContainer(img, filename, true);
            };

            convertedImages.add(out);
            index = convertedImages.indexOf(out);
        } catch (IOException e) {
            log.atWarn().log(e.toString());
        }
        return index;
    }
    
    public OutputContainer getConvertedImage(int index) {
        if(index>=0 && index<convertedImages.size()) {
            return convertedImages.get(index);
        }else{
            return convertedImages.getFirst();
        }
    }

    private static BufferedImage scale(final BufferedImage before, final double scale) {
        int w = before.getWidth();
        int h = before.getHeight();
        int w2 = (int) (w * scale);
        int h2 = (int) (h * scale);
        BufferedImage after = new BufferedImage(w2, h2, before.getType());
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        scaleOp.filter(before, after);
        return after;
    }
}
