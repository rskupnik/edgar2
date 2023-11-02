package com.github.rskupnik.edgar.assistant;

public abstract class Task implements Subscriber {

    private Steps steps;
    private int currentStep = 0;

    String randomVar2;

    Task() {
        EventManager.subscribe(TriggerNextStepEvent.class, this);
    }

    protected void setSteps(Steps steps) {
        this.steps = steps;
    }

//    void start() {
//        System.out.println("Task::start");
//        if (steps == null)
//            return;
//
//        System.out.println("past null check");
//        var step = steps.next();
//        while (step.isPresent()) {
//            System.out.println("step loop tick");
//            step.get().execute();
//            step = steps.next();
//        }
//    }

    void triggerNext() {
        var stepList = steps.getSteps();
        if (currentStep >= stepList.size()) {
            return;
        }

        stepList.get(currentStep++).execute();
    }

    @Override
    public void update(Event event) {
        if (event.getClass() == TriggerNextStepEvent.class) {
            triggerNext();
        }
    }
}
