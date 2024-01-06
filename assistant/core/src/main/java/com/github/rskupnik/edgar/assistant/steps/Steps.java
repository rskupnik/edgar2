package com.github.rskupnik.edgar.assistant.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Steps {

    private final List<Step> steps;
    private final Step finalizerStep;

    private int currentStep = 0;

    private Steps(List<Step> steps, Step finalizerStep) {
        this.steps = steps;
        this.finalizerStep = finalizerStep;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Step getFinalizerStep() {
        return finalizerStep;
    }

    public Optional<Step> next() {
        return currentStep < steps.size() ? Optional.of(steps.get(currentStep++)) : Optional.empty();
    }

    public static Builder beginWith(Runnable action) {
        return new Builder(action);
    }

    public static Builder begin() {
        return new Builder();
    }

    public static Steps single(Runnable action) {
        return new Builder(action).build();
    }

    public static class Builder {

        private final List<Step> steps = new ArrayList<>();
        private Step finalizerStep = null;

        private Builder(Runnable firstAction) {
            steps.add(new ActionStep(firstAction));
        }

        private Builder() {

        }

        public Builder then(Runnable action) {
            steps.add(new ActionStep(action));
            return this;
        }

        public Builder ifElse(Predicate<Object> condition, Runnable actionSuccess, Runnable actionFail) {
            steps.add(new IfElseStep(condition, actionSuccess, actionFail));
            return this;
        }

        public Builder waitForMillis(long timeMillis) {
            steps.add(new WaitStep(timeMillis));
            return this;
        }

        public Builder thenRequestInput(String message, Consumer<Object> inputConsumer) {
            steps.add(new RequestInputStep(message, inputConsumer));
            return this;
        }

        public Builder finishWith(Runnable action) {
            finalizerStep = new FinalActionStep(action);
            return this;
        }

        public Steps build() {
            return new Steps(steps, finalizerStep);
        }
    }
}
