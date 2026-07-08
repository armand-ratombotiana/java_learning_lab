package com.databases.dbtesting;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MigrationTesterTest {
    @Test void shouldExecuteSuccessfulMigration() {
        var tester = new MigrationTester("test-migration");
        tester.addStep(1, "create table", () -> {})
            .addStep(2, "insert data", () -> {})
            .addValidation("table exists", true, () -> {})
            .addValidation("data count correct", false, () -> {});
        var result = tester.execute();
        assertTrue(result.success());
    }

    @Test void shouldFailOnStepError() {
        var tester = new MigrationTester("failing");
        tester.addStep(1, "failing step", () -> { throw new RuntimeException("fail"); })
            .addStep(2, "never reached", () -> {});
        var result = tester.execute();
        assertFalse(result.success());
    }

    @Test void shouldTrackMetrics() {
        var tester = new MigrationTester("metrics-test");
        tester.addStep(1, "step1", () -> { try { Thread.sleep(1); } catch (Exception e) {} });
        tester.execute();
        var metrics = tester.getMetrics();
        assertFalse(metrics.isEmpty());
    }
}
