package com.arch.saga;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SagaOrchestrator {
    private final Map<String, List<SagaStep>> sagas = new ConcurrentHashMap<>();
    private final Map<String, SagaExecution> activeSagas = new ConcurrentHashMap<>();

    public void registerSaga(String sagaName, List<SagaStep> steps) {
        sagas.put(sagaName, Collections.unmodifiableList(new ArrayList<>(steps)));
    }

    public String startSaga(String sagaName, Object initialPayload) {
        List<SagaStep> steps = sagas.get(sagaName);
        if (steps == null) {
            throw new IllegalArgumentException("Unknown saga: " + sagaName);
        }
        String sagaId = UUID.randomUUID().toString();
        SagaExecution execution = new SagaExecution(sagaId, sagaName, steps, initialPayload);
        activeSagas.put(sagaId, execution);
        executeNextStep(execution);
        return sagaId;
    }

    private void executeNextStep(SagaExecution execution) {
        if (execution.isCompleted() || execution.isFailed()) {
            return;
        }
        SagaStep step = execution.getCurrentStep();
        try {
            Object result = step.getAction().execute(execution.getCurrentPayload());
            execution.recordSuccess(result);
            if (execution.hasNextStep()) {
                executeNextStep(execution);
            }
        } catch (Exception e) {
            execution.recordFailure(e);
            compensate(execution);
        }
    }

    private void compensate(SagaExecution execution) {
        List<SagaStep> completedSteps = execution.getCompletedSteps();
        ListIterator<SagaStep> iterator = completedSteps.listIterator(completedSteps.size());
        while (iterator.hasPrevious()) {
            SagaStep step = iterator.previous();
            try {
                step.getCompensation().compensate(execution.getCurrentPayload());
            } catch (Exception e) {
                System.err.println("Compensation failed for step: " + step.getName() + ": " + e.getMessage());
            }
        }
    }

    public SagaStatus getStatus(String sagaId) {
        SagaExecution execution = activeSagas.get(sagaId);
        if (execution == null) throw new IllegalArgumentException("Unknown saga: " + sagaId);
        return execution.getStatus();
    }
}

class SagaExecution {
    private final String sagaId;
    private final String sagaName;
    private final List<SagaStep> steps;
    private final List<SagaStep> completedSteps = new ArrayList<>();
    private int currentStepIndex = 0;
    private Object currentPayload;
    private SagaStatus status = SagaStatus.IN_PROGRESS;
    private String errorMessage;

    public SagaExecution(String sagaId, String sagaName, List<SagaStep> steps, Object initialPayload) {
        this.sagaId = sagaId;
        this.sagaName = sagaName;
        this.steps = steps;
        this.currentPayload = initialPayload;
    }

    public void recordSuccess(Object result) {
        completedSteps.add(steps.get(currentStepIndex));
        currentPayload = result;
        currentStepIndex++;
        if (currentStepIndex >= steps.size()) {
            status = SagaStatus.COMPLETED;
        }
    }

    public void recordFailure(Exception e) {
        status = SagaStatus.FAILED;
        this.errorMessage = e.getMessage();
    }

    public SagaStep getCurrentStep() { return steps.get(currentStepIndex); }
    public Object getCurrentPayload() { return currentPayload; }
    public boolean hasNextStep() { return currentStepIndex < steps.size(); }
    public boolean isCompleted() { return status == SagaStatus.COMPLETED; }
    public boolean isFailed() { return status == SagaStatus.FAILED; }
    public SagaStatus getStatus() { return status; }
    public List<SagaStep> getCompletedSteps() { return List.copyOf(completedSteps); }
}

enum SagaStatus { IN_PROGRESS, COMPLETED, FAILED }
