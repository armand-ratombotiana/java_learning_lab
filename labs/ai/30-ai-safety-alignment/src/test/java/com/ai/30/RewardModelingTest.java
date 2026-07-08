package com.ai30;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RewardModelingTest {
    @Test void testInitialization() {
        RewardModeling rm = new RewardModeling(5, 16, 0.01);
        assertNotNull(rm);
    }

    @Test void testPredictReward() {
        RewardModeling rm = new RewardModeling(5, 16, 0.01);
        double[] response = {0.5, 0.3, 0.8, 0.2, 0.7};
        double reward = rm.predictReward(response);
        assertFalse(Double.isNaN(reward));
    }

    @Test void testTrainPairwise() {
        var rng = new java.util.Random(42);
        int dim = 3;
        RewardModeling rm = new RewardModeling(dim, 8, 0.01);
        double[][] preferred = new double[10][dim];
        double[][] dispreferred = new double[10][dim];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < dim; j++) {
                preferred[i][j] = 0.6 + rng.nextDouble() * 0.4;
                dispreferred[i][j] = rng.nextDouble() * 0.3;
            }
        }
        rm.trainPairwise(preferred, dispreferred, 100);
        double rGood = rm.predictReward(new double[]{0.8, 0.7, 0.9});
        double rBad = rm.predictReward(new double[]{0.1, 0.2, 0.1});
        assertTrue(rGood > rBad);
    }

    @Test void testRLHFInitialization() {
        RLHF rlhf = new RLHF(4, 3, 16, 0.2);
        assertNotNull(rlhf);
    }

    @Test void testActionDistribution() {
        RLHF rlhf = new RLHF(4, 3, 16, 0.2);
        double[] dist = rlhf.getActionDistribution(new double[]{0.5, 0.3, 0.8, 0.2});
        assertEquals(3, dist.length);
        double sum = 0;
        for (double p : dist) sum += p;
        assertEquals(1.0, sum, 1e-5);
    }

    @Test void testSampleAction() {
        RLHF rlhf = new RLHF(4, 3, 16, 0.2);
        int action = rlhf.sampleAction(new double[]{0.5, 0.3, 0.8, 0.2});
        assertTrue(action >= 0 && action < 3);
    }
}
