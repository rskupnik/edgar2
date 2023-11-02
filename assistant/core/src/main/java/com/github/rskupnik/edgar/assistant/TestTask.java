package com.github.rskupnik.edgar.assistant;

public class TestTask extends Task {

    private String test = null;

    protected TestTask() {
        super();
        setSteps(
            Steps.beginWith(() -> {
                Systems.UserIO.output("Test message 1");
            }).thenRequestInput("Asking for input?", o -> {
                test = (String) o;
            }).then(() -> {
                Systems.UserIO.output("Echoing: " + test);
            }).build()
        );
    }
}
