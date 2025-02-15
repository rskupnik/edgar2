package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.task.PythonTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

public class AsyncOutputPythonTask extends PythonTask {

    private boolean isRecordingOutput = false;
    private StringBuilder outputBuffer;

    public AsyncOutputPythonTask(
            TaskProperties taskProperties,
            UserIO userIO,
            Supplier<WebCrawler> webCrawlerSupplier,
            Map<String, Object> parameters
    ) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
    }

    @Override
    protected void onPipeMessageReceived(String message) {
        if (!isRecordingOutput) {
            if (message.contains("OUTPUT_BEGIN")) {
                isRecordingOutput = true;
                outputBuffer = new StringBuilder();
            } else if (message.contains(":")) {
                super.onPipeMessageReceived(message);
            }
        } else {
            if (message.contains("OUTPUT_END")) {
                isRecordingOutput = false;
                sendToWebhook(outputBuffer.toString());
            } else {
                outputBuffer.append(message);
            }
        }
    }

    private void sendToWebhook(String message) {
        System.out.println("Sending to webhook: " + message);
        try {
            var client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            var response = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("http://n8n:5678/webhook-test/driver-license-test-response"))
                            .header("Content-Type", "application/json")
                            .timeout(Duration.ofSeconds(10))
                            .POST(HttpRequest.BodyPublishers.ofString(message))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
                );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Sent");
    }
}
