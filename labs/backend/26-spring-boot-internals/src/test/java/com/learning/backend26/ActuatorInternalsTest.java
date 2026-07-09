package com.learning.backend26;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActuatorInternalsTest {

    private ActuatorInternals.CustomHealthIndicator healthIndicator;
    private ActuatorInternals.CustomMetrics metrics;
    private MeterRegistry registry;

    @BeforeEach
    void setUp() {
        healthIndicator = new ActuatorInternals.CustomHealthIndicator();
        registry = new SimpleMeterRegistry();
        metrics = new ActuatorInternals.CustomMetrics(registry);
    }

    @Test
    void healthIndicatorUpByDefault() {
        var health = healthIndicator.health();
        assertTrue(health.getStatus().equals(
                org.springframework.boot.actuate.health.Status.UP));
    }

    @Test
    void healthIndicatorDownWhenSet() {
        healthIndicator.setHealthy(false);
        var health = healthIndicator.health();
        assertTrue(health.getStatus().equals(
                org.springframework.boot.actuate.health.Status.DOWN));
    }

    @Test
    void customMetricsRecordsRequests() {
        metrics.recordRequest();
        Counter counter = registry.get("custom.requests.total")
                .tag("source", "actuator-internals")
                .counter();
        assertEquals(1.0, counter.count(), 0.001);
    }

    @Test
    void customMetricsTracksSessions() {
        assertEquals(0, metrics.getActiveSessions());
        metrics.incrementSessions();
        assertEquals(1, metrics.getActiveSessions());
        metrics.decrementSessions();
        assertEquals(0, metrics.getActiveSessions());
    }

    @Test
    void customEndpointReadOperation() {
        var endpoint = new ActuatorInternals.CustomFeaturesEndpoint();
        var result = endpoint.read();
        assertNotNull(result);
        assertTrue(result.containsKey("features"));
        assertTrue(result.containsKey("enabledCount"));
    }

    @Test
    void customEndpointWriteOperation() {
        var endpoint = new ActuatorInternals.CustomFeaturesEndpoint();
        endpoint.update("feature-test", true);
        var result = endpoint.read();
        @SuppressWarnings("unchecked")
        var features = (java.util.Map<String, Boolean>) result.get("features");
        assertTrue(features.get("feature-test"));
    }

    @Test
    void starterServiceGreets() {
        var service = new StarterCustomizer.CustomStarterService("Hello ", "!");
        assertEquals("Hello World!", service.greet("World"));
    }

    @Test
    void starterHealthIndicatorIsHealthy() {
        var indicator = new StarterCustomizer.CustomStarterHealthIndicator();
        assertTrue(indicator.isHealthy());
    }
}