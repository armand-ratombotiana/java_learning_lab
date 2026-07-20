package com.capstone.rag;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class VectorStoreTest {
    private EmbeddingInterface.MockEmbedding embed;
    private VectorStore store;

    @BeforeEach
    void setUp() {
        embed = new EmbeddingInterface.MockEmbedding(8);
        store = new VectorStore(embed);
    }

    @Test void testAddAndSearch() {
        store.addText("1", "apple fruit red", Map.of("category", "fruit"));
        store.addText("2", "car vehicle engine", Map.of("category", "vehicle"));
        var results = store.search("fruit", 5);
        assertEquals(2, results.size());
    }

    @Test void testFilteredSearch() {
        store.addText("1", "apple fruit", Map.of("type", "food"));
        store.addText("2", "car vehicle", Map.of("type", "machine"));
        var results = store.search("apple", 5, Map.of("type", "food"));
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).id());
    }

    @Test void testDelete() {
        store.addText("1", "test", Map.of());
        assertTrue(store.delete("1"));
        assertEquals(0, store.size());
    }

    @Test void testGetById() {
        store.addText("1", "hello world", Map.of());
        var entry = store.get("1");
        assertTrue(entry.isPresent());
        assertEquals("hello world", entry.get().text());
    }

    @Test void testBatchAdd() {
        store.addTexts(List.of("1", "2"), List.of("text one", "text two"), List.of(Map.of("k", "v"), Map.of()));
        assertEquals(2, store.size());
    }
}
