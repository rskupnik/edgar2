package com.github.rskupnik.edgar.assistant;

public interface WebCrawler {
    void goToWebsite(String url);
    void enterTextToElementById(String id, String text);
    void enterTextToElementByClass(String className, String text);
    void clickElementByIdAndWait(String id, long timeMillis);
    void clickElementByClassAndWait(String className, long timeMillis);
    void clickElementByClassNestedAndWait(long timeMillis, String... classNames);
    String getText(String... classes);
    void destroy();
}
