package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.assistant.Assistant;
import com.github.rskupnik.edgar.assistant.ExplicitTaskProperties;
import com.github.rskupnik.edgar.assistant.task.PythonTask;
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
@EnableConfigurationProperties({CredentialsConfig.class, TaskConfig.class})
public class AssistantAppTest {

    @Autowired
    private CredentialsConfig credentialsConfig;

    @Autowired
    private TaskConfig taskConfig;

    @Value("${assistant.enabled}")
    private Boolean enabled;

    @PostConstruct
    public void test() {
        System.out.println("Trying to load ASSISTANT");

        if (!enabled) {
            System.out.println("Assistant DISABLED");
            return;
        }

        var flattenedProps = flattenProperties("credentials", credentialsConfig.getCredentials(), new HashMap<>());
        flattenedProps = flattenProperties("tasks", taskConfig.getTasks(), flattenedProps);
//        flattenedProps.forEach((k, v) -> System.out.println(k + ": " + v));

        Assistant.defaultImplementation(
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
