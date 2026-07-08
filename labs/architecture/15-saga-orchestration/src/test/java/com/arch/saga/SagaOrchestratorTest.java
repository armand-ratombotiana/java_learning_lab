package com.arch.saga;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SagaOrchestratorTest {
    @Test
    void shouldCompleteSuccessfulSaga() {
        SagaOrchestrator orchestrator = new SagaOrchestrator();
        List<SagaStep> steps = List.of(
            new SagaStep("step1", p -> "result1", p -> {}),
            new SagaStep("step2", p -> "result2", p -> {})
        );
        orchestrator.registerSaga("test", steps);
        String sagaId = orchestrator.startSaga("test", "start");
        assertEquals(SagaStatus.COMPLETED, orchestrator.getStatus(sagaId));
    }

    @Test
    void shouldCompensateOnFailure() {
        SagaOrchestrator orchestrator = new SagaOrchestrator();
        List<SagaStep> steps = List.of(
            new SagaStep("step1", p -> "result1", p -> {}),
            new SagaStep("step2", p -> { throw new RuntimeException("step2 failed"); }, p -> {})
        );
        orchestrator.registerSaga("test", steps);
        String sagaId = orchestrator.startSaga("test", "start");
        assertEquals(SagaStatus.FAILED, orchestrator.getStatus(sagaId));
    }
}
