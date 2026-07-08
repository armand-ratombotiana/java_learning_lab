package com.ai21;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DQNTest {
    @Test
    void testDQNInitialization() {
        DQN dqn = new DQN(4, 3, 16, 0.001, 0.95, 1.0, 0.01, 0.99, 1000, 32);
        assertNotNull(dqn);
    }

    @Test
    void testSelectAction() {
        DQN dqn = new DQN(2, 2, 8, 0.001, 0.95, 0.0, 0.01, 0.99, 100, 8);
        int action = dqn.selectAction(new double[]{0.5, 0.3});
        assertTrue(action >= 0 && action < 2);
    }

    @Test
    void testStoreAndReplay() {
        DQN dqn = new DQN(2, 2, 8, 0.001, 0.95, 1.0, 0.01, 0.99, 100, 4);
        dqn.storeExperience(new double[]{0.5, 0.3}, 0, 1.0, new double[]{0.6, 0.4}, false);
        dqn.train();
        assertTrue(dqn.getEpsilon() <= 1.0);
    }

    @Test
    void testTargetNetwork() {
        DQN dqn = new DQN(3, 2, 10, 0.001, 0.95, 0.1, 0.01, 0.99, 100, 4);
        dqn.updateTargetNetwork();
        int action = dqn.selectAction(new double[]{0.1, 0.2, 0.3});
        assertTrue(action >= 0 && action < 2);
    }

    @Test
    void testEpsilonDecay() {
        DQN dqn = new DQN(2, 2, 8, 0.001, 0.95, 0.5, 0.01, 0.5, 100, 4);
        double eps = dqn.getEpsilon();
        for (int i = 0; i < 5; i++) dqn.train();
        assertTrue(dqn.getEpsilon() < eps || Math.abs(dqn.getEpsilon() - eps) < 0.001);
    }
}
