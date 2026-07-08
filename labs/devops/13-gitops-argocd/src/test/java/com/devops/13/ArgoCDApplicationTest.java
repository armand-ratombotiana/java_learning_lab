package com.devops.thirteen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class ArgoCDApplicationTest {
    private ArgoCDApplication app;

    @BeforeEach
    void setUp() {
        app = new ArgoCDApplication("test-app", "https://github.com/org/repo.git",
            "k8s/overlays/prod", "main", "test-ns");
    }

    @Test
    @DisplayName("Should create application with correct properties")
    void testApplicationCreation() {
        assertEquals("test-app", app.getName());
        assertEquals("https://github.com/org/repo.git", app.getRepoUrl());
        assertEquals("k8s/overlays/prod", app.getPath());
        assertEquals("main", app.getTargetRevision());
        assertEquals("test-ns", app.getNamespace());
        assertEquals(ArgoCDApplication.SyncStatus.UNKNOWN, app.getSyncStatus());
        assertEquals(ArgoCDApplication.HealthStatus.UNKNOWN, app.getHealthStatus());
    }

    @Test
    @DisplayName("Should set and retrieve parameters")
    void testParameters() {
        app.setParameter("replicaCount", "3");
        app.setParameter("image.tag", "latest");
        assertEquals("3", app.getParameter("replicaCount"));
        assertEquals("latest", app.getParameter("image.tag"));
    }

    @Test
    @DisplayName("Should sync successfully and update status")
    void testSync() {
        boolean result = app.sync();
        assertTrue(result);
        assertEquals(ArgoCDApplication.SyncStatus.SYNCED, app.getSyncStatus());
        assertEquals(ArgoCDApplication.HealthStatus.HEALTHY, app.getHealthStatus());
    }

    @Test
    @DisplayName("Should return complete status map")
    void testStatus() {
        app.sync();
        Map<String, Object> status = app.status();
        assertEquals("test-app", status.get("name"));
        assertEquals(ArgoCDApplication.SyncStatus.SYNCED, status.get("syncStatus"));
        assertEquals(ArgoCDApplication.HealthStatus.HEALTHY, status.get("healthStatus"));
        assertNotNull(status.get("revision"));
        assertNotNull(status.get("syncStartedAt"));
    }

    @Test
    @DisplayName("Should rollback to previous revision")
    void testRollback() {
        app.sync();
        boolean result = app.rollback("abc123");
        assertTrue(result);
        assertEquals(ArgoCDApplication.SyncStatus.SYNCED, app.getSyncStatus());
    }

    @Test
    @DisplayName("Should refresh application state")
    void testRefresh() {
        app.sync();
        boolean result = app.refresh();
        assertTrue(result);
        assertEquals(ArgoCDApplication.SyncStatus.SYNCED, app.getSyncStatus());
    }

    @Nested
    @DisplayName("When syncing multiple times")
    class MultiSyncTests {
        @Test
        @DisplayName("Should handle sequential syncs")
        void testSequentialSyncs() {
            for (int i = 0; i < 5; i++) {
                assertTrue(app.sync());
                assertEquals(ArgoCDApplication.SyncStatus.SYNCED, app.getSyncStatus());
            }
        }
    }
}
