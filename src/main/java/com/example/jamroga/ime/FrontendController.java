package com.example.jamroga.ime;

import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@Slf4j
public class FrontendController {
    public static final String PAGE_RELOAD_SCRIPT = """
        setTimeout(function(){
            location.reload();
        }, 1000);
        """;
    private final ProcessorService processorService;

    private String tmpdir;
    
    private final List<String> uploadedFiles = new ArrayList<>();
    
    @Autowired
    public FrontendController(ProcessorService processorService) {
        this.processorService = processorService;
        try {
            tmpdir = Files.createTempDirectory("IME_TMP_DIR-").toFile().getAbsolutePath();
            log.atInfo().log("Temporary directory created at "+tmpdir);
        } catch (IOException e) {
            tmpdir = null;
        }
    }

    @GetMapping("/input")
    public String input(Model model) {
        if(!uploadedFiles.isEmpty()) model.addAttribute("fileListing", uploadedFiles);
        model.addAttribute("effects", processorService.getPixelProcessorMenuElements());
        model.addAttribute("options", processorService.getImageProcessorMenuElements());
        return "input";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("effect") String effect,
                             @RequestParam("options") String options)
        throws IOException {
        if(file.isEmpty()) {
            log.atWarn().log("No file uploaded");
            return "redirect:/output";
        }

        Path path = Paths.get(tmpdir, file.getOriginalFilename());
        Files.write(path, file.getBytes());
        String fileName = file.getOriginalFilename();
        log.atInfo().log("File upload successfully, uploaded file name: " + fileName);
        uploadedFiles.add(fileName);

        int index = processorService.processImage(tmpdir, fileName, effect, options);
        
        return "redirect:/output?id=" + index;
    }
    
    @GetMapping("/output")
    public String output(@RequestParam(name="id", defaultValue="0") String index,
                         Model model) {
        
        OutputContainer out = processorService.getConvertedImage(Integer.parseInt(index));

        model.addAttribute("fileName", out.getFilename());
        model.addAttribute("finished", statusString(out.isFinished()));
        if(!out.isFinished()) model.addAttribute("script", PAGE_RELOAD_SCRIPT);
        String base64img = "data:image/png;base64, "+imgToBase64String(out.getImage());
        model.addAttribute("imageURI", base64img);
        model.addAttribute("newImageName", "blurred-"+changeExtension(out.getFilename()));
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
    
    private static String statusString(boolean status){
        if(status){
            return "\uD83D\uDFE2";
        }else{
            return "\uD83D\uDD34";
        }
    }
}
