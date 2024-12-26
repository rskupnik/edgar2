package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.events.EventManager;
import com.github.rskupnik.edgar.assistant.events.RequestInputEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PythonTask extends ExternalProcessTask {

    public static final String PYTHON_EXECUTABLE_PROPERTY = "tasks.python-executable-path";

    public static final String SCRIPT_LOCATION_PARAM = "script-location-property";
    public static final String PIPE_PATH_PARAM = "pipe-path-property";

    public PythonTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                         Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);

        String scriptLocationProperty = (String) parameters.get(SCRIPT_LOCATION_PARAM);
        String pipePathProperty = (String) parameters.get(PIPE_PATH_PARAM);

        String pythonExecutable = taskProperties.get(PYTHON_EXECUTABLE_PROPERTY);
        String scriptPath = taskProperties.get(scriptLocationProperty);
        String pipePath = taskProperties.get(pipePathProperty);

        try {
            createPipes(pipePath);
            runProcess(pythonExecutable, scriptPath, pipePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onUserAnswerReceived(String answer) {
        pipeWrite(answer);
    }

    @Override
    protected void onPipeMessageReceived(String message) {
        String[] messageFragments = message.split(":");
        String messageType = messageFragments[0].strip().toLowerCase();
        switch (messageType) {
            case "input": {
                String msg = messageFragments[1].strip().toLowerCase();
                EventManager.notify(new RequestInputEvent(msg, input -> onUserAnswerReceived((String) input)));
            }
            case "output": handleOutput(messageFragments[1].strip().toLowerCase());
        }
    }

    protected void handleOutput(String output) {
        this.userIO.output(output);
    }
}
