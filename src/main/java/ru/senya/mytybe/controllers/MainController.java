package ru.senya.mytybe.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/watch")
    public String watchVideo(@RequestParam String video, Model model) {
        model.addAttribute("video", video);
        return "video";
    }

}
