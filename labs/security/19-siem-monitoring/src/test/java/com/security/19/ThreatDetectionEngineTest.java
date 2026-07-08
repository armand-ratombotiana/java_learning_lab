package com.security19;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

class ThreatDetectionEngineTest {
    @Test
    void testBruteForceDetection() {
        ThreatDetectionEngine engine = new ThreatDetectionEngine();
        for (int i = 0; i < 6; i++) {
            var event = new ThreatDetectionEngine.SecurityEvent(
                "LOGIN_FAILURE", "10.0.0.1", "admin", Instant.now(), "attempt " + i);
            var alerts = engine.processEvent(event);
            if (i >= 5) assertFalse(alerts.isEmpty());
        }
    }

    @Test
    void testPrivilegeEscalationAlert() {
        ThreatDetectionEngine engine = new ThreatDetectionEngine();
        var event = new ThreatDetectionEngine.SecurityEvent(
            "PRIVILEGE_ESCALATION", "10.0.0.1", "user1", Instant.now(), "sudo su");
        var alerts = engine.processEvent(event);
        assertFalse(alerts.isEmpty());
        assertEquals("PRIV_ESCALATION", alerts.get(0).ruleName);
    }
}
