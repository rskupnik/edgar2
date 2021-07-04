package com.github.rskupnik.edgar2.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class MainController {

    @GetMapping("/dashboard/**")
    public String index() {
        return "index";
    }
}
