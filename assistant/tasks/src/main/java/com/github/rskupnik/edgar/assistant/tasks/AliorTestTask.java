package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.StepTask;
import com.github.rskupnik.edgar.assistant.task.Task;
import com.github.rskupnik.edgar.assistant.webcrawler.alior.AliorWebCrawler;

import java.util.Map;
import java.util.function.Supplier;

public class AliorTestTask extends StepTask {

    private WebCrawler webCrawler = null;
    private AliorWebCrawler aliorWebCrawler = null;

    private String title, amount;

    public AliorTestTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                         Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
        setSteps(
            Steps.beginWith(() -> {
                userIO.output("Testing Alior website scraping...");
                userIO.output("Please be ready to allow one-time access on Alior Mobile app soon!");
                userIO.output("Starting in 3s");
                wait(3000);
//                webCrawler.goToWebsite("https://system.aliorbank.pl/sign-in");
//                webCrawler.enterTextToElementById("login", credentials.get("aliorUserId"));
//                webCrawler.clickElementByClass("login-submit");
//
//                var pwd = credentials.get("aliorPassword");
//
//                for (int i = 0; i <= 15; i++) {
//                    webCrawler.rememberElementByName("masked[" + (i + 1) + "]", "passwordInput");
//                    var isDisabled = webCrawler.checkRememberedElementContainsPropertyEqualTo("passwordInput", "disabled", "true");
//                    if (!isDisabled) {
//                        webCrawler.enterTextToElementByName("masked[" + (i + 1) + "]", Character.toString(pwd.charAt(i)));
//                    }
//                }
//
//                webCrawler.clickElementById("password-submit");
//                webCrawler.clickElementByXpath("//button[contains(@class, 'inline-cta')]");
            }).thenRequestInput("Please provide title", m -> {
                title = (String) m;
            }).thenRequestInput("Please provide amount", m -> {
                amount = (String) m;
            }).then(() -> {
                userIO.output("Logging in...");
                webCrawler = instantiateWebCrawler();
                aliorWebCrawler = new AliorWebCrawler(webCrawler, this::wait);
                aliorWebCrawler.login(taskProperties.get("aliorUserId"), taskProperties.get("aliorPassword"));
                aliorWebCrawler.requestOneTimeAccess();
            }).thenRequestInput("Please allow one-time access and type 'continue'", m -> {
                var message = (String) m;
                // TODO: Support aborting?
                // TODO: Auto-detect and continue without the need to type continue
            }).then(() -> {
                userIO.output("Login successful!");
                userIO.output("Attempting to switch profile...");

                aliorWebCrawler.switchToBusinessContext();
                aliorWebCrawler.enterTemplatesTab();
                aliorWebCrawler.selectTemplate("Zakład Ubezpieczeń Społecznych");
                aliorWebCrawler.enterTitle(title);
                aliorWebCrawler.enterAmount(amount);
                aliorWebCrawler.submitPayment();
                aliorWebCrawler.fail();

//                webCrawler.clickElementById("context");
//                wait(2000);
//
//                webCrawler.clickElementByXpath("//li[@role='option'][@aria-selected='false']//div[contains(@class, 'color-text-default')]");
//                wait(3000);
//
//                webCrawler.clickElementByXpath("//app-main-header//li[@data-onboarding-element='TEMPLATES']//a//span");
//                wait(3000);
//
//                webCrawler.enterTextToElementByXpath("//input[@id='template-search']", "Zakład Ubezpieczeń Społecznych");
//                webCrawler.clickElementByXpath("//li//recipient-item//button-cta[@text='Send a transfer']");
//
//                wait(3000);
//                webCrawler.screenshot("/Users/rskupnik/Pictures/alior.png");

//                webCrawler.destroy();
            }).finishWith(() -> {
                userIO.output("Finished");
                aliorWebCrawler.finish();
            }).build()
        );
    }
}
