package com.github.rskupnik.edgar.assistant.steps;

import java.util.function.Predicate;

public class IfElseStep implements Step {

    private final Predicate<Object> condition;
    private final Runnable actionSuccess;
    private final Runnable actionFail;

    public IfElseStep(Predicate<Object> condition, Runnable actionSuccess, Runnable actionFail) {
        this.condition = condition;
        this.actionFail = actionFail;
        this.actionSuccess = actionSuccess;
    }

    @Override
    public void execute() {

    }
}
