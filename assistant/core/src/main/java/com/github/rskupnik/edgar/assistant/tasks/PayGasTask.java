package com.github.rskupnik.edgar.assistant.tasks;

import com.github.rskupnik.edgar.assistant.steps.Steps;

public class PayGasTask extends Task {

//    private final UserIO userIO = new DiscordUserIO();

    private String randomVar = "bla";

    public PayGasTask() {
        setSteps(
            Steps
            .begin()
            .thenRequestInput("What is the amount to pay?", (result) -> {
                randomVar = (String) result;
            }).then(() -> {
                // TODO: UserIO.askForInput("What is the title of transaction?", (o) -> { <parse input o> });
                // TODO: UserIO.output("Previous title was: <previous title, need db?>");
                userIO.output("Previous title was: " + randomVar);  // TODO: Use Java 21 features here
                System.out.println("2");
            }).then(() -> {
                // TODO: UserIO.output("Okay, I will perform the following transaction: <details of transaction>");
                // TODO: UserIO.askForInput("Do you want to continue?", (o) -> { <parse input o> });
                System.out.println("3");
            }).then(() -> {
                // TODO: UserIO.output("Performing transaction");
                // TODO: Payments.execute(<details of payment>);
                // TODO: UserIO.output("Transaction requested. Please accept on your phone");
                System.out.println("4");
            }).ifElse((o) -> {      // TODO: .waitUntil()? To wait until transaction is accepted or timeout
                // is success?
                return false;
            }, () -> {
                // TODO: UserIO.output("Done! Enjoy your day :)");
            }, () -> {
                // TODO: UserIO.output("Failed :(");
            }).build()
        );
    }

    // TODO: Make asking for input async somehow?
    // TODO: UserIO.askForInput("What is the amount to pay?", (o) -> { <parse input o> });
    // TODO: UserIO.askForInput("What is the title of transaction?", (o) -> { <parse input o> });
    // TODO: UserIO.output("Previous title was: <previous title, need db?>");
    // TODO: UserIO.output("Okay, I will perform the following transaction: <details of transaction>");
    // TODO: UserIO.askForInput("Do you want to continue?", (o) -> { <parse input o> });
    // TODO: UserIO.output("Performing transaction");
    // TODO: Payments.execute(<details of payment>);
    // TODO: UserIO.output("Transaction requested. Please accept on your phone");
    // TODO: UserIO.output("Done! Enjoy your day :)");

    // TODO: How to end the task and inform Assistant?

    // TODO: Abort if there is no answer (timeout)

    // TODO: Discord dependency as separate module?

    // TODO: Packaging + modules

    // TODO: Separate gradle submodule for exposing the assistant app as spring component for autostart?

}
