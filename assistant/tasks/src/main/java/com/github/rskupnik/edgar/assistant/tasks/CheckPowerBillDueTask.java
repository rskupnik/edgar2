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
                webCrawler.clickElementByClass("button-pink");
                webCrawler.clickElementByClass("popup-close");
                // TODO: toggle-box in the middle only works if not after deadline?
                var amount = webCrawler.getText("amount-column", "toggle-box", "amount");
                var date = webCrawler.getText("amount-column", "toggle-box", "date");

                userIO.output("Amount to pay: " + amount);
                if (date.contains("Po terminie")) {
                    userIO.output("Deadline exceeded! You are " + date.split(":")[1].trim() + " late");
                } else {
                    userIO.output("Deadline: " + date.split(":")[1].trim());
                }

                webCrawler.destroy();
            })
        );
    }
}
