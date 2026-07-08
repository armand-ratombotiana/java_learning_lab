package com.devops.fourteen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class HelmHookManagerTest {
    private HelmHookManager manager;

    @BeforeEach
    void setUp() {
        manager = new HelmHookManager();
    }

    @Test
    @DisplayName("Should add and execute pre-install hooks")
    void testPreInstallHooks() {
        manager.addHook("db-migration", HelmHookManager.HookType.PRE_INSTALL, "batch/v1", "Job", "migrations:1.0", "migrate");
        String yaml = manager.renderPreInstallJob();
        assertTrue(yaml.contains("db-migration"));
        assertTrue(yaml.contains("helm.sh/hook: pre-install"));
        assertTrue(yaml.contains("batch/v1"));
    }

    @Test
    @DisplayName("Should execute hooks without errors")
    void testHookExecution() {
        manager.addHook("check", HelmHookManager.HookType.PRE_INSTALL, "batch/v1", "Job", "curl", "curl localhost");
        manager.addHook("verify", HelmHookManager.HookType.POST_INSTALL, "batch/v1", "Job", "curl", "curl localhost/health");
        assertDoesNotThrow(() -> manager.executePreInstallHooks());
        assertDoesNotThrow(() -> manager.executePostInstallHooks());
    }

    @Test
    @DisplayName("Should handle all hook types")
    void testAllHookTypes() {
        for (var type : HelmHookManager.HookType.values()) {
            manager.addHook("hook-" + type.name(), type, "batch/v1", "Job", "image", "echo");
        }
        assertDoesNotThrow(() -> manager.renderPreInstallJob());
    }
}
