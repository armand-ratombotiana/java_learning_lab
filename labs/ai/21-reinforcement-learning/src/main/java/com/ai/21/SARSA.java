package com.ai21;

import java.util.*;

public class SARSA {
    private final double[][] qTable;
    private final double learningRate, discountFactor, epsilon;
    private final Random random;
    private final int stateCount, actionCount;

    public SARSA(int stateCount, int actionCount, double learningRate, double discountFactor, double epsilon) {
        this.stateCount = stateCount;
        this.actionCount = actionCount;
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
        this.random = new Random(42);
        this.qTable = new double[stateCount][actionCount];
    }

    public int selectAction(int state) {
        if (random.nextDouble() < epsilon) return random.nextInt(actionCount);
        int best = 0;
        for (int a = 1; a < actionCount; a++) if (qTable[state][a] > qTable[state][best]) best = a;
        return best;
    }

    public void update(int state, int action, double reward, int nextState, int nextAction) {
        double tdTarget = reward + discountFactor * qTable[nextState][nextAction];
        double tdError = tdTarget - qTable[state][action];
        qTable[state][action] += learningRate * tdError;
    }

    public void train(QLearning.Environment env, int episodes) {
        for (int ep = 0; ep < episodes; ep++) {
            int state = env.reset();
            int action = selectAction(state);
            boolean done = false;
            double totalReward = 0;
            while (!done) {
                var result = env.step(action);
                int nextState = result.state();
                double reward = result.reward();
                done = result.done();
                int nextAction = done ? 0 : selectAction(nextState);
                update(state, action, reward, nextState, nextAction);
                state = nextState;
                action = nextAction;
                totalReward += reward;
            }
            if (ep % 200 == 0) System.out.printf("SARSA Episode %d: Total Reward = %.2f%n", ep, totalReward);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== SARSA Demo ===");
        QLearning.GridWorld env = new QLearning.GridWorld(4, 4);
        SARSA agent = new SARSA(16, 4, 0.1, 0.95, 0.1);
        agent.train(env, 1000);
        System.out.println("SARSA training completed on 4x4 grid.");
    }
}
