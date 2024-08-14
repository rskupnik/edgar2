package com.github.rskupnik.edgar.assistant.steps;

import com.github.rskupnik.edgar.assistant.events.EventManager;
import com.github.rskupnik.edgar.assistant.events.TriggerNextStepEvent;

public class ActionStep implements Step {

    private final Runnable action;

    public ActionStep(Runnable action) {
        this.action = action;
    }

    @Override
    public void execute() {
        action.run();
        EventManager.notify(new TriggerNextStepEvent());
    }
}
