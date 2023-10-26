package com.github.rskupnik.edgar.assistant;

public class WaitStep implements Step {

    private final long timeMillis;

    WaitStep(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public void execute() {
        // TODO: Instead of blocking the thread, wait for counter to fill up and then trigger next task
        try {
            Thread.sleep(timeMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
