package com.example.jamroga.ime.controllers;

import com.example.jamroga.ime.api.ProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class InputController {
    private final ProcessorService processorService;

    @Autowired
    public InputController(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @GetMapping("/input")
    public String input(Model model) {
        List<String> fileListing = processorService.getProcessedImageListing()
            .stream()
            .parallel()
            .map(me -> String.format("%s <a href=\"/output?id=%s\">view</a>", me.description(), me.name()))
            .toList();
        if(!fileListing.isEmpty()) model.addAttribute("fileListing", fileListing);
        model.addAttribute("effects", processorService.getPixelProcessorMenuElements());
        model.addAttribute("options", processorService.getImageProcessorMenuElements());
        return "input";
    }
}
