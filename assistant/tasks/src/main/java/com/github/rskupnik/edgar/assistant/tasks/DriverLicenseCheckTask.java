package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.task.PythonTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

public class DriverLicenseCheckTask extends PythonTask {

    private boolean isRecordingOutput = false;
    private StringBuilder outputBuffer;

    public DriverLicenseCheckTask(
            TaskProperties taskProperties,
            UserIO userIO,
            Supplier<WebCrawler> webCrawlerSupplier,
            Map<String, Object> parameters
    ) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
    }

    @Override
    protected void preRunHook() {
        // Write data to a file
        System.out.println("DATA RECEIVED");
        System.out.println(this.parameters.get("data"));
        String data = this.parameters.get("data").toString();

        try {
            // Write to the file, creating if necessary and truncating if it exists
            Files.writeString(Path.of("/tmp/driverCheckInput"), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("File created/overwritten and data written at: /tmp/driverCheckInput");
        } catch (IOException e) {
            System.err.println("Error creating or writing to file: " + e.getMessage());
        }
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
//                            .uri(URI.create("http://n8n:5678/webhook-test/driver-license-test-response"))
                            .uri(URI.create("http://n8n.home/webhook-test/driver-license-test-response"))
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
