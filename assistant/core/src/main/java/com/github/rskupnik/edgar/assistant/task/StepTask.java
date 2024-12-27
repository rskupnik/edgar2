package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.events.EventManager;
import com.github.rskupnik.edgar.assistant.events.TaskTerminatedEvent;
import com.github.rskupnik.edgar.assistant.steps.Steps;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

public abstract class StepTask extends Task{

    private Steps steps;
    private int currentStep = 0;

    private LocalDateTime lastExecutionTime = LocalDateTime.now();
    private int timeoutSeconds = 30;

    protected StepTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier, Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
    }

    protected void setSteps(Steps steps) {
        this.steps = steps;
    }

    public void triggerNext() {
        if (steps == null)
            return;

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

    protected void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
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
}
