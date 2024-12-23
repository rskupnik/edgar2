package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;

import java.util.Map;
import java.util.function.Supplier;

public class PythonTask extends ExternalProcessTask {

    public static final String SCRIPT_LOCATION_PROPERTY = "script-location-property";
    public static final String PIPE_PATH_PROPERTY = "pipe-path-property";

    protected PythonTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                         Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);

        String scriptLocationProperty = (String) parameters.get(SCRIPT_LOCATION_PROPERTY);
        String pipePath = (String) parameters.get(PIPE_PATH_PROPERTY);

        try {
            createPipe(taskProperties.get(pipePath));
            runProcess(
                    taskProperties.get("tasks.python-executable-path"),
                    taskProperties.get(scriptLocationProperty)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
