package com.example.jamroga.ime.controllers;

import com.example.jamroga.ime.api.MiscUtils;
import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class OutputController {
    public static final String PAGE_RELOAD_SCRIPT = """
        setTimeout(function(){
            location.reload();
        }, 1000);
        """;
    public static final String CIRCLE_GREEN = "\uD83D\uDFE2";
    public static final String CIRCLE_RED = "\uD83D\uDD34";
    public static final String OUTPUT_FORMAT = "png";
    public static final String BASE64_IMAGE_HEADER = "data:image/%s;base64, %s";
    
    private final ProcessorService processorService;

    @Autowired
    public OutputController(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @GetMapping("/output")
    public String output(@RequestParam(name="id", defaultValue="0") String index,
                         Model model) {
        OutputContainer out = processorService.getConvertedImage(Integer.parseInt(index));

        model.addAttribute("fileName", out.getFilename());
        model.addAttribute("finished", out.isFinished() ? CIRCLE_GREEN : CIRCLE_RED);
        if(!out.isFinished()) model.addAttribute("script", PAGE_RELOAD_SCRIPT);
        model.addAttribute("imageURI", 
            String.format(BASE64_IMAGE_HEADER, OUTPUT_FORMAT, MiscUtils.imgToBase64String(out.getImage())));
        model.addAttribute("newImageName", "blurred-"+changeExtension(out.getFilename()));
        return "output";
    }

    private static String changeExtension(String f) {
        int i = f.lastIndexOf('.');
        String name = f.substring(0,i);
        return name + OUTPUT_FORMAT;
    }
}
