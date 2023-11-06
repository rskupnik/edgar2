package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.steps.Steps;

public class TestTask extends Task {

    private String test = null;

    public TestTask() {
        setSteps(
            Steps.beginWith(() -> {
                userIO.output("Test message 1");
            }).thenRequestInput("Asking for input?", o -> {
                test = (String) o;
            }).then(() -> {
                userIO.output("Echoing: " + test);
            }).build()
        );
    }
}
