package com.ai21;

import java.util.*;

public class QLearning {
    private final double[][] qTable;
    private final double learningRate;
    private final double discountFactor;
    private final double epsilon;
    private final Random random;
    private final int stateCount;
    private final int actionCount;

    public QLearning(int stateCount, int actionCount, double learningRate, double discountFactor, double epsilon) {
        this.stateCount = stateCount;
        this.actionCount = actionCount;
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
        this.random = new Random(42);
        this.qTable = new double[stateCount][actionCount];
    }

    public int selectAction(int state) {
        if (random.nextDouble() < epsilon) {
            return random.nextInt(actionCount);
        }
        return greedyAction(state);
    }

    public int greedyAction(int state) {
        int bestAction = 0;
        double bestValue = qTable[state][0];
        for (int a = 1; a < actionCount; a++) {
            if (qTable[state][a] > bestValue) {
                bestValue = qTable[state][a];
                bestAction = a;
            }
        }
        return bestAction;
    }

    public void update(int state, int action, double reward, int nextState) {
        double maxNextQ = qTable[nextState][greedyAction(nextState)];
        double tdTarget = reward + discountFactor * maxNextQ;
        double tdError = tdTarget - qTable[state][action];
        qTable[state][action] += learningRate * tdError;
    }

    public void train(Environment env, int episodes) {
        for (int ep = 0; ep < episodes; ep++) {
            int state = env.reset();
            boolean done = false;
            double totalReward = 0;
            while (!done) {
                int action = selectAction(state);
                var result = env.step(action);
                int nextState = result.state();
                double reward = result.reward();
                done = result.done();
                update(state, action, reward, nextState);
                state = nextState;
                totalReward += reward;
            }
            if (ep % 100 == 0) {
                System.out.printf("Episode %d: Total Reward = %.2f%n", ep, totalReward);
            }
        }
    }

    public double[][] getQTable() { return qTable; }

    public static void main(String[] args) {
        System.out.println("=== Q-Learning Demo ===");
        GridWorld env = new GridWorld(5, 5);
        QLearning agent = new QLearning(25, 4, 0.1, 0.95, 0.1);
        agent.train(env, 1000);

        System.out.println("Q-Table for state 0 (top-left corner):");
        double[] q0 = agent.getQTable()[0];
        System.out.printf("UP=%.2f DOWN=%.2f LEFT=%.2f RIGHT=%.2f%n", q0[0], q0[1], q0[2], q0[3]);
        System.out.println("Q-Table for goal state (bottom-right):");
        double[] q24 = agent.getQTable()[24];
        System.out.printf("UP=%.2f DOWN=%.2f LEFT=%.2f RIGHT=%.2f%n", q24[0], q24[1], q24[2], q24[3]);
    }

    public record StepResult(int state, double reward, boolean done) {}

    public interface Environment {
        int reset();
        StepResult step(int action);
    }

    public static class GridWorld implements Environment {
        private final int rows, cols;
        private int agentRow, agentCol;

        public GridWorld(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }

        public int reset() {
            agentRow = 0;
            agentCol = 0;
            return encodeState();
        }

        public StepResult step(int action) {
            int newRow = agentRow;
            int newCol = agentCol;
            switch (action) {
                case 0 -> newRow = Math.max(0, agentRow - 1);
                case 1 -> newRow = Math.min(rows - 1, agentRow + 1);
                case 2 -> newCol = Math.max(0, agentCol - 1);
                case 3 -> newCol = Math.min(cols - 1, agentCol + 1);
            }
            agentRow = newRow;
            agentCol = newCol;
            boolean done = (agentRow == rows - 1 && agentCol == cols - 1);
            double reward = done ? 10.0 : -0.1;
            return new StepResult(encodeState(), reward, done);
        }

        private int encodeState() { return agentRow * cols + agentCol; }
    }
}
