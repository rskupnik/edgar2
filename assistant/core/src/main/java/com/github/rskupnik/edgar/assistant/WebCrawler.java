package com.github.rskupnik.edgar.assistant;

public interface WebCrawler {
    void goToWebsite(String url);
    void enterTextToElementById(String id, String text);
    void enterTextToElementByClass(String className, String text);
    void enterTextToElementByName(String name, String text);
    void clickElementById(String id);
    void clickElementByClass(String className);
    void clickElementByXpath(String xpath);
    void clickElementByClassNested(String... classNames);
    void rememberElementByName(String name, String referenceId);
    boolean checkRememberedElementContainsPropertyEqualTo(String referenceId, String propertyName, String propertyValue);
    String getText(String... classes);
    void screenshot(String destination);
    void destroy();
}
