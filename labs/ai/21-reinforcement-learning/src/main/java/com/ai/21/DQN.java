package com.ai21;

import java.util.*;

public class DQN {
    private final NeuralNetwork qNetwork;
    private final NeuralNetwork targetNetwork;
    private final Deque<Experience> replayBuffer;
    private final int batchSize;
    private final int replayCapacity;
    private final double learningRate;
    private final double discountFactor;
    private double epsilon;
    private final double epsilonMin;
    private final double epsilonDecay;
    private final Random random;

    public DQN(int stateDim, int actionCount, int hiddenSize, double learningRate, double discountFactor,
               double epsilon, double epsilonMin, double epsilonDecay, int replayCapacity, int batchSize) {
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
        this.epsilonMin = epsilonMin;
        this.epsilonDecay = epsilonDecay;
        this.replayCapacity = replayCapacity;
        this.batchSize = batchSize;
        this.random = new Random(42);
        this.replayBuffer = new ArrayDeque<>(replayCapacity);
        this.qNetwork = new NeuralNetwork(stateDim, hiddenSize, actionCount);
        this.targetNetwork = qNetwork.copy();
    }

    public int selectAction(double[] state) {
        if (random.nextDouble() < epsilon) {
            return random.nextInt(qNetwork.outputSize);
        }
        double[] qValues = qNetwork.forward(state);
        int best = 0;
        for (int i = 1; i < qValues.length; i++) {
            if (qValues[i] > qValues[best]) best = i;
        }
        return best;
    }

    public void storeExperience(double[] state, int action, double reward, double[] nextState, boolean done) {
        if (replayBuffer.size() >= replayCapacity) replayBuffer.pollFirst();
        replayBuffer.addLast(new Experience(state, action, reward, nextState, done));
    }

    public void train() {
        if (replayBuffer.size() < batchSize) return;
        List<Experience> batch = new ArrayList<>(replayBuffer);
        Collections.shuffle(batch, random);
        batch = batch.subList(0, batchSize);

        double[][] states = new double[batchSize][];
        double[][] targets = new double[batchSize][];
        for (int i = 0; i < batchSize; i++) {
            Experience exp = batch.get(i);
            states[i] = exp.state;
            double[] qValues = qNetwork.forward(exp.state);
            targets[i] = Arrays.copyOf(qValues, qValues.length);
            if (exp.done) {
                targets[i][exp.action] = exp.reward;
            } else {
                double[] nextQ = targetNetwork.forward(exp.nextState);
                double maxNextQ = Arrays.stream(nextQ).max().orElse(0);
                targets[i][exp.action] = exp.reward + discountFactor * maxNextQ;
            }
        }
        qNetwork.train(states, targets, learningRate);
        if (epsilon > epsilonMin) epsilon *= epsilonDecay;
    }

    public void updateTargetNetwork() {
        double[][] targetW = targetNetwork.getWeights();
        double[][] qW = qNetwork.getWeights();
        for (int i = 0; i < targetW.length; i++) {
            System.arraycopy(qW[i], 0, targetW[i], 0, targetW[i].length);
        }
        double[] targetB = targetNetwork.getBiases();
        double[] qB = qNetwork.getBiases();
        System.arraycopy(qB, 0, targetB, 0, targetB.length);
    }

    public double getEpsilon() { return epsilon; }

    public record Experience(double[] state, int action, double reward, double[] nextState, boolean done) {}

    static class NeuralNetwork {
        final int inputSize, hiddenSize, outputSize;
        double[][] w1, w2;
        double[] b1, b2;

        NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
            this.inputSize = inputSize;
            this.hiddenSize = hiddenSize;
            this.outputSize = outputSize;
            Random rng = new Random(42);
            w1 = new double[inputSize][hiddenSize];
            b1 = new double[hiddenSize];
            w2 = new double[hiddenSize][outputSize];
            b2 = new double[outputSize];
            for (int i = 0; i < inputSize; i++)
                for (int h = 0; h < hiddenSize; h++)
                    w1[i][h] = rng.nextDouble() * 0.1 - 0.05;
            for (int h = 0; h < hiddenSize; h++)
                for (int o = 0; o < outputSize; o++)
                    w2[h][o] = rng.nextDouble() * 0.1 - 0.05;
        }

        double[] forward(double[] input) {
            double[] hidden = new double[hiddenSize];
            for (int h = 0; h < hiddenSize; h++) {
                double sum = b1[h];
                for (int i = 0; i < inputSize; i++) sum += input[i] * w1[i][h];
                hidden[h] = Math.max(0, sum);
            }
            double[] output = new double[outputSize];
            for (int o = 0; o < outputSize; o++) {
                double sum = b2[o];
                for (int h = 0; h < hiddenSize; h++) sum += hidden[h] * w2[h][o];
                output[o] = sum;
            }
            return output;
        }

        void train(double[][] inputs, double[][] targets, double lr) {
            int n = inputs.length;
            double[][] dw1 = new double[inputSize][hiddenSize];
            double[] db1 = new double[hiddenSize];
            double[][] dw2 = new double[hiddenSize][outputSize];
            double[] db2 = new double[outputSize];

            for (int s = 0; s < n; s++) {
                double[] hidden = new double[hiddenSize];
                for (int h = 0; h < hiddenSize; h++) {
                    double sum = b1[h];
                    for (int i = 0; i < inputSize; i++) sum += inputs[s][i] * w1[i][h];
                    hidden[h] = Math.max(0, sum);
                }
                double[] output = new double[outputSize];
                for (int o = 0; o < outputSize; o++) {
                    double sum = b2[o];
                    for (int h = 0; h < hiddenSize; h++) sum += hidden[h] * w2[h][o];
                    output[o] = sum;
                }
                for (int o = 0; o < outputSize; o++) {
                    double dOut = 2 * (output[o] - targets[s][o]) / n;
                    db2[o] += dOut;
                    for (int h = 0; h < hiddenSize; h++) dw2[h][o] += hidden[h] * dOut;
                }
                for (int h = 0; h < hiddenSize; h++) {
                    double dHidden = 0;
                    for (int o = 0; o < outputSize; o++) dHidden += w2[h][o] * (2 * (output[o] - targets[s][o]) / n);
                    if (hidden[h] > 0) {
                        db1[h] += dHidden;
                        for (int i = 0; i < inputSize; i++) dw1[i][h] += inputs[s][i] * dHidden;
                    }
                }
            }
            for (int i = 0; i < inputSize; i++)
                for (int h = 0; h < hiddenSize; h++) w1[i][h] -= lr * dw1[i][h];
            for (int h = 0; h < hiddenSize; h++) b1[h] -= lr * db1[h];
            for (int h = 0; h < hiddenSize; h++)
                for (int o = 0; o < outputSize; o++) w2[h][o] -= lr * dw2[h][o];
            for (int o = 0; o < outputSize; o++) b2[o] -= lr * db2[o];
        }

        NeuralNetwork copy() {
            NeuralNetwork nn = new NeuralNetwork(inputSize, hiddenSize, outputSize);
            for (int i = 0; i < inputSize; i++) System.arraycopy(w1[i], 0, nn.w1[i], 0, hiddenSize);
            System.arraycopy(b1, 0, nn.b1, 0, hiddenSize);
            for (int h = 0; h < hiddenSize; h++) System.arraycopy(w2[h], 0, nn.w2[h], 0, outputSize);
            System.arraycopy(b2, 0, nn.b2, 0, outputSize);
            return nn;
        }

        double[][] getWeights() { return w2; }
        double[] getBiases() { return b2; }
    }

    public static void main(String[] args) {
        System.out.println("=== DQN Demo ===");
        DQN dqn = new DQN(2, 4, 32, 0.001, 0.95, 1.0, 0.01, 0.995, 10000, 32);
        System.out.println("DQN initialized with 2D state space, 4 actions, 32 hidden units");
        double[] testState = {0.5, 0.5};
        int action = dqn.selectAction(testState);
        System.out.printf("Test state [0.5,0.5] -> selected action %d (epsilon=%.3f)%n", action, dqn.getEpsilon());
        System.out.println("DQN training loop ready for environment interaction.");
    }
}
