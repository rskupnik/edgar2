package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.function.Supplier;

public class PayPowerBillTask extends Task {

    private WebCrawler webCrawler = null;

    private String blikCode = null;

    public PayPowerBillTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(
            Steps.beginWith(() -> {
                // TODO: Cover case where no amount is due

                userIO.output("Checking the power bill amount due...");
                webCrawler = instantiateWebCrawler();
                webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
                webCrawler.enterTextToElementById("username1", credentials.get("tauronUsername"));
                webCrawler.enterTextToElementById("password1", credentials.get("tauronPassword"));
                webCrawler.clickElementByClassAndWait("button-pink", 2000L);
                webCrawler.clickElementByClassAndWait("popup-close", 500);
                userIO.output("Amount to pay: " + webCrawler.getText("amount", "pp-sum"));  // TODO: This will break if not after deadline

//                Systems.UserIO.askForInput("Provide BLIK code", o -> {
//                    blikCode = (String) o;
//                    triggerNext();  // TODO: Do this better, trigger implicitly somehow?
//                });
            })
            .thenRequestInput("Provide BLIK code", o -> {
                blikCode = (String) o;
                triggerNext();  // TODO: Do this better, trigger implicitly somehow?
            })
            .then(() -> {
                webCrawler.clickElementByClassAndWait("ebok-button-pay-agreement", 3000);
                webCrawler.clickElementByIdAndWait("payway-radio-BLIK", 500);
                webCrawler.enterTextToElementById("customerEmail", "r.skupnik@gmail.com");
                webCrawler.clickElementByClassNestedAndWait( 4000, "submit-wrapper", "btn"); // TODO: Check content?
                webCrawler.enterTextToElementByClass("blik_input_field_input", blikCode);
                webCrawler.clickElementByClassAndWait("button_do_blik_code", 1500);

                userIO.output("Payment triggered! Please accept on your phone");    // TODO: Check if no payment anymore?
            })
            .waitForMillis(8000)
            .then(() -> {
                userIO.output("Power bill paid, have a nice day!");
                webCrawler.destroy();
                webCrawler = null;
                blikCode = null;
            }).build()
        );
    }
}
