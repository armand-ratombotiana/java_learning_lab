package com.capstone.vectordb;

import org.junit.jupiter.api.*;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VectorStoreTest {
    private VectorStore store;

    @BeforeEach
    void setUp() {
        store = new VectorStore(Path.of(System.getProperty("java.io.tmpdir"), "vectordb_test"));
        store.clear();
    }

    @AfterEach
    void tearDown() { store.clear(); }

    @Test void testInsertAndGet() {
        store.insert("1", new float[]{1, 0, 0});
        var vec = store.get("1");
        assertTrue(vec.isPresent());
        assertEquals(1.0, vec.get()[0], 0.001);
    }

    @Test void testSearch() {
        store.insert("1", new float[]{1, 0, 0});
        store.insert("2", new float[]{0, 1, 0});
        var results = store.search(new float[]{1, 0, 0}, 2);
        assertEquals(2, results.size());
    }

    @Test void testDelete() {
        store.insert("1", new float[]{1, 0});
        store.delete("1");
        assertTrue(store.get("1").isEmpty());
    }

    @Test void testPersistAndLoad() {
        store.insert("1", new float[]{1, 0, 0});
        store.insert("2", new float[]{0, 1, 0}, Map.of("label", "test"));
        store.persist();
        VectorStore loaded = new VectorStore(
            Path.of(System.getProperty("java.io.tmpdir"), "vectordb_test"));
        loaded.load();
        assertEquals(2, loaded.size());
        var meta = loaded.getMetadata("2");
        assertTrue(meta.isPresent());
        assertEquals("test", meta.get().get("label"));
    }

    @Test void testSearchWithFilter() {
        store.insert("1", new float[]{1, 0}, Map.of("type", "a"));
        store.insert("2", new float[]{0, 1}, Map.of("type", "b"));
        var results = store.search(new float[]{1, 0}, 10, Map.of("type", "a"), VectorStore.SearchMode.BRUTE_FORCE);
        assertEquals(1, results.size());
    }
}
