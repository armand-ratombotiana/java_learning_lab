package com.ai29;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecureAggregationTest {
    @Test void testInitialization() {
        SecureAggregation sa = new SecureAggregation(5, 10);
        assertNotNull(sa);
    }

    @Test void testGenerateMasks() {
        SecureAggregation sa = new SecureAggregation(3, 4);
        double[][] masks = sa.generateMasks();
        assertEquals(3, masks.length);
        assertEquals(4, masks[0].length);
    }

    @Test void testMaskAndAggregate() {
        SecureAggregation sa = new SecureAggregation(3, 4);
        var rng = new java.util.Random(42);
        java.util.List<double[]> gradients = new java.util.ArrayList<>();
        for (int c = 0; c < 3; c++) {
            double[] grad = new double[4];
            for (int p = 0; p < 4; p++) grad[p] = rng.nextGaussian() * 0.1;
            gradients.add(grad);
        }

        double[][] masks = sa.generateMasks();
        java.util.List<double[]> maskedGradients = new java.util.ArrayList<>();
        for (int c = 0; c < 3; c++) {
            maskedGradients.add(sa.maskGradients(gradients.get(c), masks[c], new int[]{c}));
        }

        double[] secureAgg = sa.aggregateSecure(maskedGradients, masks);
        double[] plainAgg = sa.aggregatePlain(gradients);
        boolean verified = sa.verifyAggregation(gradients, secureAgg, plainAgg);
        assertTrue(verified);
    }

    @Test void testAggregationConsistency() {
        SecureAggregation sa = new SecureAggregation(3, 2);
        var rng = new java.util.Random(42);
        java.util.List<double[]> grads = new java.util.ArrayList<>();
        for (int c = 0; c < 3; c++) grads.add(new double[]{c + 1, c + 0.5});
        double[] plain = sa.aggregatePlain(grads);
        assertEquals(2.0, plain[0], 1e-10);
        assertEquals(1.5, plain[1], 1e-10);
    }
}
