package com.ai28;

import java.util.*;

public class SHAPExplainer {
    private final Model model;
    private final double[][] backgroundData;
    private final Random random;
    private final int numFeatures;
    private final int numSamples;

    public SHAPExplainer(Model model, double[][] backgroundData, int numSamples) {
        this.model = model;
        this.backgroundData = backgroundData;
        this.numSamples = numSamples;
        this.numFeatures = backgroundData[0].length;
        this.random = new Random(42);
    }

    public double[] explain(double[] instance) {
        double[] shapValues = new double[numFeatures];
        int numBg = backgroundData.length;
        double expectedValue = 0;
        for (double[] bg : backgroundData) expectedValue += model.predict(bg);
        expectedValue /= numBg;

        for (int feat = 0; feat < numFeatures; feat++) {
            double sumDiff = 0;
            for (int s = 0; s < numSamples; s++) {
                double[] withFeature = new double[numFeatures];
                double[] withoutFeature = new double[numFeatures];
                int bgIdx = random.nextInt(numBg);
                for (int f = 0; f < numFeatures; f++) {
                    if (f == feat) {
                        withFeature[f] = instance[f];
                        withoutFeature[f] = backgroundData[bgIdx][f];
                    } else if (random.nextBoolean()) {
                        withFeature[f] = instance[f];
                        withoutFeature[f] = instance[f];
                    } else {
                        withFeature[f] = backgroundData[bgIdx][f];
                        withoutFeature[f] = backgroundData[bgIdx][f];
                    }
                }
                sumDiff += model.predict(withFeature) - model.predict(withoutFeature);
            }
            shapValues[feat] = sumDiff / numSamples;
        }
        return shapValues;
    }

    public interface Model {
        double predict(double[] input);
    }

    public static class LinearModel implements Model {
        private final double[] weights;
        private final double bias;

        public LinearModel(double[] weights, double bias) {
            this.weights = weights;
            this.bias = bias;
        }

        public double predict(double[] input) {
            double sum = bias;
            for (int i = 0; i < weights.length; i++) sum += weights[i] * input[i];
            return 1.0 / (1.0 + Math.exp(-sum));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== SHAP Explainer Demo ===");
        double[] weights = {2.0, -1.5, 0.8, 0.3, -0.5};
        LinearModel model = new LinearModel(weights, 0.1);
        Random rng = new Random(42);
        double[][] background = new double[50][5];
        for (int i = 0; i < 50; i++) for (int j = 0; j < 5; j++) background[i][j] = rng.nextDouble();

        SHAPExplainer explainer = new SHAPExplainer(model, background, 100);
        double[] instance = {0.8, 0.3, 0.6, 0.2, 0.9};
        double[] shapValues = explainer.explain(instance);

        System.out.println("SHAP values for instance [0.8, 0.3, 0.6, 0.2, 0.9]:");
        double pred = model.predict(instance);
        System.out.printf("Prediction: %.4f%n", pred);
        double total = 0;
        for (int i = 0; i < 5; i++) {
            System.out.printf("Feature %d: %+.4f (value=%.2f)%n", i, shapValues[i], instance[i]);
            total += shapValues[i];
        }
        System.out.printf("Sum of SHAP values: %.4f%n", total);
    }
}
