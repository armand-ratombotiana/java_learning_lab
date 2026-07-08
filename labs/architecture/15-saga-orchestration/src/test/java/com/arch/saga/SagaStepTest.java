package com.arch.saga;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SagaStepTest {
    @Test
    void shouldCreateSagaStep() {
        SagaStep step = new SagaStep("test", p -> "result", p -> {});
        assertEquals("test", step.getName());
    }

    @Test
    void shouldExecuteAction() {
        SagaStep step = new SagaStep("test", p -> "executed:" + p, p -> {});
        Object result = step.getAction().execute("input");
        assertEquals("executed:input", result);
    }

    @Test
    void shouldExecuteCompensation() {
        final boolean[] compensated = {false};
        SagaStep step = new SagaStep("test", p -> "ok", p -> compensated[0] = true);
        step.getCompensation().compensate("data");
        assertTrue(compensated[0]);
    }
}
