package com.learning.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ActuatorSolutionTest {

    private ActuatorSolution solution;

    @BeforeEach
    void setUp() {
        solution = new ActuatorSolution();
    }

    @Test
    void testCreateHealthStatus() {
        Map<String, Object> details = Map.of("database", "ok", "cache", "ok");
        Health health = solution.createHealthStatus("UP", details);
        assertEquals(Health.Status.UP, health.getStatus());
    }

    @Test
    void testCreateDownHealth() {
        Health health = solution.createDownHealth();
        assertEquals(Health.Status.DOWN, health.getStatus());
    }

    @Test
    void testGetMetrics() {
        var result = solution.getMetrics("test.metric", () -> 42.0);
        assertEquals("test.metric", result.get("name"));
    }

    @Test
    void testCustomEndpointReadOperation() {
        ActuatorSolution.CustomEndpoint endpoint = new ActuatorSolution.CustomEndpoint();
        Map<String, String> data = endpoint.getData();
        assertEquals("UP", data.get("status"));
    }

    @Test
    void testDatabaseHealthIndicator() {
        ActuatorSolution.DatabaseHealthIndicator indicator = new ActuatorSolution.DatabaseHealthIndicator();
        Health health = indicator.health();
        assertEquals(Health.Status.UP, health.getStatus());
    }
}