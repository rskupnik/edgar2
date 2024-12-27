package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;

import java.util.Map;
import java.util.function.Supplier;

public abstract class Task {

    protected final TaskProperties taskProperties;
    protected final UserIO userIO;
    protected final Supplier<WebCrawler> webCrawlerSupplier;
    protected final Map<String, Object> parameters;

    protected Task(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                   Map<String, Object> parameters) {
        this.taskProperties = taskProperties;
        this.userIO = userIO;
        this.webCrawlerSupplier = webCrawlerSupplier;
        this.parameters = parameters;
    }

    protected WebCrawler instantiateWebCrawler() {
        return webCrawlerSupplier.get();
    }

    protected void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
