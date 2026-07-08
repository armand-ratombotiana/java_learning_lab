package com.ai27;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrototypicalNetworksTest {
    @Test void testDistanceMetric() {
        PrototypicalNetworks pn = new PrototypicalNetworks(3, 2, 8);
        double[] q1 = {0.9, 0.9};
        double[] q2 = {0.1, 0.1};
        double[][] prots = {{1, 1}, {0, 0}};
        assertEquals(0, pn.classify(q1, prots), 1e-10);
        assertEquals(1, pn.classify(q2, prots), 1e-10);
    }

    @Test void testTrainEpisode() {
        PrototypicalNetworks pn = new PrototypicalNetworks(3, 2, 8);
        java.util.List<double[]> support = new java.util.ArrayList<>();
        int[] supLabels = new int[4];
        for (int i = 0; i < 4; i++) {
            support.add(new double[]{i < 2 ? 1.0 : 0.0, i < 2 ? 1.0 : 0.0, i < 2 ? 1.0 : 0.0});
            supLabels[i] = i < 2 ? 0 : 1;
        }
        java.util.List<double[]> query = new java.util.ArrayList<>();
        int[] qLabels = {0, 1};
        query.add(new double[]{0.8, 0.8, 0.8});
        query.add(new double[]{0.2, 0.2, 0.2});
        double loss = pn.trainEpisode(support, supLabels, query, qLabels, 2, 0.01);
        assertTrue(loss > 0);
    }
}
