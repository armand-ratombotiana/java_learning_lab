package com.security16;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccessPolicyEngineTest {
    @Test
    void testDenyLowTrust() {
        AccessPolicyEngine engine = new AccessPolicyEngine();
        var request = new AccessPolicyEngine.AccessRequest(
            "alice", "/api/admin", "POST", "unknown", false, false, true);
        var decision = engine.evaluate(request);
        assertEquals(AccessPolicyEngine.Decision.DENY, decision.decision());
    }

    @Test
    void testPermitHighTrust() {
        AccessPolicyEngine engine = new AccessPolicyEngine();
        var request = new AccessPolicyEngine.AccessRequest(
            "bob", "/api/read", "GET", "internal", true, true, false);
        var decision = engine.evaluate(request);
        assertEquals(AccessPolicyEngine.Decision.PERMIT, decision.decision());
    }

    @Test
    void testStepUpForSensitiveResource() {
        AccessPolicyEngine engine = new AccessPolicyEngine();
        var request = new AccessPolicyEngine.AccessRequest(
            "carol", "/api/payments", "POST", "vpn", true, true, true);
        var decision = engine.evaluate(request);
        assertNotNull(decision);
    }
}
