package com.security14;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContainerSecurityScannerTest {
    @Test
    void testScanDockerfileRejectsRoot() {
        ContainerSecurityScanner scanner = new ContainerSecurityScanner();
        String df = "FROM eclipse-temurin:21-jre\nUSER root\nCOPY target/app.jar /app/\n";
        var result = scanner.scanDockerfile(df);
        assertFalse(result.isPassed());
    }

    @Test
    void testCreateSecurityContext() {
        ContainerSecurityScanner scanner = new ContainerSecurityScanner();
        var ctx = scanner.createSecurityContext();
        assertTrue(ctx.readOnlyRootFs);
        assertEquals(10001, ctx.runAsUser);
        assertFalse(ctx.allowPrivilegeEscalation);
    }

    @Test
    void testScanDockerfileGoodPractice() {
        ContainerSecurityScanner scanner = new ContainerSecurityScanner();
        String df = "FROM eclipse-temurin:21-jre\nUSER 10001\nCOPY target/app.jar /app/\n";
        var result = scanner.scanDockerfile(df);
        assertTrue(result.isPassed());
    }
}
