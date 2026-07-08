package com.ai28;

import java.util.*;

public class IntegratedGradients {
    private final Model model;
    private final int steps;

    public IntegratedGradients(Model model, int steps) {
        this.model = model;
        this.steps = steps;
    }

    public double[] explain(double[] input, double[] baseline) {
        int n = input.length;
        double[] integratedGrad = new double[n];
        double[] scaledInput = new double[n];

        for (int i = 0; i < n; i++) scaledInput[i] = baseline[i];

        for (int s = 1; s <= steps; s++) {
            for (int i = 0; i < n; i++) {
                scaledInput[i] = baseline[i] + (double) s / steps * (input[i] - baseline[i]);
            }
            double[] grads = computeGradients(scaledInput);
            for (int i = 0; i < n; i++) {
                integratedGrad[i] += grads[i] * (input[i] - baseline[i]) / steps;
            }
        }
        return integratedGrad;
    }

    public double[] computeGradients(double[] input) {
        int n = input.length;
        double[] grads = new double[n];
        double eps = 1e-4;
        double basePred = model.predict(input);
        for (int i = 0; i < n; i++) {
            double[] inputPlus = Arrays.copyOf(input, n);
            inputPlus[i] += eps;
            double predPlus = model.predict(inputPlus);
            grads[i] = (predPlus - basePred) / eps;
        }
        return grads;
    }

    public interface Model {
        double predict(double[] input);
    }

    public static class SimpleNN implements Model {
        private final double[][] w1, w2;
        private final double[] b1, b2;
        private final int hiddenSize;

        public SimpleNN(int inputDim, int hiddenSize) {
            this.hiddenSize = hiddenSize;
            Random r = new Random(42);
            w1 = new double[inputDim][hiddenSize]; b1 = new double[hiddenSize];
            w2 = new double[hiddenSize][1]; b2 = new double[1];
            for (int i = 0; i < inputDim; i++) for (int h = 0; h < hiddenSize; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hiddenSize; h++) w2[h][0] = r.nextGaussian() * 0.1;
        }

        public double predict(double[] input) {
            double[] h = new double[hiddenSize];
            for (int j = 0; j < hiddenSize; j++) {
                double s = b1[j]; for (int i = 0; i < input.length; i++) s += input[i] * w1[i][j];
                h[j] = Math.max(0, s);
            }
            double s = b2[0]; for (int j = 0; j < hiddenSize; j++) s += h[j] * w2[j][0];
            return 1.0 / (1.0 + Math.exp(-s));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Integrated Gradients Demo ===");
        SimpleNN model = new SimpleNN(4, 16);
        IntegratedGradients ig = new IntegratedGradients(model, 50);
        double[] input = {1.0, 0.5, 0.3, 0.8};
        double[] baseline = {0, 0, 0, 0};
        double[] attributions = ig.explain(input, baseline);
        System.out.println("Feature attributions (Integrated Gradients):");
        double pred = model.predict(input);
        System.out.printf("Prediction: %.4f%n", pred);
        for (int i = 0; i < 4; i++) {
            System.out.printf("Feature %d: %+.6f (value=%.2f)%n", i, attributions[i], input[i]);
        }
        double sumAttr = Arrays.stream(attributions).sum();
        double basePred = model.predict(baseline);
        System.out.printf("Sum of attributions: %.4f (pred - baseline_pred = %.4f - %.4f = %.4f)%n",
                sumAttr, pred, basePred, pred - basePred);
    }
}
