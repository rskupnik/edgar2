package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.assistant.Assistant;
import com.github.rskupnik.edgar.assistant.ExplicitCredentials;
import com.github.rskupnik.edgar.assistant.task.TaskRegistration;
import com.github.rskupnik.edgar.assistant.discord.DiscordUserIO;
import com.github.rskupnik.edgar.assistant.tasks.*;
import com.github.rskupnik.edgar.assistant.webcrawler.SeleniumChromeWebCrawler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
// TODO: Move this to assistant:app? Let the main app simply discover this Component
@EnableConfigurationProperties(CredentialsConfig.class)
public class AssistantAppTest {

    @Autowired
    private CredentialsConfig credentialsConfig;

    @Value("${assistant.enabled}")
    private Boolean enabled;

    @PostConstruct
    public void test() {
        var flattenedCreds = flattenCredentials(credentialsConfig);
        flattenedCreds.forEach((k, v) -> System.out.println(k + ": " + v));

        if (!enabled) {
            System.out.println("Assistant DISABLED");
            return;
        }

        Assistant.defaultImplementation(
                new DiscordUserIO(
                        flattenedCreds.get("discord.token"),
                        flattenedCreds.get("discord.authorizedUser")
                ),
                new ExplicitCredentials(flattenedCreds),
                SeleniumChromeWebCrawler::new,
                new TaskRegistration("pay gas", PayGasTask.class),
                new TaskRegistration("check power bill", CheckPowerBillDueTask.class),
                new TaskRegistration("pay power bill", PayPowerBillTask.class),
                new TaskRegistration("test", TestTask.class),
                new TaskRegistration("test rpi", RPiTest.class),
                new TaskRegistration("alior test", AliorTestTask.class),
                new TaskRegistration("pay taxes", PayTaxesTask.class),
                new TaskRegistration("playwright test", PlaywrightTestTask.class)
        );
    }

    private Map<String, String> flattenCredentials(CredentialsConfig credentialsConfig) {
        Map<String, String> flatMap = new HashMap<>();
        flatten("", credentialsConfig.getCredentials(), flatMap);
        return flatMap;
    }

    // Helper recursive function to flatten the map
    private void flatten(String parentKey, Map<String, Object> source, Map<String, String> target) {
        source.forEach((key, value) -> {
            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;
            if (value instanceof Map) {
                // Recursive call for nested maps
                flatten(fullKey, (Map<String, Object>) value, target);
            } else {
                // Add leaf values to the flat map
                target.put(fullKey, value.toString());
            }
        });
    }
}
