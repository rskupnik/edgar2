package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.Credentials;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;
import com.github.rskupnik.edgar.assistant.steps.Steps;
import com.github.rskupnik.edgar.assistant.task.Task;

import java.io.*;
import java.util.function.Supplier;

public class PlaywrightTestTask extends Task {

    public PlaywrightTestTask(Credentials credentials, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(credentials, userIO, webCrawlerSupplier);
        setSteps(Steps.beginWith(() -> {
            System.out.println("STARTING TEST");
            try {
                String pipePath = "/tmp/my_pipe"; // Path to the named pipe
                String labelId = "username1";    // Label ID to send to Python

                // Ensure the named pipe exists
                File pipe = new File(pipePath);
                if (pipe.exists()) {
                    pipe.delete();
                    System.out.println("Deleted old pipe");
                }
                Runtime.getRuntime().exec("mkfifo " + pipePath).waitFor();
                System.out.println("Java: Named pipe created.");

                ProcessBuilder processBuilder = new ProcessBuilder(
//                        "/Users/myzek/workspace/priv/edgar2/assistant/playwright-tasks/venv/bin/python",
//                        "/Users/myzek/workspace/priv/edgar2/assistant/playwright-tasks/check-tauron-bill.py"
                        "/home/pi/playwright-test/venv/bin/python",
                        "/home/pi/playwright-test/check-tauron-bill.py"
                );
                processBuilder.redirectError();
                Process process = processBuilder.start();

                // Start threads to capture stdout and stderr from Python
                Thread stdoutThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("Python stdout: " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                Thread stderrThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.err.println("Python stderr: " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // Start the threads
                stdoutThread.start();
                stderrThread.start();

                // Read output from the Python script
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        System.out.println(line); // Print or handle output
//                    }
//                }

                // Wait for the Python script to signal readiness
                System.out.println("Java: Waiting for READY signal from Python...");
                try (BufferedReader pipeReader = new BufferedReader(new FileReader(pipePath))) {
                    String readySignal = pipeReader.readLine();
                    if ("READY".equals(readySignal)) {
                        System.out.println("Java: Received READY signal from Python.");
                    } else {
                        System.out.println("Java: Unexpected signal from Python: " + readySignal);
                        return;
                    }
                }

                System.out.println("Java: Writing label ID to pipe...");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(pipePath))) {
                    writer.write(labelId + "\n");
                    System.out.println("Java: Label ID sent to Python: " + labelId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Wait for output from Python script
                System.out.println("Java: Waiting for output from Python...");
                try (BufferedReader pipeReader = new BufferedReader(new FileReader(pipePath))) {
                    String output = pipeReader.readLine();
                    System.out.println("Output: " + output);
                }

                // Wait for the process to complete and get the exit code
                int exitCode = process.waitFor();
                System.out.println("Python script exited with code: " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).build());
    }
}
