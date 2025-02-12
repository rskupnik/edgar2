package com.github.rskupnik.edgar.assistant;

import com.github.rskupnik.edgar.assistant.task.Task;
import com.github.rskupnik.edgar.assistant.task.TaskRegistration;

import java.util.Map;
import java.util.function.Supplier;

public interface Assistant {
    void registerCommand(String cmd, TaskDescriptor taskDescriptor);
    void processCommand(String cmd);
    void processCommandHeadless(String cmd);

    static Assistant defaultImplementation(
            UserIO userIO,
            TaskProperties taskProperties,
            Supplier<WebCrawler> webCrawlerSupplier,
            TaskRegistration... taskRegistrations
    ) {
        return new AssistantImpl(userIO, taskProperties, webCrawlerSupplier, taskRegistrations);
    }

    record TaskDescriptor(Class<? extends Task> taskClass, Map<String, Object> params) {}
}