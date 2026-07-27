# Multi-Layer Perceptron with Backpropagation

## Problem Statement

**Problem:** Implement a fully-connected multi-layer perceptron (MLP) with backpropagation and stochastic gradient descent for multi-class classification.

Design and implement an MLP that:
1. Supports an arbitrary number of hidden layers with configurable width.
2. Uses the sigmoid activation function for hidden layers and softmax for the output layer.
3. Implements forward pass, backward pass (backpropagation), and parameter updates.
4. Learns to classify synthetic spiral data (a common non-linear benchmark).

**Example:**
```
Input:  Layer sizes: [2, 10, 10, 3]  (2 input features, 2 hidden layers of 10 neurons, 3 output classes)
        Learning rate: 0.01
        Epochs: 1000
Output: Training accuracy >= 90% on the spiral dataset (300 samples per class)
```

**Constraints:**
- $1 \leq L \leq 10$ (number of layers)
- $1 \leq \text{layer\_sizes}[i] \leq 1024$ (neurons per layer)
- $1 \leq N \leq 10^6$ (training samples)
- Must use mini-batch gradient descent with configurable batch size.

---

## Step-by-Step Solution Walkthrough

### 1. MLP Architecture

A multi-layer perceptron consists of:
- An input layer (size $n_{\text{in}}$)
- $L$ hidden layers with activation functions
- An output layer (size $n_{\text{out}}$)

Each layer performs: $\mathbf{h}^{(l)} = \sigma^{(l)}(\mathbf{W}^{(l)} \cdot \mathbf{h}^{(l-1)} + \mathbf{b}^{(l)})$

where $\mathbf{W}^{(l)}$ is the weight matrix, $\mathbf{b}^{(l)}$ is the bias vector, and $\sigma^{(l)}$ is the activation function.

### 2. Forward Pass

For each layer $l = 1, 2, ..., L$:

1. Compute the linear transformation: $\mathbf{z}^{(l)} = \mathbf{W}^{(l)} \mathbf{a}^{(l-1)} + \mathbf{b}^{(l)}$
2. Apply activation: $\mathbf{a}^{(l)} = \sigma^{(l)}(\mathbf{z}^{(l)})$

Where $\mathbf{a}^{(0)} = \mathbf{x}$ is the input.

For the output layer, we use **softmax**:
$$\text{softmax}(\mathbf{z})_i = \frac{e^{z_i}}{\sum_{j=1}^{K} e^{z_j}}$$

### 3. Loss Function: Cross-Entropy

For multi-class classification with $K$ classes:
$$\mathcal{L}(\mathbf{y}, \hat{\mathbf{y}}) = -\sum_{i=1}^{K} y_i \log(\hat{y}_i)$$

where $\mathbf{y}$ is the one-hot encoded true label and $\hat{\mathbf{y}}$ is the softmax output.

### 4. Backward Pass (Backpropagation)

Backpropagation computes gradients of the loss with respect to all parameters using the chain rule.

**Output layer gradient (combining softmax + cross-entropy):**
$$\frac{\partial \mathcal{L}}{\partial \mathbf{z}^{(L)}} = \hat{\mathbf{y}} - \mathbf{y}$$

This elegant simplification occurs because the softmax and cross-entropy gradient combine to $\hat{y}_i - y_i$.

**Hidden layer gradient:**
$$\frac{\partial \mathcal{L}}{\partial \mathbf{z}^{(l)}} = \left((\mathbf{W}^{(l+1)})^T \cdot \boldsymbol{\delta}^{(l+1)}\right) \odot \sigma'(\mathbf{z}^{(l)})$$

where $\boldsymbol{\delta}^{(l)} = \frac{\partial \mathcal{L}}{\partial \mathbf{z}^{(l)}}$ and $\odot$ is element-wise multiplication.

**Parameter gradients:**
$$\frac{\partial \mathcal{L}}{\partial \mathbf{W}^{(l)}} = \boldsymbol{\delta}^{(l)} \otimes \mathbf{a}^{(l-1)}$$
$$\frac{\partial \mathcal{L}}{\partial \mathbf{b}^{(l)}} = \boldsymbol{\delta}^{(l)}$$

### 5. Parameter Update

Using gradient descent:
$$\mathbf{W}^{(l)} \leftarrow \mathbf{W}^{(l)} - \eta \frac{\partial \mathcal{L}}{\partial \mathbf{W}^{(l)}}$$
$$\mathbf{b}^{(l)} \leftarrow \mathbf{b}^{(l)} - \eta \frac{\partial \mathcal{L}}{\partial \mathbf{b}^{(l)}}$$

### 6. The Chain Rule in Detail

To understand backpropagation, trace the gradient flow for a simple 2-layer network:

**Network:** $\mathbf{x} \to \mathbf{W}^{(1)}, \mathbf{b}^{(1)} \to \mathbf{z}^{(1)} \to \sigma \to \mathbf{a}^{(1)} \to \mathbf{W}^{(2)}, \mathbf{b}^{(2)} \to \mathbf{z}^{(2)} \to \text{softmax} \to \hat{\mathbf{y}} \to \mathcal{L}$

**Gradient chain for $\mathbf{W}^{(1)}$:**
$$\frac{\partial \mathcal{L}}{\partial \mathbf{W}^{(1)}} = \frac{\partial \mathcal{L}}{\partial \hat{\mathbf{y}}} \cdot \frac{\partial \hat{\mathbf{y}}}{\partial \mathbf{z}^{(2)}} \cdot \frac{\partial \mathbf{z}^{(2)}}{\partial \mathbf{a}^{(1)}} \cdot \frac{\partial \mathbf{a}^{(1)}}{\partial \mathbf{z}^{(1)}} \cdot \frac{\partial \mathbf{z}^{(1)}}{\partial \mathbf{W}^{(1)}}$$

Each term is a Jacobian matrix. The product combines to give the final gradient.

### 7. Vanishing Gradients

For sigmoid activation, $\sigma'(z) = \sigma(z)(1 - \sigma(z)) \in (0, 0.25]$. When many layers are stacked, these small derivatives multiply, causing gradients to vanish exponentially with depth.

**Impact:** Early layers learn very slowly or not at all. This was a major obstacle for deep networks before ReLU and batch normalization.

---

## Java Implementation

```java
package com.deeplearning.mlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Multi-Layer Perceptron with backpropagation and mini-batch gradient descent.
 * 
 * <p>This implementation supports an arbitrary number of fully-connected
 * hidden layers with sigmoid activation and a softmax output layer
 * for multi-class classification.</p>
 */
public class MultiLayerPerceptron {

    private final List<Layer> layers;
    private final double learningRate;
    private final int batchSize;
    private final Random random;

    /**
     * Constructs an MLP with the given architecture.
     *
     * @param layerSizes  array where layerSizes[0] = input dimension,
     *                    layerSizes[last] = output dimension
     * @param learningRate step size for gradient descent
     * @param batchSize    mini-batch size
     */
    public MultiLayerPerceptron(int[] layerSizes, double learningRate, int batchSize) {
        this.learningRate = learningRate;
        this.batchSize = batchSize;
        this.random = new Random(42);
        this.layers = new ArrayList<>();

        for (int i = 1; i < layerSizes.length; i++) {
            boolean isOutput = (i == layerSizes.length - 1);
            layers.add(new Layer(layerSizes[i - 1], layerSizes[i], random, isOutput));
        }
    }

    /**
     * Trains the MLP on the provided data.
     *
     * @param features training features shape [N][inputDim]
     * @param labels   training labels shape [N][outputDim] (one-hot encoded)
     * @param epochs   number of training epochs
     */
    public void train(double[][] features, double[][] labels, int epochs) {
        int n = features.length;
        for (int epoch = 0; epoch < epochs; epoch++) {
            shuffle(features, labels, n);
            for (int batchStart = 0; batchStart < n; batchStart += batchSize) {
                int batchEnd = Math.min(batchStart + batchSize, n);
                int batchLen = batchEnd - batchStart;
                // Forward pass for batch (compute per-sample, accumulate gradients)
                for (int sample = batchStart; sample < batchEnd; sample++) {
                    forward(features[sample]);
                    backward(labels[sample], 1.0 / batchLen);
                }
                // Update parameters
                for (Layer layer : layers) {
                    layer.update(learningRate);
                }
            }
            if (epoch % 100 == 0) {
                double acc = evaluate(features, labels);
                System.out.printf("Epoch %d, accuracy: %.4f%n", epoch, acc);
            }
        }
    }

    /**
     * Runs forward pass for a single sample.
     */
    private double[] forward(double[] input) {
        double[] activations = input;
        for (Layer layer : layers) {
            activations = layer.forward(activations);
        }
        return activations;
    }

    /**
     * Runs backward pass for a single sample.
     */
    private void backward(double[] target, double scale) {
        // Output layer gradient: softmax output minus target
        double[] delta = new double[target.length];
        double[] output = layers.get(layers.size() - 1).activations;
        for (int i = 0; i < delta.length; i++) {
            delta[i] = (output[i] - target[i]) * scale;
        }
        // Backpropagate through all layers
        for (int l = layers.size() - 1; l >= 0; l--) {
            delta = layers.get(l).backward(delta);
        }
    }

    /**
     * Predicts class indices for input samples.
     */
    public int[] predict(double[][] features) {
        int[] predictions = new int[features.length];
        for (int i = 0; i < features.length; i++) {
            double[] output = forward(features[i]);
            predictions[i] = argmax(output);
        }
        return predictions;
    }

    /**
     * Evaluates accuracy on the given dataset.
     */
    public double evaluate(double[][] features, double[][] labels) {
        int correct = 0;
        for (int i = 0; i < features.length; i++) {
            double[] output = forward(features[i]);
            if (argmax(output) == argmax(labels[i])) {
                correct++;
            }
        }
        return (double) correct / features.length;
    }

    private int argmax(double[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[idx]) idx = i;
        }
        return idx;
    }

    private void shuffle(double[][] features, double[][] labels, int n) {
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            double[] tmpF = features[i];
            features[i] = features[j];
            features[j] = tmpF;
            double[] tmpL = labels[i];
            labels[i] = labels[j];
            labels[j] = tmpL;
        }
    }

    // --- Inner class: Layer ---

    static class Layer {
        final int inputSize;
        final int outputSize;
        final double[][] weights;
        final double[] biases;
        final boolean isOutput;

        // Cache for forward/backward
        double[] input;
        double[] z;
        double[] activations;
        double[][] weightGradients;
        double[] biasGradients;

        Layer(int inputSize, int outputSize, Random random, boolean isOutput) {
            this.inputSize = inputSize;
            this.outputSize = outputSize;
            this.isOutput = isOutput;
            this.weights = new double[outputSize][inputSize];
            this.biases = new double[outputSize];
            this.weightGradients = new double[outputSize][inputSize];
            this.biasGradients = new double[outputSize];

            // Xavier initialization
            double scale = Math.sqrt(2.0 / (inputSize + outputSize));
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = random.nextGaussian() * scale;
                }
                biases[i] = 0.0;
            }
        }

        double[] forward(double[] input) {
            this.input = input;
            this.z = new double[outputSize];
            this.activations = new double[outputSize];

            // Linear transformation: z = W * input + b
            for (int i = 0; i < outputSize; i++) {
                double sum = biases[i];
                for (int j = 0; j < inputSize; j++) {
                    sum += weights[i][j] * input[j];
                }
                z[i] = sum;
            }

            // Activation
            if (isOutput) {
                softmax(z, activations);
            } else {
                for (int i = 0; i < outputSize; i++) {
                    activations[i] = sigmoid(z[i]);
                }
            }
            return activations;
        }

        /**
         * Backward pass: computes gradients w.r.t. parameters.
         * @param deltaGrad the gradient from the next layer (∂L/∂output)
         * @return gradient to pass to the previous layer (∂L/∂input)
         */
        double[] backward(double[] deltaGrad) {
            // δ_local = sigmoid'(z) * deltaGrad (for hidden) or deltaGrad (for output, already combined)
            double[] deltaLocal;
            if (isOutput) {
                deltaLocal = deltaGrad;
            } else {
                deltaLocal = new double[outputSize];
                for (int i = 0; i < outputSize; i++) {
                    double sig = activations[i];
                    deltaLocal[i] = deltaGrad[i] * sig * (1.0 - sig);
                }
            }

            // Accumulate gradients w.r.t. weights and biases
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weightGradients[i][j] += deltaLocal[i] * input[j];
                }
                biasGradients[i] += deltaLocal[i];
            }

            // Compute gradient for previous layer: ∂L/∂input = W^T * δ_local
            double[] prevDelta = new double[inputSize];
            for (int j = 0; j < inputSize; j++) {
                double sum = 0.0;
                for (int i = 0; i < outputSize; i++) {
                    sum += weights[i][j] * deltaLocal[i];
                }
                prevDelta[j] = sum;
            }
            return prevDelta;
        }

        void update(double lr) {
            for (int i = 0; i < outputSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] -= lr * weightGradients[i][j];
                    weightGradients[i][j] = 0.0; // reset
                }
                biases[i] -= lr * biasGradients[i];
                biasGradients[i] = 0.0; // reset
            }
        }

        private static double sigmoid(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }

        private static void softmax(double[] input, double[] output) {
            double max = input[0];
            for (double v : input) if (v > max) max = v;
            double sum = 0.0;
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.exp(input[i] - max);
                sum += output[i];
            }
            for (int i = 0; i < input.length; i++) {
                output[i] /= sum;
            }
        }
    }

    // --- Utility: Spiral dataset generator ---

    public static double[][] generateSpiralData(int samplesPerClass, int classes, double[][] featuresOut, double[][] labelsOut) {
        int n = samplesPerClass * classes;
        int idx = 0;
        for (int c = 0; c < classes; c++) {
            for (int i = 0; i < samplesPerClass; i++) {
                double t = (double) i / samplesPerClass;
                double angle = t * 4 * Math.PI + c * 2 * Math.PI / classes;
                double radius = t * 2.0 + 0.5;
                featuresOut[idx][0] = radius * Math.sin(angle);
                featuresOut[idx][1] = radius * Math.cos(angle);
                for (int j = 0; j < classes; j++) {
                    labelsOut[idx][j] = (j == c) ? 1.0 : 0.0;
                }
                idx++;
            }
        }
        return featuresOut;
    }

    // --- Main method for testing ---

    public static void main(String[] args) {
        int samplesPerClass = 300;
        int classes = 3;
        int n = samplesPerClass * classes;
        double[][] X = new double[n][2];
        double[][] Y = new double[n][classes];
        generateSpiralData(samplesPerClass, classes, X, Y);

        int[] architecture = {2, 10, 10, classes};
        MultiLayerPerceptron mlp = new MultiLayerPerceptron(architecture, 0.01, 32);

        System.out.println("Training MLP on spiral data...");
        long start = System.nanoTime();
        mlp.train(X, Y, 1000);
        long end = System.nanoTime();

        double finalAcc = mlp.evaluate(X, Y);
        System.out.printf("Final training accuracy: %.4f%n", finalAcc);
        System.out.printf("Training time: %.2f seconds%n", (end - start) / 1e9);
    }
}
```

### Spiral Dataset Generator (Standalone)

```java
package com.deeplearning.mlp;

/**
 * Generates synthetic spiral dataset for testing non-linear classification.
 * Each class spirals outward from the center at different starting angles.
 */
public class SpiralGenerator {

    /**
     * Generates spiral data points.
     *
     * @param samplesPerClass number of points per class
     * @param classes         number of distinct classes
     * @return array where result[0] = features[N][2], result[1] = labels[N][classes](one-hot)
     */
    public static Object[] generate(int samplesPerClass, int classes) {
        int n = samplesPerClass * classes;
        double[][] features = new double[n][2];
        double[][] labels = new double[n][classes];

        for (int c = 0; c < classes; c++) {
            for (int i = 0; i < samplesPerClass; i++) {
                double t = (double) i / samplesPerClass;
                double angle = t * 4 * Math.PI + c * 2 * Math.PI / classes;
                double radius = t * 2.0 + 0.5;
                features[c * samplesPerClass + i][0] = radius * Math.sin(angle);
                features[c * samplesPerClass + i][1] = radius * Math.cos(angle);
                labels[c * samplesPerClass + i][c] = 1.0;
            }
        }
        return new Object[]{features, labels};
    }

    public static void main(String[] args) {
        Object[] data = generate(300, 3);
        double[][] X = (double[][]) data[0];
        double[][] Y = (double[][]) data[1];
        System.out.printf("Generated spiral dataset: %d samples, %d classes%n", X.length, Y[0].length);
        // Print first 5 samples
        for (int i = 0; i < 5; i++) {
            System.out.printf("Sample %d: x=[%.4f, %.4f], class=%d%n",
                i, X[i][0], X[i][1], argmax(Y[i]));
        }
    }

    private static int argmax(double[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[idx]) idx = i;
        }
        return idx;
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Forward pass (single sample):** $O(\sum_{l=1}^{L} n_{l-1} \cdot n_l)$ where $n_l$ is the size of layer $l$. This is the cost of all matrix-vector multiplications.

**Backward pass (single sample):** $O(\sum_{l=1}^{L} n_{l-1} \cdot n_l)$ — same order as forward, computing weight gradients and backpropagating deltas.

**Parameter update (per batch):** $O(\sum_{l=1}^{L} n_{l-1} \cdot n_l)$.

**Total training:** $O(E \cdot N \cdot \sum_{l} n_{l-1} n_l / B)$ where $E$ is epochs, $N$ is total samples, and $B$ is batch size.

### Space Complexity

**Parameter storage:** $O(\sum_{l=1}^{L} n_{l-1} \cdot n_l + \sum_{l=1}^{L} n_l)$ for weights and biases.

**Forward/backward cache:** $O(\sum_{l=1}^{L} n_l)$ for activations, $z$, and input values.

**Gradient accumulation:** Same as parameter storage: $O(\sum_{l=1}^{L} n_{l-1} \cdot n_l)$.

---

## Follow-Up Questions

### Q1: Why is the combination of softmax and cross-entropy gradient so simple ($\hat{y} - y$)?

**Answer:** This is a key result in deep learning. For the cross-entropy loss $\mathcal{L} = -\sum_j y_j \log(\hat{y}_j)$ with softmax $\hat{y}_j = e^{z_j} / \sum_k e^{z_k}$:

$$\frac{\partial \mathcal{L}}{\partial z_i} = \sum_j \frac{\partial \mathcal{L}}{\partial \hat{y}_j} \cdot \frac{\partial \hat{y}_j}{\partial z_i}$$

$$\frac{\partial \mathcal{L}}{\partial \hat{y}_j} = -\frac{y_j}{\hat{y}_j}$$

$$\frac{\partial \hat{y}_j}{\partial z_i} = \hat{y}_j(\delta_{ij} - \hat{y}_i)$$

Combining:
$$\frac{\partial \mathcal{L}}{\partial z_i} = \sum_j \left(-\frac{y_j}{\hat{y}_j}\right) \cdot \hat{y}_j(\delta_{ij} - \hat{y}_i) = -\sum_j y_j(\delta_{ij} - \hat{y}_i) = -\left(y_i - \hat{y}_i \sum_j y_j\right)$$

Since $\sum_j y_j = 1$ (one-hot encoding sums to 1):
$$\frac{\partial \mathcal{L}}{\partial z_i} = \hat{y}_i - y_i$$

### Q2: What causes vanishing gradients and how do modern architectures mitigate this?

**Answer:** Vanishing gradients occur when the derivative of the activation function is less than 1, causing the gradient to shrink as it's backpropagated through multiple layers. With sigmoid, $\max(\sigma'(z)) = 0.25$, so after $L$ layers, the gradient scales by at most $0.25^L$.

**Mitigations:**
1. **ReLU activation:** $f'(x) = 1$ for $x > 0$, $0$ otherwise — no squashing for positive inputs.
2. **Batch normalization:** Normalizes layer inputs, preventing saturation.
3. **Residual connections:** Allows gradient to flow directly through skip connections.
4. **Proper initialization:** Xavier/He initialization maintains variance.
5. **Gradient clipping:** Caps gradient magnitude at a threshold.

### Q3: How does mini-batch gradient descent compare to full-batch and stochastic (SGD)?

| Variant | Update frequency | Gradient noise | Convergence | GPU efficiency |
|---------|-----------------|----------------|-------------|----------------|
| Full-batch GD | Once per epoch | Low | Stable but slow | Poor for large N |
| SGD (batch=1) | N times per epoch | High | Can escape local minima, but oscillates | Poor |
| Mini-batch (32-512) | N/B times per epoch | Moderate | Fast, good convergence | Excellent |

Mini-batch provides a good trade-off: enough gradient noise to escape shallow local minima, efficient vectorized computation, and stable convergence.

### Q4: How would you add L2 regularization to the MLP?

**Answer:** L2 regularization adds a penalty term to the loss: $\mathcal{L}_{\text{reg}} = \mathcal{L} + \frac{\lambda}{2} \sum_l \|\mathbf{W}^{(l)}\|_F^2$. The gradient becomes:

$$\frac{\partial \mathcal{L}_{\text{reg}}}{\partial \mathbf{W}^{(l)}} = \frac{\partial \mathcal{L}}{\partial \mathbf{W}^{(l)}} + \lambda \mathbf{W}^{(l)}$$

The weight update becomes:
$$\mathbf{W}^{(l)} \leftarrow \mathbf{W}^{(l)} - \eta \left(\frac{\partial \mathcal{L}}{\partial \mathbf{W}^{(l)}} + \lambda \mathbf{W}^{(l)}\right) = (1 - \eta\lambda)\mathbf{W}^{(l)} - \eta \frac{\partial \mathcal{L}}{\partial \mathbf{W}^{(l)}}$$

This is also called **weight decay**.

### Q5: What is the difference between batch gradient descent and using the full training set?

**Answer:** They are the same thing. "Batch" in batch gradient descent means using ALL training samples to compute the gradient. "Mini-batch" uses a subset. "Stochastic" uses a single sample. The terminology can be confusing because in modern deep learning, "batch size" usually refers to the mini-batch size.

### Q6: Derive the gradient for the sigmoid activation function.

**Answer:** Given $\sigma(z) = \frac{1}{1 + e^{-z}}$:

$$\sigma'(z) = \frac{d}{dz} (1 + e^{-z})^{-1} = -(1 + e^{-z})^{-2} \cdot (-e^{-z}) = \frac{e^{-z}}{(1 + e^{-z})^2} = \frac{1}{1 + e^{-z}} \cdot \frac{e^{-z}}{1 + e^{-z}}$$

$$\sigma'(z) = \sigma(z) \cdot (1 - \sigma(z))$$

### Q7: How would you modify the MLP to support regression (single output, no softmax)?

**Answer:**
1. Remove softmax from the output layer (use linear activation: $f(z) = z$).
2. Replace cross-entropy loss with **mean squared error**: $\mathcal{L} = \frac{1}{2}(\hat{y} - y)^2$.
3. The output gradient becomes $\frac{\partial \mathcal{L}}{\partial \mathbf{z}^{(L)}} = \hat{y} - y$ (same form, different derivation).
4. For multi-output regression, use the same approach for each output dimension.

### Q8: How do you choose the number of hidden layers and neurons?

**Answer:** There's no universal rule, but some guidelines:
- **Shallow networks (1-2 hidden layers):** Often sufficient for many problems (universal approximation theorem).
- **Deep networks (3+ hidden layers):** More parameter-efficient for complex functions; can learn hierarchical representations.
- **Width:** Too few neurons = underfitting; too many = overfitting. Start with $n_l \approx \sqrt{n_{\text{in}} \cdot n_{\text{out}}}$ or $2/3 \cdot n_{\text{in}} + n_{\text{out}}$.
- Practical approach: Use **hyperparameter search** (grid search, random search, Bayesian optimization).

---

## Test Cases

| Test Case | Description | Architecture | Expected |
|-----------|-------------|-------------|----------|
| TC-01 | Spiral 3-class | [2, 10, 3] | Accuracy >= 85% |
| TC-02 | Spiral 3-class deep | [2, 10, 10, 3] | Accuracy >= 90% |
| TC-03 | Single hidden layer | [2, 5, 3] | Accuracy >= 80% |
| TC-04 | Wide hidden layer | [2, 100, 3] | Accuracy >= 90% (may overfit) |
| TC-05 | Binary classification | [2, 10, 1] | Accuracy >= 90% |
| TC-06 | 5-class spiral | [2, 20, 20, 5] | Accuracy >= 80% |
| TC-07 | High learning rate (0.1) | [2, 10, 3] | May diverge (instability) |
| TC-08 | Low learning rate (0.0001) | [2, 10, 3] | Slow convergence |
| TC-09 | Large batch size (256) | [2, 10, 3] | Slower per epoch, stable |
| TC-10 | Single layer (linear) | [2, 3] (no hidden) | Accuracy ~ 33-50% (linear only) |

---

## Key Takeaways

- Backpropagation efficiently computes gradients using the chain rule in $O(\sum n_{l-1}n_l)$ time.
- The combination of softmax + cross-entropy gives a clean gradient of $\hat{y} - y$.
- Vanishing gradients severely limit deep networks with sigmoid activation (motivating ReLU, BN, residuals).
- Mini-batch gradient descent balances gradient noise and computational efficiency.
- The MLP implementation exercise builds intuition for all subsequent deep learning concepts.
