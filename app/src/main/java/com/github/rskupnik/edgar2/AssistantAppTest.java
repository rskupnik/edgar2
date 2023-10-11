package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.assistant.Assistant;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AssistantAppTest {

    @PostConstruct
    public void test() {
        System.out.println("TESTING");
        Assistant.defaultImplementation();
        //Assistant.defaultImplementation().processCommand("pay gas");
    }
}
