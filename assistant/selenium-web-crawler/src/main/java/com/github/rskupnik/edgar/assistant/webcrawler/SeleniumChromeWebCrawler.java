package com.github.rskupnik.edgar.assistant.webcrawler;

import com.github.rskupnik.edgar.assistant.WebCrawler;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

// TODO: Add a helper class that encapsulates the id/className of Element and the type (ID / CLASS_NAME)
// then remove duplicated methods and simplify the API

// TODO: Need to test on RPI, see this: https://qabrio.pl/selenium-w-embedded-instalacja-na-raspberry-pi/
public class SeleniumChromeWebCrawler implements WebCrawler {

    private final WebDriver driver;

    public SeleniumChromeWebCrawler() {
        var options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        this.driver = new ChromeDriver(options);
        // TODO: Configurable timeout
        this.driver.manage().timeouts().implicitlyWait(Duration.of(10, ChronoUnit.SECONDS));
    }

    @Override
    public void goToWebsite(String url) {
        driver.get(url);
    }

    @Override
    public void enterTextToElementById(String id, String text) {
        var element = driver.findElement(By.id(id));
        element.sendKeys(Keys.LEFT_CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
        // TODO: COMMAND on Mac, but CONTROL everywhere else?
    }

    @Override
    public void enterTextToElementByClass(String className, String text) {
        var element = driver.findElement(By.className(className));
        element.sendKeys(Keys.LEFT_CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
    }

    @Override
    public void clickElementById(String id) {
        driver.findElement(By.id(id)).click();
    }

    @Override
    public void clickElementByClass(String className) {
        driver.findElement(By.className(className)).click();
    }

    @Override
    public void clickElementByClassNested(String... classNames) {

        WebElement previousElement = null;
        for (String className : classNames) {
            var element = previousElement == null ? driver.findElement(By.className(className)) : previousElement.findElement(By.className(className));
            previousElement = element;
        }

        previousElement.click();
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
    public void screenshot(String destination) {
//        System.out.println("Attempting screenshot");
        File screenShotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//        System.out.println("Took screenshot");
        try {
            Files.copy(screenShotFile.toPath(), Path.of(destination), StandardCopyOption.REPLACE_EXISTING);
//            System.out.println("Screenshot copied to: " + destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        driver.close();
        driver.quit();
    }
}
