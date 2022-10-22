package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class TestController {

    private final Edgar edgar;

    @Autowired
    public TestController(Edgar edgar) {
        this.edgar = edgar;
    }

    @PostMapping("tts")
    public void tts(@RequestParam String text) {
        edgar.ttsSpeak(text);
    }
}
