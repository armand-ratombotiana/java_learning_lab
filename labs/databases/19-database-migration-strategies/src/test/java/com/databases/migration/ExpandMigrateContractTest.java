package com.databases.migration;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExpandMigrateContractTest {
    @Test void shouldExecuteExpandPhase() {
        var emc = ExpandMigrateContract.createDefaultMigration();
        var status = emc.executePhase(ExpandMigrateContract.Phase.EXPAND);
        assertEquals(ExpandMigrateContract.Status.COMPLETED, status);
    }

    @Test void shouldTransitionPhases() {
        var emc = new ExpandMigrateContract();
        assertEquals(ExpandMigrateContract.Phase.EXPAND, emc.getCurrentPhase());
        emc.transitionTo(ExpandMigrateContract.Phase.MIGRATE);
        assertEquals(ExpandMigrateContract.Phase.MIGRATE, emc.getCurrentPhase());
    }

    @Test void shouldFailOnError() {
        var emc = new ExpandMigrateContract();
        emc.addStep("failing", "this step fails", () -> { throw new RuntimeException("fail"); });
        var status = emc.executePhase(ExpandMigrateContract.Phase.EXPAND);
        assertEquals(ExpandMigrateContract.Status.FAILED, status);
    }

    @Test void shouldTrackMetrics() {
        var emc = ExpandMigrateContract.createDefaultMigration();
        emc.executePhase(ExpandMigrateContract.Phase.EXPAND);
        assertFalse(emc.getMetrics().isEmpty());
    }
}
