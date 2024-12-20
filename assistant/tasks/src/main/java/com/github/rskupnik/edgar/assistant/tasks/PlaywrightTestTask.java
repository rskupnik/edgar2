package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.ExternalProcessTask;

import java.util.function.Supplier;

public class PlaywrightTestTask extends ExternalProcessTask {

    public PlaywrightTestTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(Steps.beginWith(() -> {
            System.out.println("STARTING TEST");

            try {
                createPipe("/tmp/playwright-test");
                runProcess(
//                        "/Users/myzek/workspace/priv/edgar2/assistant/playwright-tasks/venv/bin/python",
//                        "/Users/myzek/workspace/priv/edgar2/assistant/playwright-tasks/check-tauron-bill.py"
//                        "/home/pi/playwright-test/venv/bin/python",
//                        "/home/pi/playwright-test/check-tauron-bill.py"
                        "/home/rskupnik/workspace/priv/edgar2/assistant/playwright-tasks/venv/bin/python",
                        "/home/rskupnik/workspace/priv/edgar2/assistant/playwright-tasks/check-tauron-bill.py"
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
