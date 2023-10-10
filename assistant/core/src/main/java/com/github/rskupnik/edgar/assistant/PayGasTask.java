package com.github.rskupnik.edgar.assistant;

public class PayGasTask extends Task {

    @Override
    void start() {
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

        // Steps? nextStep(STEP) + abstract + provide list of steps?

        /*
         * DSL for constructing the steps?
         * Steps.beginWith(() -> {}).then(() -> {}).ifElse(<predicate>, <func_on_true>, <func_on_false>)
         */
    }
}
