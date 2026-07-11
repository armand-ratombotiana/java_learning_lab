# Backpropagation Code Deep Dive

This lab provides a pure Java implementation of a Multi-Layer Perceptron (MLP) with one hidden layer, trained using Backpropagation to solve the XOR problem.

## 💻 Pure Java Implementation

```java file="labs/ai/12-backpropagation/SOLUTION/MLP.java"
package ai.neuralnetworks.backprop;

import java.util.Random;

/**
 * A Multi-Layer Perceptron (MLP) with 1 hidden layer trained via Backpropagation.
 * Solves the non-linear XOR problem.
 */
public class MLP {

    // Architecture
    private final int inputNodes;
    private final int hiddenNodes;
    private final int outputNodes;

    // Weights
    private double[][] weightsIH; // Input to Hidden
    private double[][] weightsHO; // Hidden to Output

    // Biases
    private double[] biasH;
    private double[] biasO;

    private final double learningRate;
    private final Random random = new Random(42);

    public MLP(int inputNodes, int hiddenNodes, int outputNodes, double learningRate) {
        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;
        this.learningRate = learningRate;

        // Initialize weights and biases randomly between -1 and 1
        weightsIH = new double[hiddenNodes][inputNodes];
        weightsHO = new double[outputNodes][hiddenNodes];
        biasH = new double[hiddenNodes];
        biasO = new double[outputNodes];

        initMatrix(weightsIH);
        initMatrix(weightsHO);
        initArray(biasH);
        initArray(biasO);
    }

    // --- Math Helpers ---
    private double sigmoid(double x) { return 1 / (1 + Math.exp(-x)); }
    private double dSigmoid(double y) { return y * (1 - y); } // Derivative of sigmoid (requires output of sigmoid)

    private void initMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                m[i][j] = random.nextDouble() * 2 - 1;
    }

    private void initArray(double[] a) {
        for (int i = 0; i < a.length; i++)
            a[i] = random.nextDouble() * 2 - 1;
    }

    /**
     * Trains the network using a single input/target pair.
     */
    public void train(double[] inputs, double[] targets) {
        
        // ==========================================
        // 1. FORWARD PASS
        // ==========================================
        
        // Calculate Hidden Layer Activations
        double[] hidden = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = biasH[i];
            for (int j = 0; j < inputNodes; j++) {
                sum += inputs[j] * weightsIH[i][j];
            }
            hidden[i] = sigmoid(sum);
        }

        // Calculate Output Layer Activations
        double[] outputs = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            double sum = biasO[i];
            for (int j = 0; j < hiddenNodes; j++) {
                sum += hidden[j] * weightsHO[i][j];
            }
            outputs[i] = sigmoid(sum);
        }

        // ==========================================
        // 2. BACKWARD PASS (BACKPROPAGATION)
        // ==========================================

        // Calculate Output Layer Errors (Target - Output)
        double[] outputErrors = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            outputErrors[i] = targets[i] - outputs[i];
        }

        // Calculate Gradients for Hidden-to-Output Weights
        // Gradient = error * dSigmoid(output) * hidden_activation
        for (int i = 0; i < outputNodes; i++) {
            double gradient = outputErrors[i] * dSigmoid(outputs[i]) * learningRate;
            
            // Adjust Biases
            biasO[i] += gradient;
            
            // Adjust Weights
            for (int j = 0; j < hiddenNodes; j++) {
                weightsHO[i][j] += gradient * hidden[j];
            }
        }

        // Calculate Hidden Layer Errors
        // Error = sum(weight_to_output * output_error)
        double[] hiddenErrors = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = 0;
            for (int j = 0; j < outputNodes; j++) {
                sum += weightsHO[j][i] * outputErrors[j];
            }
            hiddenErrors[i] = sum;
        }

        // Calculate Gradients for Input-to-Hidden Weights
        for (int i = 0; i < hiddenNodes; i++) {
            double gradient = hiddenErrors[i] * dSigmoid(hidden[i]) * learningRate;
            
            biasH[i] += gradient;
            
            for (int j = 0; j < inputNodes; j++) {
                weightsIH[i][j] += gradient * inputs[j];
            }
        }
    }

    public double[] predict(double[] inputs) {
        // Forward pass only
        double[] hidden = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = biasH[i];
            for (int j = 0; j < inputNodes; j++) {
                sum += inputs[j] * weightsIH[i][j];
            }
            hidden[i] = sigmoid(sum);
        }

        double[] outputs = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            double sum = biasO[i];
            for (int j = 0; j < hiddenNodes; j++) {
                sum += hidden[j] * weightsHO[i][j];
            }
            outputs[i] = sigmoid(sum);
        }
        return outputs;
    }

    public static void main(String[] args) {
        // XOR Training Data
        double[][] trainingInputs = {
            {0, 0}, {0, 1}, {1, 0}, {1, 1}
        };
        double[][] trainingTargets = {
            {0}, {1}, {1}, {0}
        };

        // Network: 2 inputs, 4 hidden nodes, 1 output, learning rate 0.1
        MLP nn = new MLP(2, 4, 1, 0.1);

        // Train for 10,000 epochs
        for (int epoch = 0; epoch < 10000; epoch++) {
            // Pick a random sample
            int index = nn.random.nextInt(4);
            nn.train(trainingInputs[index], trainingTargets[index]);
        }

        // Test the trained network
        System.out.println("Testing XOR Predictions:");
        for (int i = 0; i < trainingInputs.length; i++) {
            double[] prediction = nn.predict(trainingInputs[i]);
            System.out.printf("[%d, %d] -> %.4f%n", 
                (int)trainingInputs[i][0], (int)trainingInputs[i][1], prediction[0]);
        }
    }
}
```

## 🔍 Key Takeaways
1. **The Chain Rule in Code**: Look at the line `double gradient = outputErrors[i] * dSigmoid(outputs[i]) * learningRate;`. This is the exact implementation of the mathematical chain rule derived in `MATH_FOUNDATION.md`. It multiplies the loss derivative (error) by the activation derivative (`dSigmoid`).
2. **Hidden Errors**: Notice how `hiddenErrors` are calculated. We don't have true labels for the hidden layer. Instead, we take the errors from the output layer and push them backwards, weighted by the connections (`weightsHO[j][i] * outputErrors[j]`). This is the "backward propagation of errors" in action.
3. **Solving XOR**: Unlike the single Perceptron, this MLP successfully solves the XOR problem because the hidden layer learns to transform the input space into a new, linearly separable space.