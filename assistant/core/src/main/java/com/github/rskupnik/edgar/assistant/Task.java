package com.github.rskupnik.edgar.assistant;

public abstract class Task {

    // TODO: Assign these with a constructor?
    Credentials credentials;
    UserIO userIO;

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
}
