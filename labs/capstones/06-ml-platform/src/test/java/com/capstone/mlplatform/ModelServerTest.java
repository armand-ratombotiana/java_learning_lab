package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ModelServerTest {
    private ModelRegistry registry;
    private ModelServer server;

    @BeforeEach
    void setUp() {
        registry = new ModelRegistry();
        var mv = registry.register("test-model", "/artifacts/test", Map.of("acc", 0.9));
        registry.promoteToProduction(mv.modelId());
        server = new ModelServer(registry);
    }

    @Test void testDeploy() {
        var inst = server.deploy("test-model");
        assertTrue(inst.active());
        assertEquals(9000, inst.port());
    }

    @Test void testPredict() {
        server.deploy("test-model");
        var result = server.predict("test-model", Map.of("feature1", 1.0, "feature2", 2.0));
        assertNotNull(result.prediction());
        assertTrue(result.latencyMs() >= 0);
    }

    @Test void testUndeploy() {
        server.deploy("test-model");
        server.undeploy("test-model");
        assertEquals(0, server.activeCount());
    }

    @Test void testNoDeployment() {
        assertThrows(IllegalStateException.class,
            () -> server.predict("nonexistent", Map.of()));
    }
}
