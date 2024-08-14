package com.github.rskupnik.edgar.assistant;

import java.util.function.Consumer;

public interface UserIO extends Subscriber {
    void askForInput(String message, Consumer<Object> inputConsumer);
    void output(String message);
}
