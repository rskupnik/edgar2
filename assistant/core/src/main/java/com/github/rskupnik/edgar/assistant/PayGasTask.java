package com.github.rskupnik.edgar.assistant;

public class PayGasTask extends Task {

    private final UserIO userIO = null;

    private String randomVar = "bla";

    protected PayGasTask() {
        super();
        setSteps(
            Steps.beginWith(() -> { // TODO: AskForInput task? ActionTasks by default trigger next?
                // TODO: UserIO.askForInput("What is the amount to pay?", (o) -> { <parse input o> });
                Systems.UserIO.askForInput("What is the amount to pay?", (result) -> {randomVar = (String) result; triggerNext();});
            }).then(() -> {
                // TODO: UserIO.askForInput("What is the title of transaction?", (o) -> { <parse input o> });
                // TODO: UserIO.output("Previous title was: <previous title, need db?>");
                Systems.UserIO.output("Previous title was: " + randomVar);
                System.out.println("2");
                triggerNext();
            }).then(() -> {
                // TODO: UserIO.output("Okay, I will perform the following transaction: <details of transaction>");
                // TODO: UserIO.askForInput("Do you want to continue?", (o) -> { <parse input o> });
                System.out.println("3");
                triggerNext();
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

    // TODO: State machine?

    // TODO: How to end the task and inform Assistant?

    // TODO: Abort if there is no answer

    // TODO: Make step wait for input?

    // TODO: Get rid of Systems? Instead pass classes to specific Tasks

    // TODO: Discord dependency as separate module?

    // TODO: Packaging + modules

    // TODO: Separate gradle submodule for exposing the assistant app as spring component for autostart?

}
