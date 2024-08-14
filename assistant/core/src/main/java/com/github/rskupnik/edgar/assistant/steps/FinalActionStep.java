package com.github.rskupnik.edgar.assistant.steps;

public class FinalActionStep implements Step {

    private final Runnable action;

    public FinalActionStep(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
    }
}
