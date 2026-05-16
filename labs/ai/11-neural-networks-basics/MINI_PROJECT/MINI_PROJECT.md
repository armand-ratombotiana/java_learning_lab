# Neural Networks Basics - MINI PROJECT

## Project: Handwritten Digit Classifier (MLP)

Build a multi-layer perceptron to classify MNIST-style handwritten digits.

### Implementation

```java
package com.ml.nn;

import java.io.*;
import java.util.*;

public class DigitClassifier {
    private int[] layerSizes;
    private List<double[][]> weights;
    private List<double[]> biases;
    private double learningRate;
    private Random random;

    public DigitClassifier(int inputSize, int... hiddenSizes) {
        this.layerSizes = new int[hiddenSizes.length + 2];
        this.layerSizes[0] = inputSize;
        for (int i = 0; i < hiddenSizes.length; i++) {
            this.layerSizes[i + 1] = hiddenSizes[i];
        }
        this.layerSizes[layerSizes.length - 1] = 10;
        this.random = new Random(42);
        this.learningRate = 0.1;
        initializeWeights();
    }

    private void initializeWeights() {
        weights = new ArrayList<>();
        biases = new ArrayList<>();

        for (int i = 0; i < layerSizes.length - 1; i++) {
            int nIn = layerSizes[i];
            int nOut = layerSizes[i + 1];
            double scale = Math.sqrt(2.0 / (nIn + nOut));

            double[][] w = new double[nOut][nIn];
            for (int r = 0; r < nOut; r++) {
                for (int c = 0; c < nIn; c++) {
                    w[r][c] = random.nextGaussian() * scale;
                }
            }
            weights.add(w);

            double[] b = new double[nOut];
            for (int j = 0; j < nOut; j++) {
                b[j] = random.nextGaussian() * 0.01;
            }
            biases.add(b);
        }
    }

    public int predict(double[] input) {
        double[] current = input;

        for (int l = 0; l < weights.size(); l++) {
            current = forwardLayer(current, weights.get(l), biases.get(l));
            if (l < weights.size() - 1) {
                current = relu(current);
            } else {
                current = softmax(current);
            }
        }

        int maxIdx = 0;
        for (int i = 1; i < current.length; i++) {
            if (current[i] > current[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }

    private double[] forwardLayer(double[] input, double[][] w, double[] b) {
        double[] output = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            output[i] = b[i];
            for (int j = 0; j < input.length; j++) {
                output[i] += w[i][j] * input[j];
            }
        }
        return output;
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

    public void train(double[][] trainingData, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int i = 0; i < trainingData.length; i++) {
                List<double[]> activations = new ArrayList<>();
                List<double[]> zValues = new ArrayList<>();
                double[] current = trainingData[i];
                activations.add(current);

                for (int l = 0; l < weights.size(); l++) {
                    double[] z = forwardLayer(current, weights.get(l), biases.get(l));
                    zValues.add(z);
                    current = l < weights.size() - 1 ? relu(z) : softmax(z);
                    activations.add(current);
                }

                int target = labels[i];
                double[] targetOneHot = new double[10];
                targetOneHot[target] = 1.0;

                double loss = crossEntropy(current, targetOneHot);
                totalLoss += loss;

                double[][][] gradients = backpropagate(activations, zValues, targetOneHot);
                updateWeights(gradients);
            }
            System.out.println("Epoch " + (epoch + 1) + " - Avg Loss: " + (totalLoss / trainingData.length));
        }
    }

    private double crossEntropy(double[] pred, double[] target) {
        double loss = 0;
        for (int i = 0; i < pred.length; i++) {
            loss -= target[i] * Math.log(pred[i] + 1e-10);
        }
        return loss;
    }

    private double[][][] backpropagate(List<double[]> activations,
                                       List<double[]> zValues, double[] target) {
        int numLayers = weights.size();
        double[][][] gradients = new double[numLayers][2][];

        double[] delta = new double[activations.get(numLayers).length];
        for (int i = 0; i < delta.length; i++) {
            delta[i] = activations.get(numLayers)[i] - target[i];
        }

        for (int l = numLayers - 1; l >= 0; l--) {
            double[][] gradW = new double[weights.get(l).length][weights.get(l)[0].length];
            double[] gradB = new double[biases.get(l).length];

            for (int i = 0; i < weights.get(l).length; i++) {
                for (int j = 0; j < weights.get(l)[0].length; j++) {
                    gradW[i][j] = delta[i] * activations.get(l)[j];
                }
                gradB[i] = delta[i];
            }

            gradients[l][0] = gradW;
            gradients[l][1] = gradB;

            if (l > 0) {
                double[] prevDelta = new double[weights.get(l - 1)[0].length];
                for (int j = 0; j < prevDelta.length; j++) {
                    for (int i = 0; i < weights.get(l).length; i++) {
                        prevDelta[j] += weights.get(l)[i][j] * delta[i];
                    }
                    prevDelta[j] *= (zValues.get(l - 1)[j] > 0 ? 1 : 0);
                }
                delta = prevDelta;
            }
        }

        return gradients;
    }

    private void updateWeights(double[][][] gradients) {
        for (int l = 0; l < weights.size(); l++) {
            double[][] gradW = gradients[l][0];
            double[] gradB = gradients[l][1];

            for (int i = 0; i < weights.get(l).length; i++) {
                for (int j = 0; j < weights.get(l)[0].length; j++) {
                    weights.get(l)[i][j] -= learningRate * gradW[i][j];
                }
                biases.get(l)[i] -= learningRate * gradB[i];
            }
        }
    }
}
```

### Test It

```java
public class DigitClassifierTest {
    public static void main(String[] args) {
        double[][] trainingData = generateSyntheticData(1000);
        int[] labels = generateLabels(trainingData);

        DigitClassifier classifier = new DigitClassifier(64, 128, 64);
        classifier.train(trainingData, labels, 10);

        double[][] testData = generateSyntheticData(10);
        for (double[] data : testData) {
            int prediction = classifier.predict(data);
            System.out.println("Predicted digit: " + prediction);
        }
    }

    private static double[][] generateSyntheticData(int count) {
        Random random = new Random();
        double[][] data = new double[count][64];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 64; j++) {
                data[i][j] = random.nextDouble();
            }
        }
        return data;
    }

    private static int[] generateLabels(double[][] data) {
        Random random = new Random();
        int[] labels = new int[data.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = random.nextInt(10);
        }
        return labels;
    }
}
```

## Deliverables

- [ ] Implement MLP with configurable hidden layers
- [ ] Implement ReLU activation for hidden layers
- [ ] Implement Softmax for output layer
- [ ] Implement cross-entropy loss
- [ ] Implement backpropagation algorithm
- [ ] Implement gradient descent weight updates
- [ ] Train on sample digit data
- [ ] Evaluate accuracy on test data
- [ ] Visualize training loss curve