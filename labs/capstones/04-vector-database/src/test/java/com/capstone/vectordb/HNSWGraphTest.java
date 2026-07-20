package com.capstone.vectordb;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class HNSWGraphTest {
    private HNSWGraph graph;
    private VectorIndex index;

    @BeforeEach
    void setUp() {
        index = new VectorIndex();
        graph = new HNSWGraph(index, 8, 50);
    }

    @Test void testInsert() {
        graph.insert("1", new float[]{1, 0, 0});
        graph.insert("2", new float[]{0, 1, 0});
        graph.insert("3", new float[]{0, 0, 1});
        var results = graph.search(new float[]{1, 0, 0}, 3, 50);
        assertEquals(3, results.size());
    }

    @Test void testSearchReturnsClosest() {
        graph.insert("target", new float[]{0.99f, 0.01f});
        graph.insert("far", new float[]{-0.99f, -0.01f});
        var results = graph.search(new float[]{1, 0}, 1, 50);
        assertEquals("target", results.get(0).id());
        assertTrue(results.get(0).similarity() > 0);
    }

    @Test void testEmptyGraph() {
        var results = graph.search(new float[]{1, 0, 0}, 5, 50);
        assertTrue(results.isEmpty());
    }

    @Test void testLargeGraph() {
        for (int i = 0; i < 100; i++) {
            float[] vec = {(float) Math.cos(i * 0.1), (float) Math.sin(i * 0.1)};
            graph.insert("v" + i, vec);
        }
        var results = graph.search(new float[]{1, 0}, 5, 100);
        assertEquals(5, results.size());
    }
}
