package com.example.jamroga.ime;

import com.example.jamroga.ime.api.BlurSimpleAverage;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Controller
public class FrontendController {
    @Autowired
    BlurSimpleAverage blur;

    String tmpdir;

    private static final Logger log = LoggerFactory.getLogger(FrontendController.class);

    public FrontendController() {
        try {
            tmpdir = Files.createTempDirectory("IME_TMP_DIR-").toFile().getAbsolutePath();
            log.atInfo().log("Temporary directory created at "+tmpdir);
        } catch (IOException e) {
            tmpdir = null;
        }
    }

    @GetMapping("/input")
    public String input(Model model) {
        return "input";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
        throws IOException {
        if(file.isEmpty()) {
            log.atWarn().log("No file uploaded");
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/output";
        }

        Path path = Paths.get(tmpdir, file.getOriginalFilename());
        Files.write(path, file.getBytes());
        String fileName = "";
        fileName += file.getOriginalFilename();
        redirectAttributes.addFlashAttribute("successMessage", "File upload successfully, uploaded file name: " + fileName);
        log.atInfo().log("File upload successfully, uploaded file name: " + file.getOriginalFilename());
        return "redirect:/output?fileName=" + fileName + "&fileFormat=" + getFileExtension(fileName);
    }
    
    @GetMapping("/output")
    public String output(@RequestParam(name="fileName", required=true, defaultValue="") String fileName, 
                         @RequestParam(name="fileFormat", required=true, defaultValue="png") String format, Model model) {
        try{
            URL url = FrontendController.class.getClassLoader().getResource("static/moka.png");
            
            if(!fileName.isEmpty()){
                url = Path.of(tmpdir+"/"+fileName).toUri().toURL();
            }
            assert url != null;
            
            BufferedImage img = ImageIO.read(url);
            img = blur.processImage(img);
            String base64img = "data:image/png;base64, "+imgToBase64String(img, format);
            model.addAttribute("imageURI", base64img);
            model.addAttribute("newImageName", "blurred-"+Paths.get(url.toURI()).getFileName());
        } catch (IOException | URISyntaxException e) {
            log.atWarn().log(e.toString());
        }
        return "output";
    }

    private static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf + 1);
    }
}
