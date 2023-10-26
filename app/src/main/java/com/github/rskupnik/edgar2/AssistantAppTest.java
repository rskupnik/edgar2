package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.assistant.Assistant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@PropertySource("classpath:credentials.properties")
// TODO: Move this to assistant:app? Let the main app simply discover this Component
public class AssistantAppTest {

    @Value("#{${assistant.credentials}}")
    private Map<String,String> credentials;

    @PostConstruct
    public void test() {
        System.out.println("TESTING");
        Assistant.defaultImplementation(credentials);
        //Assistant.defaultImplementation().processCommand("pay gas");
    }
}
