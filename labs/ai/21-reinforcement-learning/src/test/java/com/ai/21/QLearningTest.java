package com.ai21;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QLearningTest {
    @Test
    void testQLearningInitialization() {
        QLearning agent = new QLearning(25, 4, 0.1, 0.95, 0.1);
        assertNotNull(agent);
        double[][] qTable = agent.getQTable();
        assertEquals(25, qTable.length);
        assertEquals(4, qTable[0].length);
    }

    @Test
    void testSelectAction() {
        QLearning agent = new QLearning(10, 3, 0.1, 0.95, 0.0);
        int action = agent.selectAction(0);
        assertTrue(action >= 0 && action < 3);
    }

    @Test
    void testGreedyAction() {
        QLearning agent = new QLearning(5, 2, 0.1, 0.95, 0.0);
        int action = agent.greedyAction(0);
        assertTrue(action >= 0 && action < 2);
    }

    @Test
    void testUpdate() {
        QLearning agent = new QLearning(5, 3, 0.1, 0.95, 0.0);
        agent.update(0, 1, 10.0, 2);
        double[][] qTable = agent.getQTable();
        assertTrue(qTable[0][1] != 0);
    }

    @Test
    void testGridWorldReset() {
        QLearning.GridWorld env = new QLearning.GridWorld(4, 4);
        int state = env.reset();
        assertEquals(0, state);
    }

    @Test
    void testGridWorldStep() {
        QLearning.GridWorld env = new QLearning.GridWorld(3, 3);
        env.reset();
        var result = env.step(1);
        assertNotNull(result);
        assertTrue(result.reward() < 0);
    }

    @Test
    void testGridWorldGoal() {
        QLearning.GridWorld env = new QLearning.GridWorld(2, 2);
        env.reset();
        env.step(1);
        env.step(3);
        var result = env.step(1);
        assertTrue(result.done() || result.reward() > 0);
    }

    @Test
    void testTraining() {
        QLearning agent = new QLearning(16, 4, 0.1, 0.95, 0.3);
        QLearning.GridWorld env = new QLearning.GridWorld(4, 4);
        agent.train(env, 100);
        assertNotNull(agent.getQTable());
    }

    @Test
    void testEpsilonGreedy() {
        QLearning agent = new QLearning(5, 2, 0.1, 0.95, 1.0);
        boolean foundRandom = false;
        for (int i = 0; i < 100; i++) {
            if (agent.selectAction(0) != agent.greedyAction(0)) {
                foundRandom = true;
                break;
            }
        }
        assertTrue(foundRandom);
    }
}
