package com.github.rskupnik.edgar.assistant;


import java.util.Map;

public interface Assistant {
    void registerCommand(String cmd, Class<? extends Task> taskClass);
    void processCommand(String cmd);

    public static Assistant defaultImplementation(Map<String, String> credentials) {
        return new AssistantImpl(credentials);
    }
}