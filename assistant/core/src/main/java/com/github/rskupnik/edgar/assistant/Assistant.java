package com.github.rskupnik.edgar.assistant;

public interface Assistant {
    void registerCommand(String cmd, Class<? extends Task> taskClass);
    void processCommand(String cmd);

    static Assistant defaultImplementation(UserIO userIO, Credentials credentials) {
        return new AssistantImpl(userIO, credentials, SeleniumChromeWebCrawler.class);
    }
}