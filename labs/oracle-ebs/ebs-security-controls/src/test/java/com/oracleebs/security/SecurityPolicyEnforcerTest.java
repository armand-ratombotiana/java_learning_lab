package com.oracleebs.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SecurityPolicyEnforcerTest {
    private SecurityPolicyEnforcer enforcer;

    @BeforeEach
    void setUp() {
        enforcer = SecurityPolicyEnforcer.createDefault();
    }

    @Test
    void testFunctionAccessGranted() {
        assertEquals(SecurityPolicyEnforcer.AccessResult.GRANTED,
            enforcer.checkFunctionAccess("GL_POST", "GL_RESPONSIBILITY"));
    }

    @Test
    void testFunctionAccessDenied() {
        assertEquals(SecurityPolicyEnforcer.AccessResult.DENIED_FUNCTION,
            enforcer.checkFunctionAccess("NONEXISTENT", "ANY_RESP"));
    }

    @Test
    void testFunctionAccessDisabledForResponsibility() {
        assertEquals(SecurityPolicyEnforcer.AccessResult.DENIED_FUNCTION,
            enforcer.checkFunctionAccess("ADMIN_PANEL", "AP_RESPONSIBILITY"));
    }

    @Test
    void testDataAccessGranted() {
        assertEquals(SecurityPolicyEnforcer.AccessResult.GRANTED,
            enforcer.checkDataAccess("GL_BALANCES", "101", "SELECT"));
    }

    @Test
    void testDataAccessDeniedForOrg() {
        assertEquals(SecurityPolicyEnforcer.AccessResult.DENIED_DATA,
            enforcer.checkDataAccess("GL_BALANCES", "999", "SELECT"));
    }

    @Test
    void testDataAccessDeniedForOperation() {
        assertEquals(SecurityPolicyEnforcer.AccessResult.DENIED_DATA,
            enforcer.checkDataAccess("GL_BALANCES", "101", "DELETE"));
    }

    @Test
    void testPrivilegedUser() {
        assertTrue(enforcer.isPrivileged("SYSADMIN"));
        assertFalse(enforcer.isPrivileged("REGULAR_USER"));
    }
}
