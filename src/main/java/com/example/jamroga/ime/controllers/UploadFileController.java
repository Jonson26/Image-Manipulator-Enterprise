package com.example.jamroga.ime.controllers;

import com.example.jamroga.ime.api.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Slf4j
public class UploadFileController {
    private final ProcessorService processorService;

    private String tmpdir;
    
    @Autowired
    public UploadFileController(ProcessorService processorService) {
        this.processorService = processorService;
        try {
            tmpdir = Files.createTempDirectory("IME_TMP_DIR-").toFile().getAbsolutePath();
            log.atInfo().log("Temporary directory created at "+tmpdir);
        } catch (IOException e) {
            tmpdir = null;
        }
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

        int index = processorService.processImage(tmpdir, fileName, effect, options);
        
        return "redirect:/output?id=" + index;
    }
}
