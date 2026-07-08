package com.arch.saga;

public class SagaStep {
    private final String name;
    private final SagaAction action;
    private final CompensationAction compensation;

    public SagaStep(String name, SagaAction action, CompensationAction compensation) {
        this.name = name;
        this.action = action;
        this.compensation = compensation;
    }

    public String getName() { return name; }
    public SagaAction getAction() { return action; }
    public CompensationAction getCompensation() { return compensation; }
}
