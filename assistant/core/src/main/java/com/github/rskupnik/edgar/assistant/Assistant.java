package com.github.rskupnik.edgar.assistant;


public interface Assistant {
    void processCommand(String cmd);

    public static Assistant defaultImplementation() {
        return new AssistantImpl();
    }
}