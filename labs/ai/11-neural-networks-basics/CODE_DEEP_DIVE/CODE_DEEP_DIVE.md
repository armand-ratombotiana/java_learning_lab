# Neural Networks Basics - CODE DEEP DIVE

## Java Implementations

### 1. Perceptron Implementation

```java
package com.ml.nn;

import java.util.function.DoubleUnaryOperator;

public class Perceptron {
    private double[] weights;
    private double bias;
    private DoubleUnaryOperator activation;
    private double learningRate;

    public Perceptron(int inputSize, double learningRate) {
        this.weights = new double[inputSize];
        this.bias = 0.0;
        this.learningRate = learningRate;
        this.activation = this::stepFunction;
        initializeWeights();
    }

    private void initializeWeights() {
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble() * 2 - 1;
        }
    }

    private double stepFunction(double x) {
        return x >= 0 ? 1.0 : 0.0;
    }

    public int predict(double[] inputs) {
        double sum = bias;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * inputs[i];
        }
        return (int) activation.applyAsDouble(sum);
    }

    public void train(double[][] trainingData, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < trainingData.length; i++) {
                int prediction = predict(trainingData[i]);
                int error = labels[i] - prediction;

                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * error * trainingData[i][j];
                }
                bias += learningRate * error;
            }
        }
    }

    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }
}
```

### 2. Multi-Layer Perceptron (MLP)

```java
package com.ml.nn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultiLayerPerceptron {
    private List<int[]> layerConfigs;
    private List<double[][]> weights;
    private List<double[]> biases;
    private List<ActivationFunction> activations;
    private Random random;

    public MultiLayerPerceptron(int... layerSizes) {
        this.layerConfigs = new ArrayList<>();
        this.weights = new ArrayList<>();
        this.biases = new ArrayList<>();
        this.activations = new ArrayList<>();
        this.random = new Random(42);

        for (int i = 0; i < layerSizes.length; i++) {
            layerConfigs.add(new int[]{layerSizes[i], i});
        }

        initializeNetwork();
    }

    private void initializeNetwork() {
        for (int i = 0; i < layerConfigs.size() - 1; i++) {
            int inputSize = layerConfigs.get(i)[0];
            int outputSize = layerConfigs.get(i + 1)[0];

            double[][] layerWeights = new double[outputSize][inputSize];
            double[] layerBiases = new double[outputSize];

            double scale = Math.sqrt(2.0 / (inputSize + outputSize));
            for (int j = 0; j < outputSize; j++) {
                for (int k = 0; k < inputSize; k++) {
                    layerWeights[j][k] = random.nextGaussian() * scale;
                }
                layerBiases[j] = random.nextGaussian() * 0.01;
            }

            weights.add(layerWeights);
            biases.add(layerBiases);

            if (i == layerConfigs.size() - 2) {
                activations.add(new SoftmaxActivation());
            } else {
                activations.add(new ReLUActivation());
            }
        }
    }

    public double[] forward(double[] input) {
        double[] current = input;

        for (int i = 0; i < weights.size(); i++) {
            current = matrixVectorMultiply(weights.get(i), current);
            current = vectorAdd(current, biases.get(i));
            current = activations.get(i).apply(current);
        }

        return current;
    }

    private double[] matrixVectorMultiply(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }

    private double[] vectorAdd(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }
}

abstract class ActivationFunction {
    abstract double[] apply(double[] x);

    abstract double[] derivative(double[] x, double[] output);
}

class ReLUActivation extends ActivationFunction {
    public double[] apply(double[] x) {
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.max(0, x[i]);
        }
        return result;
    }

    public double[] derivative(double[] x, double[] output) {
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = x[i] > 0 ? 1.0 : 0.0;
        }
        return result;
    }
}

class SoftmaxActivation extends ActivationFunction {
    public double[] apply(double[] x) {
        double max = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }

        double sum = 0;
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.exp(x[i] - max);
            sum += result[i];
        }

        for (int i = 0; i < x.length; i++) {
            result[i] /= sum;
        }
        return result;
    }

    public double[] derivative(double[] x, double[] output) {
        return output;
    }
}
```

### 3. Activation Functions

```java
package com.ml.nn.activations;

public class ActivationFunctions {

    public static double sigmoid(double x) {
        if (x > 500) return 1.0;
        if (x < -500) return 0.0;
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        double s = sigmoid(x);
        return s * (1.0 - s);
    }

    public static double tanh(double x) {
        return Math.tanh(x);
    }

    public static double tanhDerivative(double x) {
        return 1.0 - Math.pow(Math.tanh(x), 2);
    }

    public static double relu(double x) {
        return Math.max(0, x);
    }

    public static double reluDerivative(double x) {
        return x > 0 ? 1.0 : 0.0;
    }

    public static double leakyRelu(double x, double alpha) {
        return x > 0 ? x : alpha * x;
    }

    public static double leakyReluDerivative(double x, double alpha) {
        return x > 0 ? 1.0 : alpha;
    }

    public static double elu(double x, double alpha) {
        return x > 0 ? x : alpha * (Math.exp(x) - 1);
    }

    public static double eluDerivative(double x, double alpha) {
        return x > 0 ? 1.0 : elu(x, alpha) + alpha;
    }

    public static double[] softmax(double[] x) {
        double max = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }

        double sum = 0;
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.exp(x[i] - max);
            sum += result[i];
        }

        for (int i = 0; i < x.length; i++) {
            result[i] /= sum;
        }
        return result;
    }

    public static double swish(double x) {
        return x * sigmoid(x);
    }

    public static double swishDerivative(double x) {
        return sigmoid(x) + x * sigmoid(x) * (1 - sigmoid(x));
    }

    public static double[] applyActivation(double[] inputs, ActivationType type) {
        double[] output = new double[inputs.length];
        switch (type) {
            case SIGMOID:
                for (int i = 0; i < inputs.length; i++) {
                    output[i] = sigmoid(inputs[i]);
                }
                break;
            case TANH:
                for (int i = 0; i < inputs.length; i++) {
                    output[i] = tanh(inputs[i]);
                }
                break;
            case RELU:
                for (int i = 0; i < inputs.length; i++) {
                    output[i] = relu(inputs[i]);
                }
                break;
            case LEAKY_RELU:
                for (int i = 0; i < inputs.length; i++) {
                    output[i] = leakyRelu(inputs[i], 0.01);
                }
                break;
            case SOFTMAX:
                return softmax(inputs);
        }
        return output;
    }

    enum ActivationType {
        SIGMOID, TANH, RELU, LEAKY_RELU, SOFTMAX
    }
}
```

### 4. Loss Functions

```java
package com.ml.nn.loss;

import java.util.Arrays;

public class LossFunctions {

    public static double mse(double[] predictions, double[] targets) {
        double sum = 0;
        for (int i = 0; i < predictions.length; i++) {
            double diff = predictions[i] - targets[i];
            sum += diff * diff;
        }
        return sum / predictions.length;
    }

    public static double[] mseGradient(double[] predictions, double[] targets) {
        double[] gradient = new double[predictions.length];
        for (int i = 0; i < predictions.length; i++) {
            gradient[i] = 2.0 * (predictions[i] - targets[i]) / predictions.length;
        }
        return gradient;
    }

    public static double crossEntropy(double[] predictions, int targetIndex) {
        double logLikelihood = -Math.log(predictions[targetIndex] + 1e-10);
        return logLikelihood;
    }

    public static double crossEntropyBatch(double[][] predictions, int[] targetIndices) {
        double totalLoss = 0;
        for (int i = 0; i < predictions.length; i++) {
            totalLoss += crossEntropy(predictions[i], targetIndices[i]);
        }
        return totalLoss / predictions.length;
    }

    public static double[] crossEntropyGradient(double[] predictions, int targetIndex) {
        double[] gradient = predictions.clone();
        gradient[targetIndex] -= 1.0;
        return gradient;
    }

    public static double hingeLoss(double[] predictions, int targetIndex) {
        double maxLoss = 0;
        for (int i = 0; i < predictions.length; i++) {
            if (i != targetIndex) {
                double loss = 1.0 - predictions[targetIndex] + predictions[i];
                if (loss > maxLoss) maxLoss = loss;
            }
        }
        return Math.max(0, maxLoss);
    }

    public static double categoricalCrossEntropy(double[] predictions, double[] targets) {
        double loss = 0;
        for (int i = 0; i < predictions.length; i++) {
            loss -= targets[i] * Math.log(predictions[i] + 1e-10);
        }
        return loss;
    }

    public static double binaryCrossEntropy(double prediction, double target) {
        return -target * Math.log(prediction + 1e-10)
             - (1 - target) * Math.log(1 - prediction + 1e-10);
    }

    public static double huberLoss(double[] predictions, double[] targets, double delta) {
        double totalLoss = 0;
        for (int i = 0; i < predictions.length; i++) {
            double error = Math.abs(predictions[i] - targets[i]);
            if (error <= delta) {
                totalLoss += 0.5 * error * error;
            } else {
                totalLoss += delta * (error - 0.5 * delta);
            }
        }
        return totalLoss / predictions.length;
    }
}
```

### 5. Weight Initialization

```java
package com.ml.nn.init;

import java.util.Random;

public class WeightInitializers {

    public static double[][] xavierInitialization(int rows, int cols, Random random) {
        double limit = Math.sqrt(2.0 / (rows + cols));
        double[][] weights = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                weights[i][j] = random.nextGaussian() * limit;
            }
        }
        return weights;
    }

    public static double[][] heInitialization(int rows, int cols, Random random) {
        double std = Math.sqrt(2.0 / rows);
        double[][] weights = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                weights[i][j] = random.nextGaussian() * std;
            }
        }
        return weights;
    }

    public static double[][] leCunInitialization(int rows, int cols, Random random) {
        double std = Math.sqrt(1.0 / rows);
        double[][] weights = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                weights[i][j] = random.nextGaussian() * std;
            }
        }
        return weights;
    }

    public static double[][] uniformInitialization(int rows, int cols, Random random, double limit) {
        double[][] weights = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                weights[i][j] = random.nextDouble() * 2 * limit - limit;
            }
        }
        return weights;
    }

    public static double[] initializeBiases(int size, double initValue) {
        double[] biases = new double[size];
        Arrays.fill(biases, initValue);
        return biases;
    }
}

class Arrays {
    public static void fill(double[] array, double value) {
        for (int i = 0; i < array.length; i++) {
            array[i] = value;
        }
    }
}
```

### 6. Forward Propagation with Batch Support

```java
package com.ml.nn;

import java.util.ArrayList;
import java.util.List;

public class ForwardPropagation {

    public static class LayerCache {
        public double[][] Z;
        public double[][] A;
        public double[][] A_prev;

        public LayerCache(int batchSize, int outputSize) {
            this.Z = new double[batchSize][outputSize];
            this.A = new double[batchSize][outputSize];
        }
    }

    public static List<LayerCache> forward(List<double[][]> weights,
                                           List<double[]> biases,
                                           double[][] inputBatch,
                                           List<ActivationFunction> activations) {
        List<LayerCache> caches = new ArrayList<>();
        double[][] current = inputBatch;

        for (int l = 0; l < weights.size(); l++) {
            LayerCache cache = new LayerCache(inputBatch.length, weights.get(l).length);

            for (int i = 0; i < inputBatch.length; i++) {
                for (int j = 0; j < weights.get(l).length; j++) {
                    double sum = biases.get(l)[j];
                    for (int k = 0; k < current[0].length; k++) {
                        sum += weights.get(l)[j][k] * current[i][k];
                    }
                    cache.Z[i][j] = sum;
                }
            }

            cache.A_prev = current;
            for (int i = 0; i < inputBatch.length; i++) {
                cache.A[i] = activations.get(l).apply(cache.Z[i]);
            }

            current = cache.A;
            caches.add(cache);
        }

        return caches;
    }

    public static double[] forwardSingleInput(List<double[][]> weights,
                                              List<double[]> biases,
                                              double[] input,
                                              List<ActivationFunction> activations) {
        double[] current = input;

        for (int l = 0; l < weights.size(); l++) {
            double[] z = new double[weights.get(l).length];

            for (int j = 0; j < weights.get(l).length; j++) {
                z[j] = biases.get(l)[j];
                for (int k = 0; k < current.length; k++) {
                    z[j] += weights.get(l)[j][k] * current[k];
                }
            }

            current = activations.get(l).apply(z);
        }

        return current;
    }
}
```