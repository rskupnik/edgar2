module edgar.app {
    requires edgar.assistant.core;
    requires edgar.assistant.userio.discord;
    requires edgar.assistant.webcrawler.selenium;
    requires edgar.assistant.tasks;

    requires edgar.core;
    requires edgar.db;
    requires edgar.http;

    requires jakarta.annotation;
    requires spring.beans;
    requires spring.context;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
}