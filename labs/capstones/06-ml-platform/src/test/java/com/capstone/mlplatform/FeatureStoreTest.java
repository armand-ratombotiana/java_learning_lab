package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeatureStoreTest {
    private FeatureStore store;

    @BeforeEach
    void setUp() { store = new FeatureStore(); }

    @Test void testRegisterGroup() {
        var defs = List.of(new FeatureStore.FeatureDefinition("age", FeatureStore.FeatureType.DOUBLE, "User age", false));
        var group = store.registerFeatureGroup("user_features", defs, "clickhouse");
        assertEquals("user_features", group.name());
    }

    @Test void testIngestAndGetOnline() {
        store.ingestOnline("user_features", "user1", Map.of("age", 30.0, "name", "Alice"));
        var features = store.getOnlineFeatures("user_features", "user1");
        assertTrue(features.isPresent());
        assertEquals(30.0, features.get().get("age"));
    }

    @Test void testBatchGet() {
        store.ingestOnline("f1", "u1", Map.of("a", 1));
        store.ingestOnline("f2", "u1", Map.of("b", 2));
        var combined = store.getOnlineFeaturesBatch("u1", List.of("f1", "f2"));
        assertEquals(2, combined.size());
    }

    @Test void testHistoricalFeatures() {
        store.ingestOnline("f1", "u1", Map.of("x", 10));
        store.ingestOnline("f1", "u2", Map.of("x", 20));
        assertEquals(2, store.getHistoricalFeatures("f1").size());
    }

    @Test void testMissingFeature() {
        assertTrue(store.getOnlineFeatures("nonexistent", "user").isEmpty());
    }
}
