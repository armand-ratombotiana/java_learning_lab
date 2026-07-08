package com.security15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityTestRunnerTest {
    @Test
    void testFuzzingDetectsSqlInjection() {
        SecurityTestRunner runner = new SecurityTestRunner();
        var findings = runner.runFuzzingTest("/api/login", List.of("' OR '1'='1"));
        assertFalse(findings.isEmpty());
        assertEquals("SQL Injection", findings.get(0).type());
    }

    @Test
    void testFuzzingDetectsXss() {
        SecurityTestRunner runner = new SecurityTestRunner();
        var findings = runner.runFuzzingTest("/api/search", List.of("<script>alert(1)</script>"));
        assertFalse(findings.isEmpty());
        assertEquals("XSS", findings.get(0).type());
    }

    @Test
    void testSecurityHeadersCheck() {
        SecurityTestRunner runner = new SecurityTestRunner();
        var headers = Map.of("X-Content-Type-Options", "nosniff", "X-Frame-Options", "DENY");
        var check = runner.checkSecurityHeaders(headers);
        assertFalse(check.passed);
    }
}
