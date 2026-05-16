# Backpropagation - CODE DEEP DIVE

## Java Implementations

### 1. Chain Rule Implementation

```java
package com.ml.nn.backprop;

public class ChainRule {

    public static double computeGradient(double dUpper, double dMiddle, double dLower) {
        return dUpper * dMiddle * dLower;
    }

    public static double[] vectorChainRule(double[] upperGrad, double[][] weightMatrix) {
        double[] lowerGrad = new double[weightMatrix[0].length];

        for (int j = 0; j < lowerGrad.length; j++) {
            double sum = 0;
            for (int i = 0; i < upperGrad.length; i++) {
                sum += upperGrad[i] * weightMatrix[i][j];
            }
            lowerGrad[j] = sum;
        }

        return lowerGrad;
    }

    public static double[][] matrixChainRule(double[][] upperGrad, double[][] weightMatrix) {
        int rows = upperGrad.length;
        int cols = weightMatrix[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 0;
                for (int k = 0; k < upperGrad[0].length; k++) {
                    result[i][j] += upperGrad[i][k] * weightMatrix[k][j];
                }
            }
        }

        return result;
    }
}
```

### 2. Gradient Computation

```java
package com.ml.nn.backprop;

import java.util.ArrayList;
import java.util.List;

public class GradientComputation {

    public static class LayerGrads {
        public double[][] weightGrad;
        public double[] biasGrad;
        public double[] activationGrad;

        public LayerGrads(int outSize, int inSize) {
            this.weightGrad = new double[outSize][inSize];
            this.biasGrad = new double[outSize];
            this.activationGrad = new double[inSize];
        }
    }

    public static LayerGrads computeGradients(
            double[] preActivation,
            double[] activation,
            double[] upstreamGrad,
            double[][] weights,
            String activationType) {

        int outSize = weights.length;
        int inSize = weights[0].length;
        LayerGrads grads = new LayerGrads(outSize, inSize);

        double[] activationDerivative = computeActivationDerivative(
            preActivation, activationType);

        double[] delta = new double[outSize];
        for (int i = 0; i < outSize; i++) {
            delta[i] = upstreamGrad[i] * activationDerivative[i];
        }

        for (int i = 0; i < outSize; i++) {
            for (int j = 0; j < inSize; j++) {
                grads.weightGrad[i][j] = delta[i] * activation[j];
            }
            grads.biasGrad[i] = delta[i];
        }

        for (int j = 0; j < inSize; j++) {
            double sum = 0;
            for (int i = 0; i < outSize; i++) {
                sum += delta[i] * weights[i][j];
            }
            grads.activationGrad[j] = sum;
        }

        return grads;
    }

    private static double[] computeActivationDerivative(
            double[] preActivation, String type) {

        double[] derivative = new double[preActivation.length];

        switch (type.toUpperCase()) {
            case "SIGMOID":
                for (int i = 0; i < preActivation.length; i++) {
                    derivative[i] = preActivation[i] * (1 - preActivation[i]);
                }
                break;
            case "TANH":
                for (int i = 0; i < preActivation.length; i++) {
                    derivative[i] = 1 - preActivation[i] * preActivation[i];
                }
                break;
            case "RELU":
                for (int i = 0; i < preActivation.length; i++) {
                    derivative[i] = preActivation[i] > 0 ? 1 : 0;
                }
                break;
            case "SOFTMAX":
                for (int i = 0; i < preActivation.length; i++) {
                    derivative[i] = 1;
                }
                break;
        }

        return derivative;
    }
}
```

### 3. Complete Backpropagation Engine

```java
package com.ml.nn.backprop;

import java.util.*;

public class NeuralNetwork {
    private int[] layerSizes;
    private List<double[][]> weights;
    private List<double[]> biases;
    private List<double[]> preActivations;
    private List<double[]> activations;
    private double learningRate;
    private String lossType;

    public NeuralNetwork(int inputSize, int... hiddenSizes) {
        this.layerSizes = new int[hiddenSizes.length + 2];
        layerSizes[0] = inputSize;
        for (int i = 0; i < hiddenSizes.length; i++) {
            layerSizes[i + 1] = hiddenSizes[i];
        }
        layerSizes[layerSizes.length - 1] = 10;
        this.learningRate = 0.01;
        this.preActivations = new ArrayList<>();
        this.activations = new ArrayList<>();

        initializeWeights();
    }

    private void initializeWeights() {
        weights = new ArrayList<>();
        biases = new ArrayList<>();
        Random random = new Random(42);

        for (int l = 0; l < layerSizes.length - 1; l++) {
            int nIn = layerSizes[l];
            int nOut = layerSizes[l + 1];
            double scale = Math.sqrt(2.0 / nIn);

            double[][] w = new double[nOut][nIn];
            for (int i = 0; i < nOut; i++) {
                for (int j = 0; j < nIn; j++) {
                    w[i][j] = random.nextGaussian() * scale;
                }
            }
            weights.add(w);

            double[] b = new double[nOut];
            Arrays.fill(b, 0.0);
            biases.add(b);
        }
    }

    public void forward(double[] input) {
        preActivations.clear();
        activations.clear();
        activations.add(input);

        double[] current = input;

        for (int l = 0; l < weights.size(); l++) {
            double[] z = new double[weights.get(l).length];
            for (int i = 0; i < z.length; i++) {
                z[i] = biases.get(l)[i];
                for (int j = 0; j < current.length; j++) {
                    z[i] += weights.get(l)[i][j] * current[j];
                }
            }
            preActivations.add(z);

            if (l == weights.size() - 1) {
                current = softmax(z);
            } else {
                current = relu(z);
            }
            activations.add(current);
        }
    }

    private double[] relu(double[] x) {
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.max(0, x[i]);
        }
        return result;
    }

    private double[] softmax(double[] x) {
        double max = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }
        double sum = 0;
        double[] exp = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }
        for (int i = 0; i < x.length; i++) {
            exp[i] /= sum;
        }
        return exp;
    }

    public double train(double[] input, int targetLabel) {
        forward(input);

        int numLayers = weights.size();
        double[] lossGradient = computeOutputGradient(targetLabel);

        for (int l = numLayers - 1; l >= 0; l--) {
            boolean isOutputLayer = (l == numLayers - 1);
            String actType = isOutputLayer ? "SOFTMAX" : "RELU";

            double[] delta = computeDelta(
                preActivations.get(l),
                activations.get(l + 1),
                lossGradient,
                actType
            );

            updateWeights(l, delta, activations.get(l));
            lossGradient = computeGradientWrtInput(l, delta);
        }

        return computeLoss(activations.get(activations.size() - 1), targetLabel);
    }

    private double[] computeOutputGradient(int targetLabel) {
        double[] gradient = activations.get(activations.size() - 1).clone();
        gradient[targetLabel] -= 1.0;
        return gradient;
    }

    private double[] computeDelta(double[] preAct, double[] act, double[] upstreamGrad, String type) {
        double[] derivative = new double[preAct.length];

        if (type.equals("RELU")) {
            for (int i = 0; i < preAct.length; i++) {
                derivative[i] = preAct[i] > 0 ? 1 : 0;
            }
        } else if (type.equals("SOFTMAX") || type.equals("SIGMOID")) {
            for (int i = 0; i < preAct.length; i++) {
                derivative[i] = act[i] * (1 - act[i]);
            }
        } else if (type.equals("TANH")) {
            for (int i = 0; i < preAct.length; i++) {
                derivative[i] = 1 - act[i] * act[i];
            }
        }

        double[] delta = new double[preAct.length];
        for (int i = 0; i < delta.length; i++) {
            delta[i] = upstreamGrad[i] * derivative[i];
        }

        return delta;
    }

    private void updateWeights(int layer, double[] delta, double[] prevActivation) {
        for (int i = 0; i < weights.get(layer).length; i++) {
            for (int j = 0; j < weights.get(layer)[0].length; j++) {
                double grad = delta[i] * prevActivation[j];
                weights.get(layer)[i][j] -= learningRate * grad;
            }
            biases.get(layer)[i] -= learningRate * delta[i];
        }
    }

    private double[] computeGradientWrtInput(int layer, double[] delta) {
        double[] grad = new double[weights.get(layer)[0].length];

        for (int j = 0; j < grad.length; j++) {
            double sum = 0;
            for (int i = 0; i < delta.length; i++) {
                sum += delta[i] * weights.get(layer)[i][j];
            }
            grad[j] = sum;
        }

        return grad;
    }

    private double computeLoss(double[] prediction, int targetLabel) {
        return -Math.log(prediction[targetLabel] + 1e-10);
    }

    public int predict(double[] input) {
        forward(input);
        double[] output = activations.get(activations.size() - 1);

        int maxIdx = 0;
        for (int i = 1; i < output.length; i++) {
            if (output[i] > output[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }
}
```

### 4. Training Loop with Mini-Batches

```java
package com.ml.nn.backprop;

import java.util.*;

public class TrainingLoop {

    public static class TrainingConfig {
        public int epochs;
        public int batchSize;
        public double learningRate;
        public double momentum;
        public double weightDecay;

        public TrainingConfig() {
            this.epochs = 100;
            this.batchSize = 32;
            this.learningRate = 0.01;
            this.momentum = 0.9;
            this.weightDecay = 0.0001;
        }
    }

    private NeuralNetwork network;
    private TrainingConfig config;
    private List<double[][]> velocityWeights;
    private List<double[]> velocityBiases;

    public TrainingLoop(NeuralNetwork network, TrainingConfig config) {
        this.network = network;
        this.config = config;
        initializeMomentum();
    }

    private void initializeMomentum() {
        velocityWeights = new ArrayList<>();
        velocityBiases = new ArrayList<>();

        for (int l = 0; l < network.getWeights().size(); l++) {
            double[][] vW = new double[network.getWeights().get(l).length][network.getWeights().get(l)[0].length];
            double[] vB = new double[network.getBiases().get(l).length];
            velocityWeights.add(vW);
            velocityBiases.add(vB);
        }
    }

    public TrainingResult train(double[][] trainingData, int[] labels) {
        int n = trainingData.length;
        int nBatches = (int) Math.ceil((double) n / config.batchSize);

        List<Double> epochLosses = new ArrayList<>();

        for (int epoch = 0; epoch < config.epochs; epoch++) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < n; i++) indices.add(i);
            Collections.shuffle(indices);

            double epochLoss = 0;

            for (int batch = 0; batch < nBatches; batch++) {
                int start = batch * config.batchSize;
                int end = Math.min(start + config.batchSize, n);

                double batchLoss = 0;
                for (int idx = start; idx < end; idx++) {
                    double loss = network.train(trainingData[indices.get(idx)], labels[indices.get(idx)]);
                    batchLoss += loss;
                }

                epochLoss += batchLoss / (end - start);
            }

            epochLosses.add(epochLoss / nBatches);

            if (epoch % 10 == 0) {
                double accuracy = evaluate(trainingData, labels);
                System.out.printf("Epoch %d: Loss=%.4f, Accuracy=%.2f%%%n",
                    epoch, epochLosses.get(epoch), accuracy * 100);
            }
        }

        return new TrainingResult(epochLosses);
    }

    private double evaluate(double[][] data, int[] labels) {
        int correct = 0;
        for (int i = 0; i < data.length; i++) {
            if (network.predict(data[i]) == labels[i]) correct++;
        }
        return (double) correct / data.length;
    }

    public static class TrainingResult {
        private List<Double> losses;
        private double finalAccuracy;

        public TrainingResult(List<Double> losses) {
            this.losses = losses;
        }

        public List<Double> getLosses() { return losses; }
    }
}
```

### 5. Gradient Checking

```java
package com.ml.nn.backprop;

import java.util.Arrays;

public class GradientChecking {

    public static boolean checkGradients(NeuralNetwork network,
                                         double[] input,
                                         int targetLabel,
                                         double epsilon) {

        network.forward(input);
        network.train(input, targetLabel);

        List<double[][]> analyticalWeights = network.getWeightGradients();
        List<double[]> analyticalBiases = network.getBiasGradients();

        double[][] numericalW = computeNumericalWeights(network, input, targetLabel, epsilon);
        double[] numericalB = computeNumericalBias(network, input, targetLabel, epsilon);

        double maxRelErrorW = 0;
        double maxRelErrorB = 0;

        for (int l = 0; l < network.getWeights().size(); l++) {
            for (int i = 0; i < numericalW.length; i++) {
                for (int j = 0; j < numericalW[0].length; j++) {
                    double analytical = analyticalWeights.get(l)[i][j];
                    double numerical = numericalW[i][j];
                    double relError = Math.abs(analytical - numerical) /
                        (Math.abs(analytical) + Math.abs(numerical) + 1e-8);
                    maxRelErrorW = Math.max(maxRelErrorW, relError);
                }
            }
        }

        for (int l = 0; l < network.getBiases().size(); l++) {
            for (int i = 0; i < numericalB.length; i++) {
                double analytical = analyticalBiases.get(l)[i];
                double numerical = numericalB[i];
                double relError = Math.abs(analytical - numerical) /
                    (Math.abs(analytical) + Math.abs(numerical) + 1e-8);
                maxRelErrorB = Math.max(maxRelErrorB, relError);
            }
        }

        boolean passed = maxRelErrorW < 1e-5 && maxRelErrorB < 1e-5;
        System.out.printf("Weight gradient error: %.2e%n", maxRelErrorW);
        System.out.printf("Bias gradient error: %.2e%n", maxRelErrorB);

        return passed;
    }

    private static double[][] computeNumericalWeights(NeuralNetwork network,
                                                       double[] input,
                                                       int targetLabel,
                                                       double epsilon) {
        List<double[][]> weights = network.getWeights();
        int layer = weights.size() - 1;
        double[][] gradients = new double[weights.get(layer).length][weights.get(layer)[0].length];

        for (int i = 0; i < gradients.length; i++) {
            for (int j = 0; j < gradients[0].length; j++) {
                double original = weights.get(layer)[i][j];

                weights.get(layer)[i][j] = original + epsilon;
                double lossPlus = computeLoss(network, input, targetLabel);

                weights.get(layer)[i][j] = original - epsilon;
                double lossMinus = computeLoss(network, input, targetLabel);

                weights.get(layer)[i][j] = original;
                gradients[i][j] = (lossPlus - lossMinus) / (2 * epsilon);
            }
        }

        return gradients;
    }

    private static double[] computeNumericalBias(NeuralNetwork network,
                                                  double[] input,
                                                  int targetLabel,
                                                  double epsilon) {
        List<double[]> biases = network.getBiases();
        int layer = biases.size() - 1;
        double[] gradients = new double[biases.get(layer).length];

        for (int i = 0; i < gradients.length; i++) {
            double original = biases.get(layer)[i];

            biases.get(layer)[i] = original + epsilon;
            double lossPlus = computeLoss(network, input, targetLabel);

            biases.get(layer)[i] = original - epsilon;
            double lossMinus = computeLoss(network, input, targetLabel);

            biases.get(layer)[i] = original;
            gradients[i] = (lossPlus - lossMinus) / (2 * epsilon);
        }

        return gradients;
    }

    private static double computeLoss(NeuralNetwork network, double[] input, int target) {
        network.forward(input);
        double[] output = network.getActivations().get(network.getActivations().size() - 1);
        return -Math.log(output[target] + 1e-10);
    }
}
```