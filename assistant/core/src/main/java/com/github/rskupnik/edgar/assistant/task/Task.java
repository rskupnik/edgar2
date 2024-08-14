package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.events.EventManager;
import com.github.rskupnik.edgar.assistant.events.TaskTerminatedEvent;
import com.github.rskupnik.edgar.assistant.steps.Steps;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public abstract class Task {

    protected final Credentials credentials;
    protected final UserIO userIO;
    protected final Supplier<WebCrawler> webCrawlerSupplier;

    private Steps steps;
    private int currentStep = 0;

    private LocalDateTime lastExecutionTime = LocalDateTime.now();
    private int timeoutSeconds = 30;

    protected Task(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        this.credentials = credentials;
        this.userIO = userIO;
        this.webCrawlerSupplier = webCrawlerSupplier;
    }

    protected void setSteps(Steps steps) {
        this.steps = steps;
    }

    protected void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public void triggerNext() {
        var stepList = steps.getSteps();
        if (currentStep >= stepList.size()) {
            return;
        }

        stepList.get(currentStep++).execute();

        lastExecutionTime = LocalDateTime.now();

        if (currentStep >= stepList.size() && steps.getFinalizerStep() != null) {
            steps.getFinalizerStep().execute();
        }
    }

    public boolean shouldBeTerminated() {
        return LocalDateTime.now().minusSeconds(timeoutSeconds).isAfter(lastExecutionTime);
    }

    public void terminate() {
        if (steps.getFinalizerStep() != null) {
            steps.getFinalizerStep().execute();
        }
        EventManager.notify(new TaskTerminatedEvent());
    }

    protected WebCrawler instantiateWebCrawler() {
        return webCrawlerSupplier.get();
    }

    protected void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
