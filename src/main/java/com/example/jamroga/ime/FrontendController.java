package com.example.jamroga.ime;

import com.example.jamroga.ime.api.BlurSimpleAverage;
import com.example.jamroga.ime.api.ImageManipulator;
import com.example.jamroga.ime.api.MapTo16Colours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

@Controller
public class FrontendController {
    public static final String MULTI = "multi";
    public static final String SINGLE = "single";
    
    @Autowired
    BlurSimpleAverage blur;
    
    @Autowired
    ImageManipulator magentaDeepFry;
    
    @Autowired
    MapTo16Colours palette16;

    private String tmpdir;
    
    private final ArrayList<BufferedImage> convertedImages = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(FrontendController.class);

    public FrontendController() {
        try {
            tmpdir = Files.createTempDirectory("IME_TMP_DIR-").toFile().getAbsolutePath();
            log.atInfo().log("Temporary directory created at "+tmpdir);

            URL url = FrontendController.class.getClassLoader().getResource("static/moka_mini.png");
            BufferedImage img = ImageIO.read(url);
            img = new BlurSimpleAverage().processImageInParallel(img);
            convertedImages.add(img);
        } catch (IOException e) {
            tmpdir = null;
        }
    }

    @GetMapping("/input")
    public String input(Model model) {
        return "input";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("effect") String effect,
                             @RequestParam("options") String options,
                             RedirectAttributes redirectAttributes)
        throws IOException {
        if(file.isEmpty()) {
            log.atWarn().log("No file uploaded");
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/output";
        }

        Path path = Paths.get(tmpdir, file.getOriginalFilename());
        Files.write(path, file.getBytes());
        String fileName = "";
        int index = -1;
        fileName += file.getOriginalFilename();
        redirectAttributes.addFlashAttribute("successMessage", "File upload successfully, uploaded file name: " + fileName);
        log.atInfo().log("File upload successfully, uploaded file name: " + file.getOriginalFilename());
        log.atInfo().log("Effect is: " + effect);
        log.atInfo().log("Option is: " + options);

        try{
            URL url = FrontendController.class.getClassLoader().getResource("static/moka.png");

            if(!fileName.isEmpty()){
                url = Path.of(tmpdir+"/"+fileName).toUri().toURL();
            }
            assert url != null;

            BufferedImage img = ImageIO.read(url);

            img = switch (effect) {
                case "blur" -> switch (options) {
                    case MULTI -> blur.processImageInParallel(img);
                    case SINGLE -> blur.processImage(img);
                    default -> img;
                };
                case "magenta" -> switch (options) {
                    case MULTI -> magentaDeepFry.processImageInParallel(img);
                    case SINGLE -> magentaDeepFry.processImage(img);
                    default -> img;
                };
                case "palette16" -> switch (options){
                    case MULTI -> palette16.processImageInParallel(img);
                    case SINGLE -> palette16.processImage(img);
                    default -> img;
                };
                default -> img;
            };
            
            convertedImages.add(img);
            index = convertedImages.indexOf(img);
        } catch (IOException e) {
            log.atWarn().log(e.toString());
        }
        
        return "redirect:/output?fileName=" + fileName + "&cid=" + index;
    }
    
    @GetMapping("/output")
    public String output(@RequestParam(name="fileName", defaultValue="moka_mini.png") String fileName,
                         @RequestParam(name="cid", defaultValue="0") String index,
                         Model model) {
        
        BufferedImage img = convertedImages.get(Integer.parseInt(index));

        String base64img = "data:image/png;base64, "+imgToBase64String(img);
        model.addAttribute("imageURI", base64img);
        model.addAttribute("newImageName", "blurred-"+changeExtension(fileName));
        return "output";
    }

    private static String imgToBase64String(final RenderedImage img) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private static String changeExtension(String f) {
        int i = f.lastIndexOf('.');
        String name = f.substring(0,i);
        return name + "png";
    }
}
