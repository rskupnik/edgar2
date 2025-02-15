package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;

import java.io.*;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ExternalProcessTask extends Task {

    protected String syncPipe;
    protected String asyncPipe;
    protected Process process;

    protected ExternalProcessTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier,
                                  Map<String, Object> parameters) {
        super(taskProperties, userIO, webCrawlerSupplier, parameters);
    }

    protected void createPipes(String pipePath) throws Exception {
        File pipe = new File(pipePath);
        if (pipe.exists()) {
            pipe.delete();
        }
        Runtime.getRuntime().exec("mkfifo " + pipePath).waitFor();
        this.syncPipe = pipePath;

        var asyncPipePath = pipePath + "_async";
        File asyncPipe = new File(asyncPipePath);
        if (asyncPipe.exists()) {
            asyncPipe.delete();
        }
        Runtime.getRuntime().exec("mkfifo " + asyncPipePath).waitFor();
        this.asyncPipe = asyncPipePath;
    }

    protected void runProcess(String... arguments) throws Exception {
        preRunHook();

        Process process = new ProcessBuilder(arguments).start();
        this.process = process;

        // Start threads to capture stdout and stderr from Python
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[" + process.pid() + "]: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("[" + process.pid() + "]: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Start a worker thread for handling interactions
        var t = new Thread(() -> {
            try (BufferedReader pipeReader = new BufferedReader(new FileReader(this.asyncPipe))) {
                while (process.isAlive()) {
                    String line;
                    while ((line = pipeReader.readLine()) != null) {
                        this.onPipeMessageReceived(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setName("python-handling-thread-" + syncPipe);
        t.start();
    }

    protected void pipeWrite(String contents) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.syncPipe))) {
            writer.write(contents + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String pipeRead() throws Exception {
        try (BufferedReader pipeReader = new BufferedReader(new FileReader(this.syncPipe))) {
            String output = pipeReader.readLine();
            return output;
        }
    }

    protected void awaitProcessFinished() throws Exception {
        this.process.waitFor();
    }

    protected void preRunHook() {

    }

    protected abstract void onPipeMessageReceived(String message);
}
