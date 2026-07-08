package com.arch.strangleradvanced;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class DbSplitterTest {
    @Test
    void shouldStartInLegacyMode() {
        DbSplitter splitter = new DbSplitter();
        splitter.registerTable("users", d -> {}, d -> {});
        assertEquals(DbSplitter.MigrationState.NOT_STARTED, splitter.getState("users"));
    }

    @Test
    void shouldSupportDualWrite() {
        DbSplitter splitter = new DbSplitter();
        splitter.registerTable("users", d -> {}, d -> {});
        splitter.enableDualWrite("users");
        splitter.write("users", Map.of("name", "Alice"));
        assertEquals(1, splitter.getPendingSync().size());
    }

    @Test
    void shouldCompleteMigration() {
        DbSplitter splitter = new DbSplitter();
        splitter.registerTable("users", d -> {}, d -> {});
        splitter.startMigration("users");
        splitter.verifyAndComplete("users");
        splitter.decommissionLegacy("users");
        assertEquals(DbSplitter.MigrationState.DECOMMISSIONED, splitter.getState("users"));
    }
}

class AsyncMigrationCoordinatorTest {
    @Test
    void shouldTrackMigrationProgress() {
        AsyncMigrationCoordinator coord = new AsyncMigrationCoordinator();
        String jobId = coord.startMigration("test", 100, () -> {});
        assertNotNull(jobId);
        coord.shutdown();
    }
}
