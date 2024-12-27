package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.StepTask;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.Map;
import java.util.function.Supplier;

public class RPiTest extends StepTask {
    public RPiTest(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                   Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
        setSteps(Steps.single(() -> {
            WebCrawler webCrawler = instantiateWebCrawler();
            webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
            webCrawler.enterTextToElementById("username1", taskProperties.get("tauronUsername"));
            System.out.println("Entered username: " + taskProperties.get("tauronUsername"));
            webCrawler.enterTextToElementById("password1", taskProperties.get("tauronPassword"));
            System.out.println("Entered password: " + taskProperties.get("tauronPassword"));
            webCrawler.clickElementByClass("button-pink");
            userIO.output("Trying to make a screenshot...");
            webCrawler.screenshot("/home/pi/test.png");
            userIO.output("Screenshot taken");
            webCrawler.destroy();
        }));
    }
}
