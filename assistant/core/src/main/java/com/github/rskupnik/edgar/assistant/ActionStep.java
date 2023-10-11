package com.github.rskupnik.edgar.assistant;

public class ActionStep implements Step {

    private final Runnable action;

    public ActionStep(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
    }
}
