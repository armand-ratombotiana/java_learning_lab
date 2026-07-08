package com.ai30;

import java.util.*;

public class RLHF {
    private final PolicyModel policy;
    private final RewardModeling.RewardModel rewardModel;
    private final Random random;
    private final int stateDim, actionDim;
    private final double clipEpsilon;

    public RLHF(int stateDim, int actionDim, int hiddenDim, double clipEpsilon) {
        this.stateDim = stateDim;
        this.actionDim = actionDim;
        this.clipEpsilon = clipEpsilon;
        this.random = new Random(42);
        this.policy = new PolicyModel(stateDim, hiddenDim, actionDim);
        this.rewardModel = new RewardModeling.RewardModel(stateDim + actionDim, hiddenDim);
    }

    public double[] getActionDistribution(double[] state) {
        return policy.forward(state);
    }

    public int sampleAction(double[] state) {
        double[] probs = getActionDistribution(state);
        double r = random.nextDouble();
        double cumSum = 0;
        for (int a = 0; a < actionDim; a++) {
            cumSum += probs[a];
            if (r < cumSum) return a;
        }
        return actionDim - 1;
    }

    public double computePPOLoss(double[] state, int action, double oldProb, double advantage) {
        double[] probs = getActionDistribution(state);
        double newProb = Math.log(Math.max(probs[action], 1e-15));
        double ratio = Math.exp(newProb - Math.log(Math.max(oldProb, 1e-15)));
        double clippedRatio = Math.max(Math.min(ratio, 1 + clipEpsilon), 1 - clipEpsilon);
        return -Math.min(ratio * advantage, clippedRatio * advantage);
    }

    public double getReward(double[] state, int action) {
        double[] sa = new double[stateDim + 1];
        System.arraycopy(state, 0, sa, 0, stateDim);
        sa[stateDim] = action;
        return rewardModel.forward(sa)[0];
    }

    public void trainPPO(List<double[]> states, List<Integer> actions, List<Double> oldProbs,
                          List<Double> advantages, double lr) {
        double totalLoss = 0;
        for (int i = 0; i < states.size(); i++) {
            double loss = computePPOLoss(states.get(i), actions.get(i), oldProbs.get(i), advantages.get(i));
            totalLoss += loss;
            double[] probs = policy.forward(states.get(i));
            double logProb = Math.log(Math.max(probs[actions.get(i)], 1e-15));
            double[] grad = new double[actionDim];
            grad[actions.get(i)] = -1.0 / Math.max(probs[actions.get(i)], 1e-15);

            double[] h = new double[policy.hiddenDim];
            for (int j = 0; j < policy.hiddenDim; j++) {
                double s = policy.b1[j];
                for (int k = 0; k < stateDim; k++) s += states.get(i)[k] * policy.w1[k][j];
                h[j] = Math.max(0, s);
            }
            double dSoftmax = grad[actions.get(i)] * probs[actions.get(i)] * (1 - probs[actions.get(i)]);
            for (int j = 0; j < policy.hiddenDim; j++) {
                policy.w2[j][actions.get(i)] -= lr * h[j] * dSoftmax;
                if (h[j] > 0) {
                    for (int k = 0; k < stateDim; k++) {
                        policy.w1[k][j] -= lr * states.get(i)[k] * policy.w2[j][actions.get(i)] * dSoftmax;
                    }
                    policy.b1[j] -= lr * policy.w2[j][actions.get(i)] * dSoftmax;
                }
            }
            policy.b2[actions.get(i)] -= lr * dSoftmax;
        }
        System.out.printf("PPO Step: Avg Loss = %.4f%n", totalLoss / states.size());
    }

    static class PolicyModel {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenDim, outputDim;

        PolicyModel(int id, int hd, int od) {
            inputDim = id; hiddenDim = hd; outputDim = od;
            Random r = new Random(42);
            w1 = new double[id][hd]; b1 = new double[hd];
            w2 = new double[hd][od]; b2 = new double[od];
            for (int i = 0; i < id; i++) for (int j = 0; j < hd; j++) w1[i][j] = r.nextGaussian() * 0.1;
            for (int j = 0; j < hd; j++) for (int o = 0; o < od; o++) w2[j][o] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] h = new double[hiddenDim];
            for (int j = 0; j < hiddenDim; j++) {
                double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j];
                h[j] = Math.max(0, s);
            }
            double[] logits = new double[outputDim];
            for (int o = 0; o < outputDim; o++) {
                double s = b2[o]; for (int j = 0; j < hiddenDim; j++) s += h[j] * w2[j][o];
                logits[o] = s;
            }
            double maxLogit = Arrays.stream(logits).max().orElse(0);
            double sumExp = 0;
            for (int o = 0; o < outputDim; o++) sumExp += Math.exp(logits[o] - maxLogit);
            double[] probs = new double[outputDim];
            for (int o = 0; o < outputDim; o++) probs[o] = Math.exp(logits[o] - maxLogit) / sumExp;
            return probs;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== RLHF (PPO) Demo ===");
        Random rng = new Random(42);
        RLHF rlhf = new RLHF(4, 3, 16, 0.2);

        List<double[]> states = new ArrayList<>();
        List<Integer> actions = new ArrayList<>();
        List<Double> oldProbs = new ArrayList<>();
        List<Double> advantages = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            double[] state = new double[4];
            for (int j = 0; j < 4; j++) state[j] = rng.nextDouble();
            states.add(state);

            double[] probs = rlhf.getActionDistribution(state);
            int action = rlhf.sampleAction(state);
            actions.add(action);
            oldProbs.add(probs[action]);

            double reward = rlhf.getReward(state, action);
            advantages.add(reward - 0.5);
        }

        rlhf.trainPPO(states, actions, oldProbs, advantages, 0.01);

        double[] testState = {0.5, 0.3, 0.8, 0.2};
        double[] testProbs = rlhf.getActionDistribution(testState);
        System.out.print("Action distribution: ");
        for (double p : testProbs) System.out.printf("%.4f ", p);
        System.out.println();
        int bestAction = rlhf.sampleAction(testState);
        System.out.printf("Sampled action: %d%n", bestAction);
    }
}
