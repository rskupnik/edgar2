package com.github.rskupnik.edgar.assistant;

public abstract class Task {

    private final Steps steps;

    protected Task(Steps steps) {
        this.steps = steps;
    }

    abstract void start();
}
