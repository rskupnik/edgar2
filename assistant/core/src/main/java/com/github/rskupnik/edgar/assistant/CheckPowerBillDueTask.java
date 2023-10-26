package com.github.rskupnik.edgar.assistant;

// TODO: If command is not recognized, send a list of all possible commands
// TODO: Trigger this in a scheduled way
// TODO: Handle a case where there is nothing to pay
// TODO: Optimize the driver init (cache the driver?)
public class CheckPowerBillDueTask extends Task {

    protected CheckPowerBillDueTask() {
        super();
        setSteps(
            Steps.single(() -> {
                Systems.UserIO.output("Checking the power bill amount due...");
                WebCrawler webCrawler = new SeleniumChromeWebCrawler();
                webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
                webCrawler.enterTextToElementById("username1", Systems.Credentials.get("tauronUsername"));
                webCrawler.enterTextToElementById("password1", Systems.Credentials.get("tauronPassword"));
                webCrawler.clickElementByClassAndWait("button-pink", 2000L);
                Systems.UserIO.output("Amount to pay: " + webCrawler.getText("amount", "pp-sum"));
                webCrawler.destroy();
            })
        );
    }
}
