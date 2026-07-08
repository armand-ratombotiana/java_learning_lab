package com.ai26;

import java.util.*;

public class PCGrad {
    private final double[][] sharedParams;
    private final Random random;

    public PCGrad(int paramDim) {
        this.random = new Random(42);
        this.sharedParams = new double[paramDim][1];
        for (int i = 0; i < paramDim; i++) sharedParams[i][0] = random.nextGaussian() * 0.1;
    }

    public double[][] projectConflictingGradients(List<double[]> taskGradients) {
        int numTasks = taskGradients.size();
        int numParams = taskGradients.get(0).length;
        double[][] projected = new double[numTasks][numParams];

        for (int i = 0; i < numTasks; i++) {
            projected[i] = Arrays.copyOf(taskGradients.get(i), numParams);
        }

        for (int i = 0; i < numTasks; i++) {
            for (int j = 0; j < numTasks; j++) {
                if (i == j) continue;
                double dot = 0;
                for (int k = 0; k < numParams; k++) {
                    dot += projected[i][k] * taskGradients.get(j)[k];
                }
                if (dot < 0) {
                    double projNorm = 0;
                    for (int k = 0; k < numParams; k++) projNorm += taskGradients.get(j)[k] * taskGradients.get(j)[k];
                    if (projNorm > 0) {
                        double scale = dot / projNorm;
                        for (int k = 0; k < numParams; k++) {
                            projected[i][k] -= scale * taskGradients.get(j)[k];
                        }
                    }
                }
            }
        }
        return projected;
    }

    public void applyGradients(double[][] projectedGrads, double lr) {
        int numTasks = projectedGrads.length;
        int numParams = sharedParams.length;
        for (int p = 0; p < numParams; p++) {
            double gradSum = 0;
            for (int t = 0; t < numTasks; t++) {
                gradSum += projectedGrads[t][p];
            }
            sharedParams[p][0] -= lr * gradSum / numTasks;
        }
    }

    public double computeLoss(double[] input, double[] target) {
        double pred = 0;
        for (int i = 0; i < input.length; i++) pred += sharedParams[i][0] * input[i];
        double diff = pred - target[0];
        return diff * diff;
    }

    public double predict(double[] input) {
        double pred = 0;
        for (int i = 0; i < input.length; i++) pred += sharedParams[i][0] * input[i];
        return pred;
    }

    public static void main(String[] args) {
        System.out.println("=== PCGrad Demo ===");
        Random rng = new Random(42);
        int paramDim = 5;
        PCGrad pcgrad = new PCGrad(paramDim);

        double[][] inputs = new double[20][paramDim];
        double[] target1 = new double[20];
        double[] target2 = new double[20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < paramDim; j++) inputs[i][j] = rng.nextDouble();
            target1[i] = inputs[i][0] * 2 + inputs[i][1] * 3 + 0.1 * rng.nextGaussian();
            target2[i] = inputs[i][2] * (-1) + inputs[i][3] * 0.5 + 0.1 * rng.nextGaussian();
        }

        for (int ep = 0; ep < 200; ep++) {
            double totalLoss = 0;
            for (int i = 0; i < 20; i++) {
                double[] grad1 = new double[paramDim];
                double[] grad2 = new double[paramDim];
                double pred1 = pcgrad.predict(inputs[i]);
                double pred2 = pcgrad.predict(inputs[i]);
                for (int j = 0; j < paramDim; j++) {
                    grad1[j] = 2 * (pred1 - target1[i]) * inputs[i][j];
                    grad2[j] = 2 * (pred2 - target2[i]) * inputs[i][j];
                }
                List<double[]> taskGrads = Arrays.asList(grad1, grad2);
                double[][] projected = pcgrad.projectConflictingGradients(taskGrads);
                pcgrad.applyGradients(projected, 0.01);
                totalLoss += pcgrad.computeLoss(inputs[i], new double[]{target1[i]});
                totalLoss += pcgrad.computeLoss(inputs[i], new double[]{target2[i]});
            }
            if (ep % 50 == 0) System.out.printf("Epoch %d: Avg Loss = %.4f%n", ep, totalLoss / 40);
        }

        double[] testIn = {0.5, 0.3, 0.7, 0.2, 0.4};
        System.out.printf("Test pred task1: %.4f%n", pcgrad.predict(testIn));
    }
}
