package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;
import com.github.rskupnik.edgar.assistant.webcrawler.alior.AliorWebCrawler;

import java.util.function.Supplier;

public class PayTaxesTask extends Task {

    // TODO: Implement a "finally" step and use it to always close the crawler

    private AliorWebCrawler aliorWebCrawler = null;

    private String vatAmount, ppeAmount, month;

    // TODO: Get rid of this
    private boolean toContinue;

    public PayTaxesTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(
            Steps.beginWith(() -> {
                userIO.output("PAYING TAXES");
            }).thenRequestInput("Please provide the name of the month", m -> {
                month = (String) m;
            }).thenRequestInput("Please provide the amount for VAT tax", m -> {
                vatAmount = (String) m;
            }).thenRequestInput("Please provide the amount for PPE tax", m -> {
                ppeAmount = (String) m;
            }).then(() -> {
                userIO.output("I will use the following params: \nMonth: " + month + "\nVAT amount: " + vatAmount + "\nPPE amount: " + ppeAmount);
            })
            .thenRequestInput("Do you want to continue? yes/no", m -> {
                var decision = (String) m;
                toContinue = decision.equalsIgnoreCase("yes");
                if (!toContinue) {
                    userIO.output("Aborting");
                    // TODO: Make actual and easier aborting possible
                }
            }).then(() -> {
                if (!toContinue) {
                    return;
                }

                userIO.output("Starting in 3s, please be ready to allow one-time access");
                wait(3000);
                aliorWebCrawler = new AliorWebCrawler(instantiateWebCrawler(), this::wait);
                aliorWebCrawler.login(credentials.get("aliorUserId"), credentials.get("aliorPassword"));
                aliorWebCrawler.requestOneTimeAccess();
            }).thenRequestInput("Please allow one-time access and type 'continue'", m -> {
                var message = (String) m;
                // TODO: Support aborting?
                // TODO: Auto-detect and continue without the need to type continue
            }).then(() -> {
                if (!toContinue)
                    return;

                // TODO: Wait before this?
                aliorWebCrawler.switchToBusinessContext();
                // TODO: Enter taxes payment and pay
                aliorWebCrawler.fail();
                aliorWebCrawler.finish();
            })
            .build()
        );
    }
}
