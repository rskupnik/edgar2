package com.github.rskupnik.edgar.processors;

public class OutputDiscordImageProcessor implements Processor<byte[], Void> {

    private final String user;

    public OutputDiscordImageProcessor(String user) {
        this.user = user;
    }

    public static OutputDiscordImageProcessor fromSomething() {
        return null;
    }

    @Override
    public String id() {
        return "outputDiscordImage";
    }

    @Override
    public Void process(byte[] input) {
        // TODO: Send the bytes as image to Discord User
        return null;
    }
}
