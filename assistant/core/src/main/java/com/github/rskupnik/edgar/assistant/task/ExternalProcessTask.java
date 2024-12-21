package com.github.rskupnik.edgar.assistant.task;

import com.github.rskupnik.edgar.assistant.TaskProperties;
import com.github.rskupnik.edgar.assistant.UserIO;
import com.github.rskupnik.edgar.assistant.WebCrawler;

import java.io.*;
import java.util.function.Supplier;

public abstract class ExternalProcessTask extends Task {

    protected String pipe;
    protected Process process;

    protected ExternalProcessTask(TaskProperties taskProperties, UserIO userIO, Supplier<WebCrawler> webCrawlerSupplier) {
        super(taskProperties, userIO, webCrawlerSupplier);
    }

    protected void createPipe(String pipePath) throws Exception {
        File pipe = new File(pipePath);
        if (pipe.exists()) {
            pipe.delete();
        }
        Runtime.getRuntime().exec("mkfifo " + pipePath).waitFor();
        this.pipe = pipePath;
    }

    protected void runProcess(String... arguments) throws Exception {
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
    }

    protected void pipeWrite(String contents) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.pipe))) {
            writer.write(contents + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String pipeRead() throws Exception {
        try (BufferedReader pipeReader = new BufferedReader(new FileReader(this.pipe))) {
            String output = pipeReader.readLine();
            return output;
        }
    }

    protected void awaitProcessFinished() throws Exception {
        this.process.waitFor();
    }
}
