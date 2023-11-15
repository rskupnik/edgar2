package com.github.rskupnik.edgar.assistant;

public interface WebCrawler {
    void goToWebsite(String url);
    void enterTextToElementById(String id, String text);
    void enterTextToElementByClass(String className, String text);
    void clickElementById(String id);
    void clickElementByClass(String className);
    void clickElementByClassNested(String... classNames);
    String getText(String... classes);
    void screenshot(String destination);
    void destroy();
}
