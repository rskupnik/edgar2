package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.function.Supplier;

// TODO: Implement Pay Gas Task
public class PayGasTask extends Task {

//    private final UserIO userIO = new DiscordUserIO();

    private String randomVar = "bla";

    public PayGasTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(
            Steps
            .begin()
            .thenRequestInput("What is the amount to pay?", (result) -> {
                randomVar = (String) result;
            }).then(() -> {
                // UserIO.askForInput("What is the title of transaction?", (o) -> { <parse input o> });
                // UserIO.output("Previous title was: <previous title, need db?>");
                userIO.output("Previous title was: " + randomVar);  // Use Java 21 features here
                System.out.println("2");
            }).then(() -> {
                // UserIO.output("Okay, I will perform the following transaction: <details of transaction>");
                // UserIO.askForInput("Do you want to continue?", (o) -> { <parse input o> });
                System.out.println("3");
            }).then(() -> {
                // UserIO.output("Performing transaction");
                // Payments.execute(<details of payment>);
                // UserIO.output("Transaction requested. Please accept on your phone");
                System.out.println("4");
            }).ifElse((o) -> {      // .waitUntil()? To wait until transaction is accepted or timeout
                // is success?
                return false;
            }, () -> {
                // UserIO.output("Done! Enjoy your day :)");
            }, () -> {
                // UserIO.output("Failed :(");
            }).build()
        );
    }

    // Abort if there is no answer (timeout)

}
