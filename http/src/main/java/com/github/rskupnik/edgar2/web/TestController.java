package com.github.rskupnik.edgar2.web;

import com.github.rskupnik.edgar.Edgar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final Edgar edgar;

    @Autowired
    public TestController(Edgar edgar) {
        this.edgar = edgar;
    }

    @GetMapping("/test")
    public String test() {
        return edgar.test();
    }
}
