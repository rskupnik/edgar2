package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.function.Supplier;

public class AliorTestTask extends Task  {

    private WebCrawler webCrawler = null;

    public AliorTestTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(
            Steps.beginWith(() -> {
                userIO.output("Testing Alior website scraping...");
                userIO.output("Please be ready to allow one-time access on Alior Mobile app soon!");
                userIO.output("Starting in 3s");
                wait(3000);

                userIO.output("Logging in...");
                webCrawler = instantiateWebCrawler();
                webCrawler.goToWebsite("https://system.aliorbank.pl/sign-in");
                webCrawler.enterTextToElementById("login", credentials.get("aliorUserId"));
                webCrawler.clickElementByClass("login-submit");

                var pwd = credentials.get("aliorPassword");

                for (int i = 0; i <= 15; i++) {
                    webCrawler.rememberElementByName("masked[" + (i + 1) + "]", "passwordInput");
                    var isDisabled = webCrawler.checkRememberedElementContainsPropertyEqualTo("passwordInput", "disabled", "true");
                    if (!isDisabled) {
                        webCrawler.enterTextToElementByName("masked[" + (i + 1) + "]", Character.toString(pwd.charAt(i)));
                    }
                }

                webCrawler.clickElementById("password-submit");
                webCrawler.clickElementByXpath("//button[@ctatheme='primary']");
            }).thenRequestInput("Please allow one-time access and type 'continue'", m -> {
                var message = (String) m;
                // TODO: Support aborting?
            }).then(() -> {
                userIO.output("Login successful!");
                userIO.output("Attempting to switch profile...");

                webCrawler.clickElementById("context");
                wait(500);
                // TODO: This is random, cannot use it
                //       Try xpath li by role?
                webCrawler.clickElementById("o1140");
                wait(15000);

                webCrawler.screenshot("/Users/rskupnik/Pictures/alior.png");

                webCrawler.destroy();
                userIO.output("Done!");
            }).build()
        );
    }
}
