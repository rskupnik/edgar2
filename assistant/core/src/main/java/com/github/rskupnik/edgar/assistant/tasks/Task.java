package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;

import java.util.function.Supplier;

public abstract class Task {

    // TODO: Assign these with a constructor?
    public Credentials credentials;
    public UserIO userIO;
    public Supplier<WebCrawler> webCrawlerSupplier;

    private Steps steps;
    private int currentStep = 0;

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

    WebCrawler instantiateWebCrawler() {
        return webCrawlerSupplier.get();
    }
}
