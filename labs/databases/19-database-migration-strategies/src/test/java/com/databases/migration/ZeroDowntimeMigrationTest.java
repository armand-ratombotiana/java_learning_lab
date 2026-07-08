package com.databases.migration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZeroDowntimeMigrationTest {
    @Test void shouldExecuteSuccessfulMigration() {
        var zdm = new ZeroDowntimeMigration("test");
        zdm.addStep("canary1", () -> {}, 1, true)
           .addStep("step1", () -> {}, 2, false)
           .addStep("step2", () -> {}, 3, false);
        var result = zdm.execute();
        assertTrue(result.success());
    }

    @Test void shouldFailOnCanaryStep() {
        var zdm = new ZeroDowntimeMigration("fail");
        zdm.addStep("canary", () -> { throw new RuntimeException("canary fail"); }, 1, true);
        var result = zdm.execute();
        assertFalse(result.success());
    }

    @Test void shouldBeHealthyInitially() {
        var zdm = new ZeroDowntimeMigration("health");
        assertTrue(zdm.isHealthy());
    }
}
