package com.github.rskupnik.edgar.assistant;

import java.util.function.Consumer;

public class DiscordUserIO implements UserIO {

    @Override
    public void askForInput(String message, Consumer<Object> inputConsumer) {
        System.out.println(message);
        inputConsumer.accept("returned input");
    }

    @Override
    public void output(String message) {
        System.out.println(message);
    }
}
