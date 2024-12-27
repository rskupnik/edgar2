package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.events.EventManager;
import com.github.rskupnik.edgar.assistant.events.RequestInputEvent;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class PythonTask extends ExternalProcessTask {

    private static final String PYTHON_EXECUTABLE = "tasks.python-executable-path";

    public static final String SCRIPT_LOCATION_PROP = "script-location-property";
    public static final String PIPE_PATH_PROP = "pipe-path-property";
    public static final String ADDITIONAL_ARGUMENTS = "additional-arguments";

    public PythonTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                         Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);

        String scriptLocationProperty = (String) parameters.get(SCRIPT_LOCATION_PROP);
        String pipePathProperty = (String) parameters.get(PIPE_PATH_PROP);

        String pythonExecutable = taskProperties.get(PYTHON_EXECUTABLE);
        String scriptPath = taskProperties.get(scriptLocationProperty);
        String pipePath = taskProperties.get(pipePathProperty);

        String[] additionalArguments = (String[]) parameters.getOrDefault(ADDITIONAL_ARGUMENTS, new String[] {});

        try {
            createPipes(pipePath);
            runProcess(
                    Stream.concat(
                            Stream.of(pythonExecutable, scriptPath, pipePath),
                            Stream.of(additionalArguments)
                    ).toArray(String[]::new)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onUserAnswerReceived(String answer) {
        pipeWrite(answer);
    }

    @Override
    protected void onPipeMessageReceived(String message) {
        String[] messageFragments = message.split(":", 2);
        String messageType = messageFragments[0].strip().toLowerCase();
        switch (messageType) {
            case "input": {
                String msg = messageFragments[1].strip();
                EventManager.notify(new RequestInputEvent(msg, input -> onUserAnswerReceived((String) input)));
                break;
            }
            case "output": {
                handleOutput(messageFragments[1].strip());
                break;
            }
        }
    }

    protected void handleOutput(String output) {
        this.userIO.output(output);
    }
}
