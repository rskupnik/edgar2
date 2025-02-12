package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.assistant.Assistant;
import com.github.rskupnik.edgar.assistant.ExplicitTaskProperties;
import com.github.rskupnik.edgar.assistant.discord.DiscordUserIO;
import com.github.rskupnik.edgar.assistant.task.PythonTask;
import com.github.rskupnik.edgar.assistant.task.TaskRegistration;
import com.github.rskupnik.edgar.assistant.tasks.*;
import com.github.rskupnik.edgar.assistant.webcrawler.SeleniumChromeWebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({CredentialsConfig.class, TaskConfig.class})
public class AssistantConfig {

    @Autowired
    private CredentialsConfig credentialsConfig;

    @Autowired
    private TaskConfig taskConfig;

    @Bean
    public Assistant assistant() {
        // TODO: Somehow only initialize if assistant is enabled

        var flattenedProps = flattenProperties("credentials", credentialsConfig.getCredentials(), new HashMap<>());
        flattenedProps = flattenProperties("tasks", taskConfig.getTasks(), flattenedProps);

        System.out.println("INITIALIZING ASSISTANT");

        return Assistant.defaultImplementation(
            new DiscordUserIO(
                    flattenedProps.get("credentials.discord.token"),
                    flattenedProps.get("credentials.discord.authorizedUser")
            ),
            new ExplicitTaskProperties(flattenedProps),
            SeleniumChromeWebCrawler::new,
            new TaskRegistration("pay gas", PayGasTask.class),
            //new TaskRegistration("check power bill", CheckPowerBillDueTask.class),
            new TaskRegistration("pay power bill", PayPowerBillTask.class),
            new TaskRegistration("test", TestTask.class),
            new TaskRegistration("test rpi", RPiTest.class),
            new TaskRegistration("alior test", AliorTestTask.class),
            new TaskRegistration("pay taxes", PayTaxesTask.class),
            new TaskRegistration("check power bill", PythonTask.class, Map.of(
                    PythonTask.SCRIPT_LOCATION_PROP, "tasks.check-tauron-power-bill.script-location",
                    PythonTask.PIPE_PATH_PROP, "tasks.check-tauron-power-bill.pipe",
                    PythonTask.ADDITIONAL_ARGUMENTS, new String[] {flattenedProps.get("credentials.tauron.username"), flattenedProps.get("credentials.tauron.password")}
            )),
            new TaskRegistration("pay power bill", PythonTask.class, Map.of(
                    PythonTask.SCRIPT_LOCATION_PROP, "tasks.pay-tauron-power-bill.script-location",
                    PythonTask.PIPE_PATH_PROP, "tasks.pay-tauron-power-bill.pipe",
                    PythonTask.ADDITIONAL_ARGUMENTS, new String[] {flattenedProps.get("credentials.tauron.username"), flattenedProps.get("credentials.tauron.password")}
            )),
            new TaskRegistration("test driver license", PythonTask.class, Map.of(
                    PythonTask.SCRIPT_LOCATION_PROP, "tasks.test-driver-license.script-location",
                    PythonTask.PIPE_PATH_PROP, "tasks.test-driver-license.pipe"
            ))
        );
    }

    private Map<String, String> flattenProperties(String parentKey, Map<String, Object> source, Map<String, String> target) {
        flatten(parentKey, source, target);
        return target;
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
