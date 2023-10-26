package com.github.rskupnik.edgar.assistant;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

// TODO: Add a helper class that encapsulates the id/className of Element and the type (ID / CLASS_NAME)
// then remove duplicated methods and simplify the API
public class SeleniumChromeWebCrawler implements WebCrawler {

    private final WebDriver driver;

    SeleniumChromeWebCrawler() {
        var options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

        // TODO: Probably not needed
//        System.setProperty("webdriver.chrome.logfile", "/Users/rskupnik/chromedriver.log");
//        System.setProperty("webdriver.chrome.verboseLogging", "true");

        // TODO: Do we need to setup this every time?
        WebDriverManager.chromedriver().setup();

        this.driver = new ChromeDriver(options);
    }

    @Override
    public void goToWebsite(String url) {
        driver.get(url);
    }

    @Override
    public void enterTextToElementById(String id, String text) {
        driver.findElement(By.id(id)).sendKeys(text);
    }

    @Override
    public void enterTextToElementByClass(String className, String text) {
        driver.findElement(By.className(className)).sendKeys(text);
    }

    @Override
    public void clickElementByIdAndWait(String id, long timeMillis) {
        var element = driver.findElement(By.id(id));
        element.click();
        try {
            element.wait(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clickElementByClassAndWait(String className, long timeMillis) {
        var element = driver.findElement(By.className(className));
        element.click();
        synchronized (element) {
            try {
                element.wait(timeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getText(String... classes) {
        // TODO: Make this method better and more clear
        WebElement previousElement = null;
        for (String className : classes) {
            var element = previousElement == null ? driver.findElement(By.className(className)) : previousElement.findElement(By.className(className));
            previousElement = element;
        }
        return previousElement.getText();
    }

    @Override
    public void destroy() {
        driver.close();
    }
}
