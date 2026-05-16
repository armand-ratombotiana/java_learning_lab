# Backpropagation - MINI PROJECT

## Project: Build a Training Pipeline

Implement a complete neural network training pipeline with backpropagation.

### Implementation

```java
package com.ml.training;

import java.util.*;

public class NeuralNetworkTrainer {
    private int[] layerSizes;
    private List<double[][]> weights;
    private List<double[]> biases;
    private double learningRate;
    private double momentum;
    private double weightDecay;

    public NeuralNetworkTrainer(int inputSize, int... hiddenSizes) {
        this.layerSizes = new int[hiddenSizes.length + 2];
        layerSizes[0] = inputSize;
        for (int i = 0; i < hiddenSizes.length; i++) {
            layerSizes[i + 1] = hiddenSizes[i];
        }
        layerSizes[layerSizes.length - 1] = 10;
        this.learningRate = 0.01;
        this.momentum = 0.9;
        this.weightDecay = 0.0001;

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
            biases.add(new double[nOut]);
        }
    }

    private double[] forward(double[] input, List<double[]> cache) {
        double[] current = input;
        cache.add(current);

        for (int l = 0; l < weights.size(); l++) {
            double[] z = matrixVectorMultiply(weights.get(l), current);
            z = vectorAdd(z, biases.get(l));

            if (l == weights.size() - 1) {
                current = softmax(z);
            } else {
                current = relu(z);
            }
            cache.add(z);
            cache.add(current);
        }
        return current;
    }

    public double train(double[][] trainingData, int[] labels, int epochs) {
        List<double[][]> velocityW = new ArrayList<>();
        List<double[]> velocityB = new ArrayList<>();

        for (int l = 0; l < weights.size(); l++) {
            velocityW.add(new double[weights.get(l).length][weights.get(l)[0].length]);
            velocityB.add(new double[biases.get(l).length]);
        }

        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;

            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < trainingData.length; i++) indices.add(i);
            Collections.shuffle(indices);

            for (int idx : indices) {
                List<double[]> cache = new ArrayList<>();
                double[] output = forward(trainingData[idx], cache);

                totalLoss -= Math.log(output[labels[idx]] + 1e-10);

                double[] gradOutput = output.clone();
                gradOutput[labels[idx]] -= 1.0;

                List<double[]> gradientsW = new ArrayList<>();
                List<double[]> gradientsB = new ArrayList<>();

                double[] delta = gradOutput;
                for (int l = weights.size() - 1; l >= 0; l--) {
                    double[] aPrev = cache.get(l * 2);
                    double[] z = cache.get(l * 2 + 1);

                    if (l < weights.size() - 1) {
                        double[] dRelu = new double[z.length];
                        for (int i = 0; i < z.length; i++) {
                            dRelu[i] = z[i] > 0 ? 1.0 : 0.0;
                        }
                        for (int i = 0; i < delta.length; i++) {
                            delta[i] *= dRelu[i];
                        }
                    }

                    double[] gradW = new double[weights.get(l).length * weights.get(l)[0].length];
                    double[] gradB = new double[weights.get(l).length];

                    for (int i = 0; i < weights.get(l).length; i++) {
                        for (int j = 0; j < weights.get(l)[0].length; j++) {
                            gradW[i * weights.get(l)[0].length + j] = delta[i] * aPrev[j];
                        }
                        gradB[i] = delta[i];
                    }
                    gradientsW.add(0, gradW);
                    gradientsB.add(0, gradB);

                    if (l > 0) {
                        double[] newDelta = new double[weights.get(l - 1).length];
                        for (int j = 0; j < newDelta.length; j++) {
                            for (int i = 0; i < delta.length; i++) {
                                newDelta[j] += weights.get(l)[i][j] * delta[i];
                            }
                        }
                        delta = newDelta;
                    }
                }

                for (int l = 0; l < weights.size(); l++) {
                    for (int i = 0; i < weights.get(l).length; i++) {
                        for (int j = 0; j < weights.get(l)[0].length; j++) {
                            double grad = gradientsW.get(l)[i * weights.get(l)[0].length + j];
                            velocityW.get(l)[i][j] = momentum * velocityW.get(l)[i][j]
                                - learningRate * grad - learningRate * weightDecay * weights.get(l)[i][j];
                            weights.get(l)[i][j] += velocityW.get(l)[i][j];
                        }
                        velocityB.get(l)[i] = momentum * velocityB.get(l)[i]
                            - learningRate * gradientsB.get(l)[i];
                        biases.get(l)[i] += velocityB.get(l)[i];
                    }
                }
            }

            System.out.println("Epoch " + (epoch + 1) + " - Loss: " + (totalLoss / trainingData.length));
        }

        return totalLoss / trainingData.length;
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
        for (int i = 1; i < x.length; i++) if (x[i] > max) max = x[i];
        double sum = 0;
        double[] exp = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }
        for (int i = 0; i < x.length; i++) exp[i] /= sum;
        return exp;
    }

    private double[] matrixVectorMultiply(double[][] m, double[] v) {
        double[] r = new double[m.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < v.length; j++) r[i] += m[i][j] * v[j];
        }
        return r;
    }

    private double[] vectorAdd(double[] a, double[] b) {
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i] + b[i];
        return r;
    }
}
```

### Test It

```java
public class TrainerTest {
    public static void main(String[] args) {
        int n = 1000;
        double[][] data = new double[n][4];
        int[] labels = new int[n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            data[i][0] = random.nextDouble();
            data[i][1] = random.nextDouble();
            data[i][2] = random.nextDouble();
            data[i][3] = random.nextDouble();
            labels[i] = random.nextInt(10);
        }

        NeuralNetworkTrainer trainer = new NeuralNetworkTrainer(4, 16, 8);
        trainer.train(data, labels, 20);
    }
}
```

## Deliverables

- [ ] Initialize network with Xavier weights
- [ ] Implement forward pass with ReLU/Softmax
- [ ] Implement backpropagation algorithm
- [ ] Compute gradients for weights and biases
- [ ] Implement gradient descent with momentum
- [ ] Add L2 weight decay
- [ ] Train on sample data
- [ ] Plot training loss curve
- [ ] Evaluate on test set