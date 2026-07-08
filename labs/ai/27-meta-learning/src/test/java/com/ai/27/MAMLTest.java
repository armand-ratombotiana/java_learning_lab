package com.ai27;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MAMLTest {
    @Test void testMAMLInitialization() {
        MAML maml = new MAML(1, 16, 1, 0.01, 0.001, 3);
        assertNotNull(maml);
    }

    @Test void testPredict() {
        MAML maml = new MAML(1, 16, 1, 0.01, 0.001, 3);
        double result = maml.predict(new double[]{1.5});
        assertFalse(Double.isNaN(result));
    }

    @Test void testPrototypicalNetworksInit() {
        PrototypicalNetworks pn = new PrototypicalNetworks(5, 4, 16);
        assertNotNull(pn);
    }

    @Test void testEncode() {
        PrototypicalNetworks pn = new PrototypicalNetworks(5, 4, 16);
        double[] input = {0.5, 0.3, 0.8, 0.2, 0.7};
        double[] emb = pn.encode(input);
        assertEquals(4, emb.length);
    }

    @Test void testComputePrototypes() {
        PrototypicalNetworks pn = new PrototypicalNetworks(5, 4, 16);
        java.util.List<double[]> support = new java.util.ArrayList<>();
        int[] labels = new int[4];
        for (int c = 0; c < 2; c++) {
            for (int i = 0; i < 2; i++) {
                double[] pt = new double[5];
                for (int j = 0; j < 5; j++) pt[j] = c == 0 ? 1.0 : 0.0;
                support.add(pt);
                labels[support.size() - 1] = c;
            }
        }
        double[][] prots = pn.computePrototypes(support, labels, 2);
        assertEquals(2, prots.length);
        assertEquals(4, prots[0].length);
    }

    @Test void testClassification() {
        PrototypicalNetworks pn = new PrototypicalNetworks(2, 2, 8);
        double[][] prots = {{1, 1}, {0, 0}};
        double class0 = pn.classify(new double[]{0.9, 0.9}, prots);
        assertEquals(0, class0, 1e-10);
        double class1 = pn.classify(new double[]{0.1, 0.1}, prots);
        assertEquals(1, class1, 1e-10);
    }
}
