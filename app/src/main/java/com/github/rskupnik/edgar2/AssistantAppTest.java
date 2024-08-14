package com.github.rskupnik.edgar2;

import com.github.rskupnik.edgar.assistant.Assistant;
import com.github.rskupnik.edgar.assistant.ExplicitCredentials;
import com.github.rskupnik.edgar.assistant.task.TaskRegistration;
import com.github.rskupnik.edgar.assistant.discord.DiscordUserIO;
import com.github.rskupnik.edgar.assistant.tasks.*;
import com.github.rskupnik.edgar.assistant.webcrawler.SeleniumChromeWebCrawler;
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
        Assistant.defaultImplementation(
                new DiscordUserIO(
                        credentials.get("discordToken"),
                        credentials.get("discordAuthorizedUser")
                ),
                new ExplicitCredentials(credentials),
                SeleniumChromeWebCrawler::new,
                new TaskRegistration("pay gas", PayGasTask.class),
                new TaskRegistration("check power bill", CheckPowerBillDueTask.class),
                new TaskRegistration("pay power bill", PayPowerBillTask.class),
                new TaskRegistration("test", TestTask.class),
                new TaskRegistration("test rpi", RPiTest.class),
                new TaskRegistration("alior test", AliorTestTask.class),
                new TaskRegistration("pay taxes", PayTaxesTask.class)
        );
    }
}
