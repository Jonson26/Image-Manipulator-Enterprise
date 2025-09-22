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
        model.addAttribute("finished", statusString(out.isFinished()));
        if(!out.isFinished()) model.addAttribute("script", PAGE_RELOAD_SCRIPT);
        String base64img = "data:image/png;base64, "+ MiscUtils.imgToBase64String(out.getImage());
        model.addAttribute("imageURI", base64img);
        model.addAttribute("newImageName", "blurred-"+changeExtension(out.getFilename()));
        return "output";
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
