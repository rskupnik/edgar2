package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.ExternalProcessTask;

import java.util.function.Supplier;

public class PlaywrightTestTask extends ExternalProcessTask {

    public PlaywrightTestTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(taskProperties, userIO, webCrawlerSupplier);
        setSteps(Steps.beginWith(() -> {
            System.out.println("STARTING TEST");

            try {
                createPipe("/tmp/playwright-test");
                runProcess(
                        taskProperties.get("tasks.python-executable-path"),
                        taskProperties.get("tasks.check-tauron-power-bill.script-location")
                );

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
