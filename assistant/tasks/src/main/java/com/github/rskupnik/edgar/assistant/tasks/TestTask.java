package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.StepTask;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.util.Map;
import java.util.function.Supplier;

public class TestTask extends StepTask {

    private String test = null;

    public TestTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                    Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
        setSteps(
            Steps.beginWith(() -> {
                userIO.output("Test message 1");
            }).thenRequestInput("Asking for input?", o -> {
                test = (String) o;
            }).then(() -> {
                userIO.output("Echoing: " + test);
            }).build()
        );
    }
}

// TODO: TASK: pay taxes
// TODO: TASK: pay gas
//      - Both require bank website scraping, might be dangerous - consider alternatives?
// TODO: TASK: Send docs to księgowa?
//      - Requires GMAIL API
//      - Requires Conditionals
// TODO: TASK: Read gas counter (need cam + wifi extension)
//      - Requires significant hand work
// TODO: TASK: Pay VWFS