package com.github.rskupnik.edgar.assistant;

// TODO: Trigger this in a scheduled way
// TODO: Handle a case where there is nothing to pay
// TODO: Optimize the driver init (cache the driver?)
// TODO: Test this on RaspberryPi
public class CheckPowerBillDueTask extends Task {

    protected CheckPowerBillDueTask() {
        setSteps(
            Steps.single(() -> {
                userIO.output("Checking the power bill amount due...");
                WebCrawler webCrawler = instantiateWebCrawler();
                webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
                webCrawler.enterTextToElementById("username1", credentials.get("tauronUsername"));
                webCrawler.enterTextToElementById("password1", credentials.get("tauronPassword"));
                webCrawler.clickElementByClassAndWait("button-pink", 2000L);
                webCrawler.clickElementByClassAndWait("popup-close", 500);
                var amount = webCrawler.getText("amount-column", "toggle-box", "amount");
                var date = webCrawler.getText("amount-column", "toggle-box", "date");
                userIO.output("Amount to pay: " + amount);
                userIO.output("Deadline: " + date.split(":")[1].trim());    // TODO: Foolproof

                //Systems.UserIO.output("Amount to pay: " + webCrawler.getText("amount", "pp-sum"));  // TODO: This only works when after deadline - handle this


                webCrawler.destroy();
            })
        );
    }
}
