# Backpropagation - Complete Theory and Implementation

## 1. Chain Rule Review

### 1.1 Single Variable
If z = f(y) and y = g(x), then:
dz/dx = (dz/dy) * (dy/dx)

### 1.2 Multiple Variables
If z = f(u, v), u = g(x), v = h(x), then:
dz/dx = (dz/du)(du/dx) + (dz/dv)(dv/dx)

### 1.3 Vector Form (Jacobians)
For functions F: R^n -> R^m:
dF/dx = (dF/dy)(dy/dx)

## 2. Backpropagation Algorithm

### 2.1 Forward Pass
Compute all layer activations and cache intermediate values.

### 2.2 Loss Computation
Compute loss L(y, pred) at output.

### 2.3 Backward Pass
Propagate gradients from output to input using chain rule.

### 2.4 Weight Update
Update weights using gradients.

## 3. Gradients for Each Layer

### 3.1 Output Layer

**For MSE Loss**:
L = (1/2) sum (y - pred)^2
dL/dpred = pred - y

**For Cross-Entropy + Softmax**:
dL/dz = pred - y
(Simplifies to this nice form!)

### 3.2 Hidden Layer

Gradients flow from next layer:
dL/dz^{(l)} = (W^{(l+1)})^T * dL/dz^{(l+1)} * activation'(z^{(l)})

### 3.3 Weight Gradients

dL/dW^{(l)} = dL/dz^{(l)} * (a^{(l-1)})^T

dL/db^{(l)} = dL/dz^{(l)}

## 4. Matrix Calculus for Backprop

### 4.1 Layer Definition
- Input: a^{(l-1)}
- Pre-activation: z^{(l)} = W^{(l)} a^{(l-1)} + b^{(l)}
- Output: a^{(l)} = f(z^{(l)})

### 4.2 Gradient Definitions
- delta^{(l)} = dL/dz^{(l)}

### 4.3 Recurrence
delta^{(l)} = (W^{(l+1)})^T delta^{(l+1)} * f'(z^{(l)})

### 4.4 Weight Gradients
dL/dW^{(l)} = delta^{(l)} (a^{(l-1)})^T

dL/db^{(l)} = delta^{(l)}

## 5. Activation Function Gradients

### 5.1 Sigmoid
f(z) = 1/(1 + e^{-z})
f'(z) = f(z)(1 - f(z))

### 5.2 Tanh
f(z) = (e^z - e^{-z})/(e^z + e^{-z})
f'(z) = 1 - f(z)^2

### 5.3 ReLU
f(z) = max(0, z)
f'(z) = 1 if z > 0, else 0

### 5.4 Leaky ReLU
f(z) = z if z > 0, else alpha*z
f'(z) = 1 if z > 0, else alpha

## 6. Complete Algorithm

```
Initialize W, b randomly

for epoch in 1..num_epochs:
    for each batch:
        # Forward pass
        a0 = X_batch
        for l = 1..L:
            z[l] = W[l] a[l-1] + b[l]
            a[l] = f(z[l])
        
        # Compute output loss
        delta[L] = dL/da[L] * f'(z[L])
        
        # Backward pass
        for l = L..1:
            dL/dW[l] = delta[l] a[l-1]^T
            dL/db[l] = delta[l]
            if l > 1:
                delta[l-1] = W[l]^T delta[l] * f'(z[l-1])
        
        # Update weights
        W[l] = W[l] - learning_rate * dL/dW[l]
        b[l] = b[l] - learning_rate * dL/db[l]
```

## 7. Java Implementation - Complete Backpropagation

```java
package com.ml.nn;

import com.ml.la.vector.Vector;
import com.ml.la.matrix.Matrix;
import com.ml.la.matrix.MatrixOperations;

public class Backpropagation {

    public static class Layer {
        Matrix weights;
        Vector biases;
        Vector z;      // pre-activation
        Vector a;      // activation
        Vector delta;   // gradient w.r.t. z
        
        public Layer(int inSize, int outSize, java.util.Random gen) {
            double scale = Math.sqrt(2.0 / (inSize + outSize));
            double[][] wData = new double[outSize][inSize];
            for (int i = 0; i < outSize; i++) {
                for (int j = 0; j < inSize; j++) {
                    wData[i][j] = gen.nextGaussian() * scale;
                }
            }
            this.weights = new Matrix(wData);
            this.biases = Vector.random(outSize, gen.nextLong());
        }
    }

    public static class NeuralNetwork {
        private Layer[] layers;
        private double learningRate;
        private int[] layerSizes;
        
        public NeuralNetwork(double lr, int... sizes) {
            this.learningRate = lr;
            this.layerSizes = sizes;
            this.layers = new Layer[sizes.length - 1];
            
            java.util.Random gen = new java.util.Random(42);
            for (int i = 0; i < layers.length; i++) {
                layers[i] = new Layer(sizes[i], sizes[i + 1], gen);
            }
        }
        
        public double[] predict(double[] input) {
            Vector a = new Vector(input);
            for (int l = 0; l < layers.length; l++) {
                layers[l].z = MatrixOperations.multiply(layers[l].weights, a);
                layers[l].z = VectorOperations.add(layers[l].z, layers[l].biases);
                
                a = relu(layers[l].z);
                if (l == layers.length - 1) {
                    a = softmax(layers[l].z);
                }
                layers[l].a = a;
            }
            return a.toArray();
        }
        
        public double train(double[] x, double[] y) {
            // Forward pass
            predict(x);
            
            // Compute output delta (cross-entropy with softmax)
            Vector yTrue = new Vector(y);
            Vector pred = layers[layers.length - 1].a;
            Vector delta = VectorOperations.subtract(pred, yTrue);
            
            // Backward pass
            for (int l = layers.length - 1; l >= 0; l--) {
                layers[l].delta = delta;
                
                // Gradient w.r.t. weights: delta * a^T
                Vector aPrev = (l == 0) ? new Vector(x) : layers[l - 1].a;
                Matrix gradW = MatrixOperations.outerProduct(delta, aPrev);
                MatrixOperations.scale(gradW, learningRate);
                layers[l].weights = MatrixOperations.subtract(layers[l].weights, gradW);
                
                // Gradient w.r.t. biases: delta
                Vector gradB = delta;
                layers[l].biases = VectorOperations.subtract(layers[l].biases, gradB);
                
                // Propagate delta to previous layer
                if (l > 0) {
                    delta = MatrixOperations.multiply(
                        MatrixOperations.transpose(layers[l].weights), delta);
                    
                    // Apply activation derivative
                    Vector zPrev = layers[l - 1].z;
                    delta = elementwiseMultiply(delta, reluDerivative(zPrev));
                }
            }
            
            // Compute loss
            return crossEntropy(new Vector(y), layers[layers.length - 1].a);
        }
        
        public double trainBatch(double[][] X, double[][] Y) {
            double totalLoss = 0;
            for (int i = 0; i < X.length; i++) {
                totalLoss += train(X[i], Y[i]);
            }
            return totalLoss / X.length;
        }
        
        private Vector relu(Vector x) {
            return VectorOperations.applyFunction(x, v -> Math.max(0, v));
        }
        
        private Vector reluDerivative(Vector x) {
            return VectorOperations.applyFunction(x, v -> v > 0 ? 1 : 0);
        }
        
        private Vector softmax(Vector z) {
            double max = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < z.size(); i++) {
                max = Math.max(max, z.get(i));
            }
            
            double sum = 0;
            double[] exp = new double[z.size()];
            for (int i = 0; i < z.size(); i++) {
                exp[i] = Math.exp(z.get(i) - max);
                sum += exp[i];
            }
            
            double[] result = new double[z.size()];
            for (int i = 0; i < z.size(); i++) {
                result[i] = exp[i] / sum;
            }
            return new Vector(result);
        }
        
        private double crossEntropy(Vector yTrue, Vector yPred) {
            double sum = 0;
            for (int i = 0; i < yTrue.size(); i++) {
                sum += yTrue.get(i) * Math.log(Math.max(1e-15, yPred.get(i)));
            }
            return -sum;
        }
    }

    // Helper methods
    private static Vector elementwiseMultiply(Vector a, Vector b) {
        return VectorOperations.elementwiseMultiply(a, b);
    }
}
```

## 8. Batch Training

```java
public class BatchTrainer {
    private NeuralNetwork nn;
    private double learningRate;
    private int batchSize;
    
    public BatchTrainer(NeuralNetwork nn, double lr, int batchSize) {
        this.nn = nn;
        this.learningRate = lr;
        this.batchSize = batchSize;
    }
    
    public void trainEpoch(double[][] X, double[][] Y) {
        int n = X.length;
        Matrix[] gradW = new Matrix[nn.layers.length];
        Vector[] gradB = new Vector[nn.layers.length];
        
        // Initialize gradients
        for (int l = 0; l < nn.layers.length; l++) {
            gradW[l] = MatrixOperations.zeros(
                nn.layers[l].weights.rows(),
                nn.layers[l].weights.cols()
            );
            gradB[l] = VectorOperations.zeros(nn.layers[l].biases.size());
        }
        
        // Accumulate gradients over batch
        for (int i = 0; i < n; i++) {
            nn.predict(X[i]);
            
            // Compute output delta
            Vector yTrue = new Vector(Y[i]);
            Vector pred = nn.layers[nn.layers.length - 1].a;
            Vector delta = VectorOperations.subtract(pred, yTrue);
            
            // Backward pass with gradient accumulation
            for (int l = nn.layers.length - 1; l >= 0; l--) {
                Vector aPrev = (l == 0) ? new Vector(X[i]) : nn.layers[l - 1].a;
                
                gradW[l] = MatrixOperations.add(gradW[l],
                    MatrixOperations.outerProduct(delta, aPrev));
                gradB[l] = VectorOperations.add(gradB[l], delta);
                
                if (l > 0) {
                    delta = MatrixOperations.multiply(
                        MatrixOperations.transpose(nn.layers[l].weights), delta);
                    delta = elementwiseMultiply(delta, 
                        reluDerivative(nn.layers[l - 1].z));
                }
            }
        }
        
        // Average and update
        double scale = learningRate / n;
        for (int l = 0; l < nn.layers.length; l++) {
            Matrix gradAvgW = MatrixOperations.scale(gradW[l], scale);
            nn.layers[l].weights = MatrixOperations.subtract(
                nn.layers[l].weights, gradAvgW);
            nn.layers[l].biases = VectorOperations.subtract(
                nn.layers[l].biases, VectorOperations.scale(gradB[l], scale));
        }
    }
}
```

## 9. Gradient Checking

```java
public boolean checkGradients(double[][] X, double[][] Y, double epsilon) {
    NeuralNetwork nn = new NeuralNetwork(0.01, 2, 4, 2);
    
    // Compute numerical gradients
    for (int l = 0; l < nn.layers.length; l++) {
        for (int i = 0; i < nn.layers[l].weights.rows(); i++) {
            for (int j = 0; j < nn.layers[l].weights.cols(); j++) {
                double orig = nn.layers[l].weights.data[i][j];
                
                nn.layers[l].weights.data[i][j] = orig + epsilon;
                double lossPlus = computeLoss(nn, X, Y);
                
                nn.layers[l].weights.data[i][j] = orig - epsilon;
                double lossMinus = computeLoss(nn, X, Y);
                
                double numericalGrad = (lossPlus - lossMinus) / (2 * epsilon);
                
                // Compare with analytical gradient
                double analyticalGrad = getAnalyticalGradient(nn, l, i, j);
                
                double diff = Math.abs(numericalGrad - analyticalGrad);
                if (diff > 1e-5) {
                    System.out.println("Gradient mismatch at layer " + l + 
                        " [" + i + "," + j + "]: " + diff);
                    return false;
                }
            }
        }
    }
    return true;
}
```

## 10. Complete Training Loop

```java
public class Trainer {
    public static void main(String[] args) {
        // Create network: 784 input, 256, 128 hidden, 10 output
        NeuralNetwork nn = new NeuralNetwork(0.01, 784, 256, 128, 10);
        
        // Load data (MNIST example)
        double[][] Xtrain = loadMNISTImages("train-images.csv");
        double[][] Ytrain = loadMNNISTLabels("train-labels.csv");
        
        // Training
        int epochs = 50;
        int batchSize = 32;
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            // Shuffle data
            shuffle(Xtrain, Ytrain);
            
            // Mini-batch training
            for (int i = 0; i < Xtrain.length; i += batchSize) {
                int end = Math.min(i + batchSize, Xtrain.length);
                
                // Train on batch
                double loss = 0;
                for (int j = i; j < end; j++) {
                    loss += nn.train(Xtrain[j], Ytrain[j]);
                }
                loss /= (end - i);
                
                if (i % 1000 == 0) {
                    System.out.println("Epoch " + epoch + 
                        ", Batch " + i + ", Loss: " + loss);
                }
            }
            
            // Validation
            double accuracy = evaluate(nn, Xval, Yval);
            System.out.println("Epoch " + epoch + " Validation Accuracy: " + accuracy);
        }
    }
}
```