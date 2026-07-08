package com.arch.circuitbreaker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

class ResilientServiceTest {
    @Test
    void shouldExecuteSuccessfully() {
        ResilientService svc = new ResilientService(5, Duration.ofSeconds(10), 10, 0, Duration.ofMillis(1));
        String result = svc.execute("test", () -> "ok");
        assertEquals("ok", result);
    }

    @Test
    void shouldThrowWhenCircuitOpen() {
        ResilientService svc = new ResilientService(1, Duration.ofSeconds(60), 10, 0, Duration.ofMillis(1));
        assertThrows(CircuitBreakerException.class, () -> svc.execute("test", () -> { throw new RuntimeException("fail"); }));
    }
}
