package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ModelRegistryTest {
    private ModelRegistry registry;

    @BeforeEach
    void setUp() { registry = new ModelRegistry(); }

    @Test void testRegister() {
        var mv = registry.register("model-a", "/models/model-a/v1", Map.of("accuracy", 0.95));
        assertEquals("STAGING", mv.status().name());
        assertEquals("model-a", mv.modelName());
    }

    @Test void testPromoteToProduction() {
        var mv = registry.register("model-a", "/models/model-a/v1", Map.of());
        var promoted = registry.promoteToProduction(mv.modelId());
        assertEquals("PRODUCTION", promoted.status().name());
    }

    @Test void testGetLatestProduction() {
        registry.register("m1", "/m1/v1", Map.of());
        var mv = registry.register("m1", "/m1/v2", Map.of());
        registry.promoteToProduction(mv.modelId());
        var latest = registry.getLatestProductionModel("m1");
        assertTrue(latest.isPresent());
        assertEquals("v2", latest.get().version());
    }

    @Test void testArchive() {
        var mv = registry.register("m1", "/m1/v1", Map.of());
        var archived = registry.archiveModel(mv.modelId());
        assertEquals("ARCHIVED", archived.status().name());
    }

    @Test void testGetModelHistory() {
        registry.register("m1", "/m1/v1", Map.of());
        registry.register("m1", "/m1/v2", Map.of());
        assertEquals(2, registry.getModelHistory("m1").size());
    }
}
