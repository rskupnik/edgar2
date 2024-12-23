package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.ExternalProcessTask;
import com.github.rskupnik.edgar.assistant.task.PythonTask;

import java.util.Map;
import java.util.function.Supplier;

public class PlaywrightTestTask extends PythonTask {

    public PlaywrightTestTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                              Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
        setSteps(Steps.beginWith(() -> {
            System.out.println("STARTING TEST");

            try {
                String isReadySignal = pipeRead();
                if (!"READY".equalsIgnoreCase(isReadySignal)) {
                    System.out.println("Invalid signal received: " + isReadySignal);
                    return;
                }

                pipeWrite("username1");

                String output = pipeRead();
                System.out.println("Output: " + output);

                awaitProcessFinished();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).build());
    }
}
