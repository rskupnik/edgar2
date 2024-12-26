package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.task.PythonTask;

import java.util.Map;
import java.util.function.Supplier;

/**
 * This class serves as an example on how you can override the PythonTask to customize the functionality
 */
public class OverriddenPythonTaskExample extends PythonTask {

    public OverriddenPythonTaskExample(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                                       Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
    }

    /**
     * Can override this function to handle output in a special way or add additional handling.
     * Call super.handleOutput to also launch the default behaviour (which is to send the output to discord)
     * @param output
     */
    @Override
    public void handleOutput(String output) {
        System.out.println("Output: " + output);
        super.handleOutput(output);
    }

    /**
     * Can override this function to handle pipe messages from the script in a custom way.
     * Best way is to add custom handling and then delegate to super for default handling
     * @param message
     */
    @Override
    protected void onPipeMessageReceived(String message) {
        System.out.println("Pipe message: " + message);
        super.onPipeMessageReceived(message);
    }
}
