module edgar.assistant.tasks {
    requires edgar.assistant.core;
    requires edgar.assistant.webcrawler.alior;
    requires java.net.http;

    exports com.github.rskupnik.edgar.assistant.tasks;
}