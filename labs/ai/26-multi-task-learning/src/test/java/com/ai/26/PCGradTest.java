package com.ai26;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PCGradTest {
    @Test void testNonConflictingGradients() {
        PCGrad pcgrad = new PCGrad(3);
        double[] g1 = {1, 0, 0};
        double[] g2 = {1, 1, 0};
        double[][] projected = pcgrad.projectConflictingGradients(java.util.List.of(g1, g2));
        assertArrayEquals(g1, projected[0], 1e-10);
    }

    @Test void testConflictingGradients() {
        PCGrad pcgrad = new PCGrad(3);
        double[] g1 = {1, 0, 0};
        double[] g2 = {-1, 1, 0};
        double[][] projected = pcgrad.projectConflictingGradients(java.util.List.of(g1, g2));
        double dot = 0;
        for (int i = 0; i < 3; i++) dot += projected[0][i] * g2[i];
        assertTrue(dot >= -1e-10);
    }

    @Test void testApplyGradients() {
        PCGrad pcgrad = new PCGrad(2);
        double[] g1 = {0.5, -0.3};
        double[] g2 = {-0.2, 0.1};
        double before = pcgrad.predict(new double[]{1, 1});
        double[][] projected = pcgrad.projectConflictingGradients(java.util.List.of(g1, g2));
        pcgrad.applyGradients(projected, 0.1);
        double after = pcgrad.predict(new double[]{1, 1});
        assertNotEquals(before, after, 1e-10);
    }
}
