package com.github.rskupnik.edgar.assistant.steps;

import com.github.rskupnik.edgar.assistant.events.EventManager;
import com.github.rskupnik.edgar.assistant.events.RequestInputEvent;

import java.util.function.Consumer;

public class RequestInputStep implements Step {

    private final String message;
    private final Consumer<Object> inputConsumer;

    RequestInputStep(String message, Consumer<Object> inputConsumer) {
        this.message = message;
        this.inputConsumer = inputConsumer;
    }

    @Override
    public void execute() {
        EventManager.notify(new RequestInputEvent(message, inputConsumer));
    }
}
