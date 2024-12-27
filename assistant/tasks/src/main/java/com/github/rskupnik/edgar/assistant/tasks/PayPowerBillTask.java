package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.StepTask;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.Map;
import java.util.function.Supplier;

// TODO: Failproof, cover case where no amount is due, if not after deadline, etc.
public class PayPowerBillTask extends StepTask {

    private WebCrawler webCrawler = null;

    private String blikCode = null;

    public PayPowerBillTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                            Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
        setSteps(
            Steps.beginWith(() -> {
                userIO.output("Checking the power bill amount due...");
                webCrawler = instantiateWebCrawler();
                webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
                webCrawler.enterTextToElementById("username1", taskProperties.get("tauronUsername"));
                webCrawler.enterTextToElementById("password1", taskProperties.get("tauronPassword"));
                webCrawler.clickElementByClass("button-pink");
                webCrawler.clickElementByClass("popup-close");
                userIO.output("Amount to pay: " + webCrawler.getText("amount", "pp-sum"));  // TODO: This will break if not after deadline

//                Systems.UserIO.askForInput("Provide BLIK code", o -> {
//                    blikCode = (String) o;
//                    triggerNext();  // TODO: Do this better, trigger implicitly somehow?
//                });
            })
            .thenRequestInput("Provide BLIK code", o -> {
                blikCode = (String) o;
//                triggerNext();  // TODO: Do this better, trigger implicitly somehow?
            })
            .then(() -> {
                webCrawler.clickElementByClass("ebok-button-pay-agreement");
                webCrawler.clickElementById("payway-radio-BLIK");
                webCrawler.enterTextToElementById("customerEmail", "r.skupnik@gmail.com");
                webCrawler.clickElementByClassNested("submit-wrapper", "btn"); // TODO: Check content?
                webCrawler.screenshot("/home/pi/test.png");
                System.out.println("Screenshot taken");
                webCrawler.enterTextToElementByClass("blik_input_field_input", blikCode);
                webCrawler.clickElementByClass("button_do_blik_code");

                userIO.output("Payment triggered! Please accept on your phone");    // TODO: Check if no payment anymore?
            })
            .waitForMillis(8000)
            .then(() -> {
                // TODO: Always close browser on exception
                userIO.output("Power bill paid, have a nice day!");
                webCrawler.destroy();
                webCrawler = null;
                blikCode = null;
            }).build()
        );
    }
}
