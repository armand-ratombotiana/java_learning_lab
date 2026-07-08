package com.ai26;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MultiTaskModelTest {
    @Test void testInitialization() {
        MultiTaskModel model = new MultiTaskModel(5, 16, java.util.List.of(1, 2));
        assertNotNull(model);
    }

    @Test void testEncode() {
        MultiTaskModel model = new MultiTaskModel(5, 16, java.util.List.of(1));
        double[] input = {0.5, 0.3, 0.8, 0.2, 0.7};
        double[] shared = model.encode(input);
        assertEquals(16, shared.length);
    }

    @Test void testForward() {
        MultiTaskModel model = new MultiTaskModel(5, 16, java.util.List.of(1, 2));
        double[] input = {0.5, 0.3, 0.8, 0.2, 0.7};
        double[] out = model.forward(0, input);
        assertEquals(1, out.length);
    }

    @Test void testPCGradInitialization() {
        PCGrad pcgrad = new PCGrad(5);
        assertNotNull(pcgrad);
    }

    @Test void testProjectConflicting() {
        PCGrad pcgrad = new PCGrad(3);
        double[] g1 = {1, 0, 0};
        double[] g2 = {-1, 1, 0};
        double[][] projected = pcgrad.projectConflictingGradients(java.util.List.of(g1, g2));
        assertEquals(2, projected.length);
        assertEquals(3, projected[0].length);
    }

    @Test void testPredict() {
        PCGrad pcgrad = new PCGrad(3);
        double[] input = {0.5, 0.3, 0.8};
        double pred = pcgrad.predict(input);
        assertFalse(Double.isNaN(pred));
    }
}
