package com.capstone.vectordb;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class VectorIndexTest {
    private VectorIndex index;

    @BeforeEach
    void setUp() { index = new VectorIndex(); }

    @Test void testInsertAndSearch() {
        index.insert("1", new float[]{1, 0, 0});
        index.insert("2", new float[]{0, 1, 0});
        index.insert("3", new float[]{0, 0, 1});
        var results = index.search(new float[]{1, 0, 0}, 3);
        assertEquals(3, results.size());
        assertEquals("1", results.get(0).id());
        assertTrue(results.get(0).similarity() > 0.9);
    }

    @Test void testCosineSimilarity() {
        float sim = VectorIndex.cosineSimilarity(new float[]{1, 0}, new float[]{0, 1});
        assertEquals(0, sim, 0.001);
    }

    @Test void testL2Distance() {
        float dist = VectorIndex.l2Distance(new float[]{0, 0}, new float[]{3, 4});
        assertEquals(-5.0, dist, 0.001);
    }

    @Test void testInnerProduct() {
        float ip = VectorIndex.innerProduct(new float[]{1, 2, 3}, new float[]{4, 5, 6});
        assertEquals(32, ip, 0.001);
    }

    @Test void testDelete() {
        index.insert("1", new float[]{1, 0});
        index.delete("1");
        assertTrue(index.getVector("1").isEmpty());
    }

    @Test void testFilteredSearch() {
        index.insert("1", new float[]{1, 0}, Map.of("color", "red"));
        index.insert("2", new float[]{0, 1}, Map.of("color", "blue"));
        var results = index.search(new float[]{1, 0}, 10, Map.of("color", "red"));
        assertEquals(1, results.size());
        assertEquals("1", results.get(0).id());
    }

    @Test void testUpdateMetadata() {
        index.insert("1", new float[]{1, 0}, Map.of("color", "red"));
        index.updateMetadata("1", Map.of("color", "blue"));
        var meta = index.getMetadata("1");
        assertTrue(meta.isPresent());
        assertEquals("blue", meta.get().get("color"));
    }

    @Test void testIdenticalVectors() {
        index.insert("1", new float[]{1, 1, 1});
        index.insert("2", new float[]{1, 1, 1});
        var results = index.search(new float[]{1, 1, 1}, 2);
        assertEquals(2, results.size());
        assertTrue(results.get(0).similarity() > 0.99);
    }

    @Test void testNegativeDimensions() {
        index.insert("1", new float[]{-1, -2, -3});
        index.insert("2", new float[]{1, 2, 3});
        var results = index.search(new float[]{-1, -2, -3}, 2);
        assertEquals("1", results.get(0).id());
    }
}
