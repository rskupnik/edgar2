package com.github.rskupnik.edgar.assistant;

public class PayPowerBillTask extends Task {

    private WebCrawler webCrawler = null;

    private String blikCode = null;

    protected PayPowerBillTask() {
        super();
        setSteps(
            Steps.beginWith(() -> {
                // TODO: Cover case where no amount is due

                Systems.UserIO.output("Checking the power bill amount due...");
                webCrawler = new SeleniumChromeWebCrawler();
                webCrawler.goToWebsite("https://logowanie.tauron.pl/login");
                webCrawler.enterTextToElementById("username1", Systems.Credentials.get("tauronUsername"));
                webCrawler.enterTextToElementById("password1", Systems.Credentials.get("tauronPassword"));
                webCrawler.clickElementByClassAndWait("button-pink", 2000L);
                webCrawler.clickElementByClassAndWait("popup-close", 500);
                Systems.UserIO.output("Amount to pay: " + webCrawler.getText("amount", "pp-sum"));  // TODO: This will break if not after deadline

                Systems.UserIO.askForInput("Provide BLIK code", o -> {
                    blikCode = (String) o;
                    triggerNext();  // TODO: Do this better, trigger implicitly somehow?
                });
            }).then(() -> {
                webCrawler.clickElementByClassAndWait("ebok-button-pay-agreement", 3000);
                webCrawler.clickElementByIdAndWait("payway-radio-BLIK", 500);
                webCrawler.enterTextToElementById("customerEmail", "r.skupnik@gmail.com");
                webCrawler.clickElementByClassNestedAndWait( 4000, "submit-wrapper", "btn"); // TODO: Check content?
                webCrawler.enterTextToElementByClass("blik_input_field_input", blikCode);
                webCrawler.clickElementByClassAndWait("button_do_blik_code", 1500);

                Systems.UserIO.output("Payment triggered! Please accept on your phone");    // TODO: Check if no payment anymore?
            })
            .waitForMillis(8000)
            .then(() -> {
                Systems.UserIO.output("Power bill paid, have a nice day!");
                webCrawler.destroy();
                webCrawler = null;
                blikCode = null;
            }).build()
        );
    }
}
