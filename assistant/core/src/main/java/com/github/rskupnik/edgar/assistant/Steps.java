package com.github.rskupnik.edgar.assistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Steps {

    private final List<Step> steps;

    private int currentStep = 0;

    private Steps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Optional<Step> next() {
        return currentStep < steps.size() ? Optional.of(steps.get(currentStep++)) : Optional.empty();
    }

    public static Builder beginWith(Runnable action) {
        return new Builder(action);
    }

    public static Steps single(Runnable action) {
        return new Builder(action).build();
    }

    static class Builder {

        private final List<Step> steps = new ArrayList<>();

        Builder(Runnable firstAction) {
            steps.add(new ActionStep(firstAction));
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

        public Steps build() {
            return new Steps(steps);
        }
    }
}
