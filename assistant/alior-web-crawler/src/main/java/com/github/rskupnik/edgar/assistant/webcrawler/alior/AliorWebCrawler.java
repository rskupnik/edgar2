package com.github.rskupnik.edgar.assistant.webcrawler.alior;

import com.github.rskupnik.edgar.assistant.WebCrawler;

import java.util.function.Consumer;

public class AliorWebCrawler {

    private final WebCrawler webCrawler;
    private final Consumer<Integer> waitMethod;

    public AliorWebCrawler(WebCrawler webCrawler, Consumer<Integer> waitMethod) {
        this.webCrawler = webCrawler;
        this.waitMethod = waitMethod;
    }

    public void login(String login, String password) {
        webCrawler.goToWebsite("https://system.aliorbank.pl/sign-in");
        webCrawler.enterTextToElementById("login", login);
        webCrawler.clickElementByClass("login-submit");

        for (int i = 0; i <= 15; i++) {
            webCrawler.rememberElementByName("masked[" + (i + 1) + "]", "passwordInput");
            var isDisabled = webCrawler.checkRememberedElementContainsPropertyEqualTo("passwordInput", "disabled", "true");
            if (!isDisabled) {
                webCrawler.enterTextToElementByName("masked[" + (i + 1) + "]", Character.toString(password.charAt(i)));
            }
        }

        webCrawler.clickElementById("password-submit");
    }

    public void requestOneTimeAccess() {
        webCrawler.clickElementByXpath("//button[contains(@class, 'inline-cta')]");
    }

    public void switchToBusinessContext() {
        webCrawler.clickElementById("context");
        waitMethod.accept(2000);
        webCrawler.clickElementByXpath("//li[@role='option'][@aria-selected='false']//div[contains(@class, 'color-text-default')]");
        waitMethod.accept(3000);
    }

    public void enterTemplatesTab() {
        webCrawler.clickElementByXpath("//app-main-header//li[@data-onboarding-element='TEMPLATES']//a//span");
        waitMethod.accept(3000);
    }

    public void selectTemplate(String template) {
        webCrawler.enterTextToElementByXpath("//input[@id='template-search']", template);
        webCrawler.clickElementByXpath("//li//recipient-item//button-cta[@text='Send a transfer']");
    }

    public void enterAmount(String amount) {
        webCrawler.enterTextToElementByXpath("//input[@id='amount.value']", amount);
    }

    public void enterTitle(String title) {
        webCrawler.enterTextToElementByXpath("//input[@id='title']", title);
    }

    public void submitPayment() {
        webCrawler.clickElementByXpath("//button[@type='submit']");
    }

    public void fail() {
        webCrawler.clickElementByXpath("//div//li//random");
    }
}
