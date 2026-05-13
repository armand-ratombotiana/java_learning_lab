# Neural Networks Basics - Complete Theory

## 1. Perceptron

### 1.1 Single Neuron Model
y = activation(w^T x + b)
- w: weights
- b: bias
- activation: step function originally

### 1.2 Decision Boundary
Linear separator in input space
- AND, OR: linearly separable
- XOR: not linearly separable

### 1.3 Perceptron Learning Rule
w = w + lr * (y - pred) * x

## 2. Multi-Layer Perceptron (MLP)

### 2.1 Architecture
Input -> Hidden Layers -> Output
Fully connected between layers

### 2.2 Universal Approximation
- Single hidden layer can approximate any continuous function
- With sufficient hidden units

### 2.3 Deep Networks
- Multiple layers
- Hierarchical feature learning

## 3. Activation Functions

### 3.1 Sigmoid
sigma(x) = 1/(1 + e^{-x})
- Range: (0, 1)
- Problem: vanishing gradients

### 3.2 Tanh
tanh(x) = (e^x - e^{-x})/(e^x + e^{-x})
- Range: (-1, 1)
- Zero-centered

### 3.3 ReLU
ReLU(x) = max(0, x)
- Sparse activation
- Fast convergence
- Dying ReLU problem

### 3.4 Leaky ReLU
f(x) = x if x > 0, 0.01x otherwise

### 3.5 ELU
f(x) = x if x > 0, alpha(e^x - 1) otherwise

### 3.6 Softmax
softmax(z)_i = e^{z_i}/sum_j e^{z_j}
For multi-class classification

## 4. Network Architecture

### 4.1 Layer Sizes
- Input: number of features
- Hidden: hyperparameters
- Output: number of classes

### 4.2 Width vs Depth
- Wide: more parameters per layer
- Deep: hierarchical representations

### 4.3 Capacity
Approximate function complexity

## 5. Forward Propagation

### 5.1 Matrix Form
z^{(l)} = W^{(l)} a^{(l-1)} + b^{(l)}
a^{(l)} = activation(z^{(l)})

### 5.2 Vectorized Computation
- Efficient for batches
- GPU acceleration

## 6. Loss Functions

### 6.1 Mean Squared Error (MSE)
L = (1/n) sum (y - pred)^2

### 6.2 Cross-Entropy
L = -sum y log(pred)
- Probabilistic interpretation
- Gradient doesn't vanish

### 6.3 Hinge Loss (SVM)
L = max(0, 1 - y * pred)

## 7. Initialization

### 7.1 Xavier/Glorot
For sigmoid/tanh:
W ~ N(0, 2/(n_in + n_out))

### 7.2 He Initialization
For ReLU:
W ~ N(0, 2/n_in)

### 7.3 Bias Initialization
- Zeros or small constants
- For ReLU: small positive like 0.01

## 8. Regularization

### 8.1 L2 Weight Decay
Add lambda * sum w^2 to loss

### 8.2 Dropout
- Random zeroing of activations
- Ensemble of sub-networks
- p = probability of keeping

### 8.3 Early Stopping
Stop when validation loss increases

### 8.4 Batch Normalization
Normalize layer inputs
- Reduces internal covariate shift
- Allows higher learning rates

## 9. Architecture Design

### 9.1 Input Layer
Match feature dimension

### 9.2 Hidden Layers
- Start with 1-2 layers
- Increase if underfitting
- Width typically 32-512

### 9.3 Output Layer
- Regression: linear
- Classification: softmax

## Java Implementation - Neural Network from Scratch

```java
package com.ml.nn;

import com.ml.la.vector.Vector;
import com.ml.la.matrix.Matrix;

public class NeuralNetwork {
    private int[] layerSizes;
    private Matrix[] weights;
    private Vector[] biases;
    private Activation[] activations;
    
    public NeuralNetwork(int... layerSizes) {
        this.layerSizes = layerSizes;
        this.weights = new Matrix[layerSizes.length - 1];
        this.biases = new Vector[layerSizes.length - 1];
        this.activations = new Activation[layerSizes.length - 1];
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        java.util.Random gen = new java.util.Random();
        for (int i = 0; i < weights.length; i++) {
            int nIn = layerSizes[i];
            int nOut = layerSizes[i + 1];
            double scale = Math.sqrt(2.0 / (nIn + nOut));
            
            weights[i] = Matrix.random(nOut, nIn, gen.nextLong());
            biases[i] = Vector.random(nOut, gen.nextLong());
            
            // He initialization for ReLU
            for (int r = 0; r < nOut; r++) {
                for (int c = 0; c < nIn; c++) {
                    weights[i].data[r][c] *= scale;
                }
            }
        }
    }
    
    public Vector[] forward(Vector input) {
        Vector[] activations = new Vector[layerSizes.length];
        activations[0] = input;
        
        for (int l = 0; l < weights.length; l++) {
            Vector z = MatrixOperations.multiply(weights[l], input);
            z = VectorOperations.add(z, biases[l]);
            
            input = applyActivation(z, l);
            activations[l + 1] = input;
        }
        return activations;
    }
    
    private Vector applyActivation(Vector z, int layer) {
        Vector result = new Vector(new double[z.size()]);
        for (int i = 0; i < z.size(); i++) {
            result.data[i] = sigmoid(z.get(i));  // or other activation
        }
        return result;
    }
    
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-Math.max(-500, Math.min(500, x))));
    }
}
```