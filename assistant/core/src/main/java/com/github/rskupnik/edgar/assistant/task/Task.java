package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;

import java.util.function.Supplier;

public abstract class Task {

    protected final Credentials credentials;
    protected final UserIO userIO;
    protected final Supplier<WebCrawler> webCrawlerSupplier;

    private Steps steps;
    private int currentStep = 0;

    protected Task(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        this.credentials = credentials;
        this.userIO = userIO;
        this.webCrawlerSupplier = webCrawlerSupplier;
    }

    protected void setSteps(Steps steps) {
        this.steps = steps;
    }

    public void triggerNext() {
        var stepList = steps.getSteps();
        if (currentStep >= stepList.size()) {
            return;
        }

        stepList.get(currentStep++).execute();
    }

    protected WebCrawler instantiateWebCrawler() {
        return webCrawlerSupplier.get();
    }
}
