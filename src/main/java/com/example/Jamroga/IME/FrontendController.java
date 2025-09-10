package com.example.Jamroga.IME;

import com.example.Jamroga.IME.API.BlurSimpleAverage;
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
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
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

    public FrontendController() {
        try {
            tmpdir = Files.createTempDirectory("tmpDirPrefix").toFile().getAbsolutePath();
            System.out.println(tmpdir);
        } catch (IOException e) {
            tmpdir = null;
        }
    }

    @GetMapping("/input")
    public String input(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        return "input";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
        throws IOException {
        if(file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/output";
        }

        System.out.println(tmpdir);
        Path path = Paths.get(tmpdir, file.getOriginalFilename());
        Files.write(path, file.getBytes());
        redirectAttributes.addFlashAttribute("successMessage", "File upload successfully, uploaded file name: " + file.getOriginalFilename());
        return "redirect:/output?fileName=" + file.getOriginalFilename() + "&fileFormat=" + getFileExtension(file.getOriginalFilename());
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
        } catch (Exception e) {
            e.printStackTrace();
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
