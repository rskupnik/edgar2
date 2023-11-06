package com.github.rskupnik.edgar.assistant.events;

import java.util.function.Consumer;

public record RequestInputEvent(String message, Consumer<Object> inputConsumer) implements Event {
}
