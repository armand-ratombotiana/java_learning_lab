package com.ai30;

import java.util.*;

public class RewardModeling {
    private final RewardModel rewardModel;
    private final Random random;
    private final double learningRate;

    public RewardModeling(int inputDim, int hiddenDim, double learningRate) {
        this.learningRate = learningRate;
        this.random = new Random(42);
        this.rewardModel = new RewardModel(inputDim, hiddenDim);
    }

    public double predictReward(double[] response) {
        return rewardModel.forward(response)[0];
    }

    public void trainPairwise(double[][] preferred, double[][] dispreferred, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int i = 0; i < preferred.length; i++) {
                double rPref = rewardModel.forward(preferred[i])[0];
                double rDis = rewardModel.forward(dispreferred[i])[0];
                double diff = rPref - rDis;
                double loss = -Math.log(1.0 / (1.0 + Math.exp(-diff)) + 1e-15);
                totalLoss += loss;

                double dLoss = 1.0 / (1.0 + Math.exp(diff));

                double[] hPref = new double[rewardModel.hiddenDim];
                for (int j = 0; j < rewardModel.hiddenDim; j++) {
                    double s = rewardModel.b1[j];
                    for (int k = 0; k < rewardModel.inputDim; k++) s += preferred[i][k] * rewardModel.w1[k][j];
                    hPref[j] = Math.max(0, s);
                }
                double[] hDis = new double[rewardModel.hiddenDim];
                for (int j = 0; j < rewardModel.hiddenDim; j++) {
                    double s = rewardModel.b1[j];
                    for (int k = 0; k < rewardModel.inputDim; k++) s += dispreferred[i][k] * rewardModel.w1[k][j];
                    hDis[j] = Math.max(0, s);
                }

                for (int j = 0; j < rewardModel.hiddenDim; j++) {
                    double gPref = rewardModel.w2[j][0] * dLoss;
                    double gDis = -rewardModel.w2[j][0] * dLoss;
                    if (hPref[j] > 0) {
                        for (int k = 0; k < rewardModel.inputDim; k++)
                            rewardModel.w1[k][j] -= learningRate * preferred[i][k] * gPref;
                        rewardModel.b1[j] -= learningRate * gPref;
                    }
                    if (hDis[j] > 0) {
                        for (int k = 0; k < rewardModel.inputDim; k++)
                            rewardModel.w1[k][j] -= learningRate * dispreferred[i][k] * gDis;
                        rewardModel.b1[j] -= learningRate * gDis;
                    }
                    rewardModel.w2[j][0] -= learningRate * (hPref[j] - hDis[j]) * dLoss;
                }
                rewardModel.b2[0] -= learningRate * dLoss;
            }
            if (epoch % 100 == 0) System.out.printf("Epoch %d: Avg Loss = %.4f%n", epoch, totalLoss / preferred.length);
        }
    }

    static class RewardModel {
        double[][] w1; double[] b1;
        double[][] w2; double[] b2;
        final int inputDim, hiddenDim;

        RewardModel(int id, int hd) {
            inputDim = id; hiddenDim = hd;
            Random r = new Random(42);
            w1 = new double[id][hd]; b1 = new double[hd];
            w2 = new double[hd][1]; b2 = new double[1];
            for (int i = 0; i < id; i++) for (int j = 0; j < hd; j++) w1[i][j] = r.nextGaussian() * 0.1;
            for (int j = 0; j < hd; j++) w2[j][0] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] h = new double[hiddenDim];
            for (int j = 0; j < hiddenDim; j++) {
                double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j];
                h[j] = Math.max(0, s);
            }
            double s = b2[0]; for (int j = 0; j < hiddenDim; j++) s += h[j] * w2[j][0];
            return new double[]{s};
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Reward Modeling Demo ===");
        Random rng = new Random(42);
        int dim = 5;
        double[][] preferred = new double[50][dim];
        double[][] dispreferred = new double[50][dim];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < dim; j++) {
                preferred[i][j] = 0.5 + rng.nextDouble() * 0.5;
                dispreferred[i][j] = rng.nextDouble() * 0.3;
            }
        }

        RewardModeling rm = new RewardModeling(dim, 16, 0.01);
        rm.trainPairwise(preferred, dispreferred, 500);

        double[] goodResponse = {0.9, 0.8, 0.7, 0.8, 0.9};
        double[] badResponse = {0.1, 0.2, 0.1, 0.2, 0.1};
        double rewardGood = rm.predictReward(goodResponse);
        double rewardBad = rm.predictReward(badResponse);
        System.out.printf("Reward for good response: %.4f%n", rewardGood);
        System.out.printf("Reward for bad response: %.4f%n", rewardBad);
        System.out.printf("Preference margin: %.4f%n", rewardGood - rewardBad);
    }
}
