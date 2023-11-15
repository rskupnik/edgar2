package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.function.Supplier;

public class RPiTest extends Task {
    public RPiTest(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(Steps.single(() -> {
            WebCrawler webCrawler = instantiateWebCrawler();
            webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
            webCrawler.enterTextToElementById("username1", credentials.get("tauronUsername"));
            System.out.println("Entered username: " + credentials.get("tauronUsername"));
            webCrawler.enterTextToElementById("password1", credentials.get("tauronPassword"));
            System.out.println("Entered password: " + credentials.get("tauronPassword"));
            webCrawler.clickElementByClass("button-pink");
            userIO.output("Trying to make a screenshot...");
            webCrawler.screenshot("/home/pi/test.png");
            userIO.output("Screenshot taken");
            webCrawler.destroy();
        }));
    }
}
