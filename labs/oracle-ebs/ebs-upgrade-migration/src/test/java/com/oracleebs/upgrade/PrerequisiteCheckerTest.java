package com.oracleebs.upgrade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class PrerequisiteCheckerTest {
    private PrerequisiteChecker checker;

    @BeforeEach
    void setUp() {
        checker = PrerequisiteChecker.createDefault();
    }

    @Test
    void testPrerequisiteReportCreated() {
        var report = checker.runChecks("R12.2.12");
        assertNotNull(report);
        assertEquals("R12.2.12", report.getTargetVersion());
    }

    @Test
    void testAllChecksPresent() {
        var report = checker.runChecks("R12.2.12");
        assertEquals(6, report.getChecks().size());
    }

    @Test
    void testReportPasses() {
        var report = checker.runChecks("R12.2.12");
        assertTrue(report.isPassed());
    }

    @Test
    void testCheckNames() {
        var report = checker.runChecks("R12.2.12");
        assertTrue(report.getChecks().stream().anyMatch(c -> c.getCheckName().equals("DATABASE_VERSION")));
        assertTrue(report.getChecks().stream().anyMatch(c -> c.getCheckName().equals("FILESYSTEM_SPACE")));
        assertTrue(report.getChecks().stream().anyMatch(c -> c.getCheckName().equals("TABLESPACE_SIZE")));
    }

    @Test
    void testWarningDoesNotFail() {
        var report = checker.runChecks("R12.2.12");
        assertTrue(report.isPassed());
    }
}
