package com.capstone.vectordb;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CosineSimilarityTest {
    @Test void testIdentical() {
        float[] v = {1, 2, 3};
        assertEquals(1.0, CosineSimilarity.compute(v, v), 0.001);
    }

    @Test void testOrthogonal() {
        assertEquals(0.0, CosineSimilarity.compute(new float[]{1, 0}, new float[]{0, 1}), 0.001);
    }

    @Test void testOpposite() {
        float sim = CosineSimilarity.compute(new float[]{1, 0}, new float[]{-1, 0});
        assertEquals(-1.0, sim, 0.001);
    }

    @Test void testNormalize() {
        float[] v = {3, 4};
        float[] n = CosineSimilarity.normalize(v);
        assertEquals(1.0, Math.sqrt(n[0]*n[0] + n[1]*n[1]), 0.001);
    }

    @Test void testCentroid() {
        float[][] vectors = {{1, 0}, {0, 1}};
        float[] c = CosineSimilarity.centroid(vectors);
        assertArrayEquals(new float[]{0.5f, 0.5f}, c, 0.001f);
    }

    @Test void testDimensionMismatch() {
        assertThrows(IllegalArgumentException.class,
            () -> CosineSimilarity.compute(new float[]{1}, new float[]{1, 2}));
    }
}
