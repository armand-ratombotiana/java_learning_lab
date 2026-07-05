package com.arch.saga;

import java.util.ArrayList;
import java.util.List;

public class SagaOrchestrator {
    private final List<SagaStep> steps = new ArrayList<>();

    public void addStep(SagaStep step) {
        steps.add(step);
    }

    public void execute(String context) {
        int executed = 0;
        try {
            for (SagaStep step : steps) {
                step.execute(context);
                executed++;
            }
            System.out.println("Saga completed successfully for: " + context);
        } catch (Exception e) {
            System.err.println("Saga failed at step " + executed + ": " + e.getMessage());
            for (int i = executed - 1; i >= 0; i--) {
                steps.get(i).compensate(context);
            }
            System.out.println("Compensation completed");
        }
    }
}
