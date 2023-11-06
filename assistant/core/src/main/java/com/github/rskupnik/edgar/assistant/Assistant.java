package com.github.rskupnik.edgar.assistant;

import com.github.rskupnik.edgar.assistant.tasks.Task;

import java.util.function.Supplier;

public interface Assistant {
    void registerCommand(String cmd, Class<? extends Task> taskClass);
    void processCommand(String cmd);

    static Assistant defaultImplementation(UserIO userIO, Credentials credentials, Supplier<WebCrawler> webCrawlerSupplier) {
        return new AssistantImpl(userIO, credentials, webCrawlerSupplier);
    }
}