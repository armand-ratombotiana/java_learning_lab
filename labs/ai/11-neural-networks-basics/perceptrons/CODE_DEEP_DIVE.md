# Perceptron Code Deep Dive

This lab provides a pure Java implementation of a single-layer Perceptron, trained to solve the logical AND problem.

## 💻 Pure Java Implementation

```java file="labs/ai/11-neural-networks-basics/perceptrons/SOLUTION/Perceptron.java"
package ai.neuralnetworks.perceptron;

import java.util.Arrays;
import java.util.Random;

/**
 * A fundamental implementation of a single-layer Perceptron.
 */
public class Perceptron {

    private double[] weights;
    private double bias;
    private final double learningRate;

    public Perceptron(int inputSize, double learningRate) {
        this.learningRate = learningRate;
        this.weights = new double[inputSize];
        
        // Initialize weights and bias with small random numbers
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < inputSize; i++) {
            this.weights[i] = random.nextDouble() - 0.5;
        }
        this.bias = random.nextDouble() - 0.5;
    }

    /**
     * The Forward Pass: Calculates the weighted sum and applies the Step Function.
     */
    public int predict(double[] inputs) {
        if (inputs.length != weights.length) {
            throw new IllegalArgumentException("Input size must match weight size.");
        }
        
        double sum = bias;
        for (int i = 0; i < inputs.length; i++) {
            sum += inputs[i] * weights[i];
        }
        
        // Step Activation Function
        return (sum >= 0) ? 1 : 0;
    }

    /**
     * Trains the Perceptron using the Perceptron Learning Rule.
     */
    public void train(double[][] trainingInputs, int[] labels, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            int errorsInEpoch = 0;
            
            for (int i = 0; i < trainingInputs.length; i++) {
                double[] x = trainingInputs[i];
                int yTrue = labels[i];
                
                int yPred = predict(x);
                int error = yTrue - yPred;
                
                if (error != 0) {
                    errorsInEpoch++;
                    // Update weights
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += learningRate * error * x[j];
                    }
                    // Update bias
                    bias += learningRate * error;
                }
            }
            
            System.out.println("Epoch " + epoch + " - Errors: " + errorsInEpoch);
            if (errorsInEpoch == 0) {
                System.out.println("Converged in " + epoch + " epochs.");
                break;
            }
        }
    }

    public static void main(String[] args) {
        // Training Data for Logical AND
        double[][] inputs = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
        };
        int[] labels = {0, 0, 0, 1}; // Only true when both inputs are 1

        Perceptron p = new Perceptron(2, 0.1);
        System.out.println("Initial Weights: " + Arrays.toString(p.weights) + ", Bias: " + p.bias);
        
        p.train(inputs, labels, 20);
        
        System.out.println("\nFinal Weights: " + Arrays.toString(p.weights) + ", Bias: " + p.bias);
        
        // Test predictions
        System.out.println("\nTesting Predictions:");
        for (double[] x : inputs) {
            System.out.println(Arrays.toString(x) + " -> " + p.predict(x));
        }
    }
}
```

## 🔍 Key Takeaways
1. **Convergence**: Because the logical AND problem is linearly separable, the Perceptron is guaranteed to converge (reach 0 errors). If you try to train this exact code on the XOR problem, it will loop indefinitely until the max epochs are reached, because convergence is mathematically impossible.
2. **The Update Rule**: Notice how simple the update rule is: `weights[j] += learningRate * error * x[j]`. This is the fundamental mechanism of machine learning—adjusting parameters based on the magnitude and direction of the error.