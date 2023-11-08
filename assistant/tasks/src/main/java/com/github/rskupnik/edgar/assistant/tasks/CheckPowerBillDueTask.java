package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.function.Supplier;

// TODO: Trigger this in a scheduled way
// TODO: Handle a case where there is nothing to pay
// TODO: Optimize the driver init (cache the driver?)
// TODO: Test this on RaspberryPi
public class CheckPowerBillDueTask extends Task {

    public CheckPowerBillDueTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(
            Steps.single(() -> {
                userIO.output("Checking the power bill amount due...");
                WebCrawler webCrawler = instantiateWebCrawler();
                webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
                webCrawler.enterTextToElementById("username1", credentials.get("tauronUsername"));
                webCrawler.enterTextToElementById("password1", credentials.get("tauronPassword"));
                webCrawler.clickElementByClassAndWait("button-pink", 2000L);
                webCrawler.clickElementByClassAndWait("popup-close", 500);
                var amount = webCrawler.getText("amount-column", "amount");
                var date = webCrawler.getText("amount-column", "date");
                userIO.output("Amount to pay: " + amount);
                userIO.output("Deadline: " + date.split(":")[1].trim());    // TODO: Foolproof

                //Systems.UserIO.output("Amount to pay: " + webCrawler.getText("amount", "pp-sum"));  // TODO: This only works when after deadline - handle this


                webCrawler.destroy();
            })
        );
    }
}
