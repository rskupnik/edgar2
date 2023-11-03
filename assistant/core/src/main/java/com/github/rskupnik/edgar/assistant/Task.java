package com.github.rskupnik.edgar.assistant;

import java.lang.reflect.InvocationTargetException;

public abstract class Task {

    // TODO: Assign these with a constructor?
    Credentials credentials;
    UserIO userIO;
    Class<? extends WebCrawler> webCrawlerImplementation;

    private Steps steps;
    private int currentStep = 0;

    protected void setSteps(Steps steps) {
        this.steps = steps;
    }

    void triggerNext() {
        var stepList = steps.getSteps();
        if (currentStep >= stepList.size()) {
            return;
        }

        stepList.get(currentStep++).execute();
    }

    WebCrawler instantiateWebCrawler() {
        try {
            return webCrawlerImplementation.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;    // TODO: Optional?
    }
}
