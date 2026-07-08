package com.ai30;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RLHFTest {
    @Test void testPPOLoss() {
        RLHF rlhf = new RLHF(2, 2, 8, 0.2);
        double[] state = {0.5, 0.3};
        double loss = rlhf.computePPOLoss(state, 0, 0.5, 1.0);
        assertFalse(Double.isNaN(loss));
    }

    @Test void testPolicyModelForward() {
        RLHF.PolicyModel pm = new RLHF.PolicyModel(3, 8, 2);
        double[] probs = pm.forward(new double[]{0.5, 0.3, 0.8});
        assertEquals(2, probs.length);
        double sum = probs[0] + probs[1];
        assertEquals(1.0, sum, 1e-5);
    }

    @Test void testTrainPPO() {
        RLHF rlhf = new RLHF(3, 2, 8, 0.2);
        java.util.List<double[]> states = new java.util.ArrayList<>();
        java.util.List<Integer> actions = new java.util.ArrayList<>();
        java.util.List<Double> oldProbs = new java.util.ArrayList<>();
        java.util.List<Double> advantages = new java.util.ArrayList<>();
        var rng = new java.util.Random(42);
        for (int i = 0; i < 5; i++) {
            double[] state = {rng.nextDouble(), rng.nextDouble(), rng.nextDouble()};
            states.add(state);
            double[] probs = rlhf.getActionDistribution(state);
            actions.add(rng.nextInt(2));
            oldProbs.add(probs[actions.get(i)]);
            advantages.add(rng.nextDouble() - 0.5);
        }
        rlhf.trainPPO(states, actions, oldProbs, advantages, 0.01);
        assertTrue(true);
    }

    @Test void testGetReward() {
        RLHF rlhf = new RLHF(2, 2, 8, 0.2);
        double reward = rlhf.getReward(new double[]{0.5, 0.3}, 1);
        assertFalse(Double.isNaN(reward));
    }
}
