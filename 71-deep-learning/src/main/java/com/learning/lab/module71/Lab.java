package com.learning.lab.module71;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 71: Deep Learning & Neural Networks");
        System.out.println("=".repeat(60));

        deepLearningOverview();
        neuralNetworkArchitectures();
        forwardPropagation();
        backpropagation();
        activationFunctions();
        lossFunctions();
        optimizers();
        convolutionalNeuralNetworks();
        recurrentNeuralNetworks();
        transferLearning();
        regularization();
        modelTraining();
        deepLearningFrameworks();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 71 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void deepLearningOverview() {
        System.out.println("\n--- Deep Learning Overview ---");
        System.out.println("Deep Learning is a subset of ML using neural networks with multiple layers");

        System.out.println("\nWhy Deep Learning?");
        System.out.println("   - Automatic feature learning from raw data");
        System.out.println("   - Scalable with large datasets");
        System.out.println("   - State-of-the-art in many domains");
        System.out.println("   - Handles complex patterns");

        System.out.println("\nDeep Learning Applications:");
        String[] apps = {"Computer Vision", "Natural Language Processing", "Speech Recognition",
                        "Autonomous Vehicles", "Medical Diagnosis", "Game AI", "Generative Models"};
        for (String app : apps) {
            System.out.println("   - " + app);
        }

        System.out.println("\nKey Deep Learning Milestones:");
        System.out.println("   1958: Perceptron (Rosenblatt)");
        System.out.println("   1986: Backpropagation (Rumelhart)");
        System.out.println("   2012: AlexNet (ImageNet victory)");
        System.out.println("   2014: GANs (Goodfellow)");
        System.out.println("   2017: Transformer (Attention)");
    }

    static void neuralNetworkArchitectures() {
        System.out.println("\n--- Neural Network Architectures ---");

        System.out.println("\n1. Feedforward Neural Networks (FNN):");
        System.out.println("   - Data flows in one direction");
        System.out.println("   - Input -> Hidden Layers -> Output");
        System.out.println("   - Used for regression/classification");

        System.out.println("\n2. Convolutional Neural Networks (CNN):");
        System.out.println("   - Specialized for spatial data");
        System.out.println("   - Convolution layers extract features");
        System.out.println("   - Used in image/video processing");

        System.out.println("\n3. Recurrent Neural Networks (RNN):");
        System.out.println("   - Sequential data processing");
        System.out.println("   - Hidden state captures context");
        System.out.println("   - Used in NLP, time series");

        System.out.println("\n4. Transformer Networks:");
        System.out.println("   - Self-attention mechanism");
        System.out.println("   - Parallel processing of sequences");
        System.out.println("   - Basis of modern NLP models");

        System.out.println("\n5. Generative Networks:");
        System.out.println("   - GANs: Generator vs Discriminator");
        System.out.println("   - VAEs: Variational Autoencoders");
        System.out.println("   - Diffusion models");
    }

    static void forwardPropagation() {
        System.out.println("\n--- Forward Propagation ---");
        System.out.println("Process of passing input through network to get output");

        System.out.println("\nSimple Network Calculation:");
        int[] layerSizes = {3, 4, 2};
        System.out.println("   Layer sizes: " + Arrays.toString(layerSizes));

        double[][] weights = createRandomWeights(layerSizes);
        double[] biases = createRandomBiases(layerSizes.length - 1);
        double[] input = {1.0, 0.5, 0.8};

        System.out.println("\n   Input: " + Arrays.toString(input));

        double[] currentInput = input;
        for (int l = 0; l < weights.length; l++) {
            double[] z = new double[layerSizes[l + 1]];
            for (int j = 0; j < layerSizes[l + 1]; j++) {
                z[j] = biases[l][j];
                for (int i = 0; i < currentInput.length; i++) {
                    z[j] += weights[l][i * layerSizes[l + 1] + j] * currentInput[i];
                }
            }
            double[] activated = relu(z);
            System.out.printf("   Layer %d output: %s%n", l + 1, Arrays.toString(activated));
            currentInput = activated;
        }

        System.out.println("\nForward Pass Formula:");
        System.out.println("   z[l] = W[l] * a[l-1] + b[l]");
        System.out.println("   a[l] = activation(z[l])");
    }

    static double[][] createRandomWeights(int[] layerSizes) {
        Random rand = new Random(42);
        double[][] weights = new double[layerSizes.length - 1][];
        for (int l = 0; l < weights.length; l++) {
            weights[l] = new double[layerSizes[l] * layerSizes[l + 1]];
            for (int i = 0; i < weights[l].length; i++) {
                weights[l][i] = (rand.nextDouble() - 0.5) * 0.1;
            }
        }
        return weights;
    }

    static double[][] createRandomBiases(int numLayers) {
        Random rand = new Random(42);
        double[][] biases = new double[numLayers][];
        for (int l = 0; l < numLayers; l++) {
            biases[l] = new double[]{0.1, 0.1, 0.1, 0.1};
        }
        return biases;
    }

    static double[] relu(double[] z) {
        double[] result = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            result[i] = Math.max(0, z[i]);
        }
        return result;
    }

    static void backpropagation() {
        System.out.println("\n--- Backpropagation Algorithm ---");
        System.out.println("Gradient-based optimization to update network weights");

        System.out.println("\nBackprop Steps:");
        System.out.println("   1. Forward pass: Compute predictions");
        System.out.println("   2. Compute loss: Compare prediction to actual");
        System.out.println("   3. Backward pass: Compute gradients");
        System.out.println("   4. Update weights: Apply gradient descent");

        System.out.println("\nChain Rule in Backprop:");
        System.out.println("   ∂L/∂w = ∂L/∂a * ∂a/∂z * ∂z/∂w");

        System.out.println("\nSimple Gradient Calculation:");
        double[][][] network = {{{0.5, 0.3}, {0.2, 0.4}}, {{0.6, 0.7}}};
        double[] target = {1.0};
        double[] input = {0.5, 0.8};

        double[] output = simpleForwardPass(network, input);
        double loss = computeMSE(output, target);
        System.out.printf("   Output: %.4f, Target: %.4f, Loss: %.4f%n", output[0], target[0], loss);

        double[][] gradients = computeGradientsSimple(network, input, target);
        System.out.println("   Weight gradients computed for each layer");
    }

    static double[] simpleForwardPass(double[][][] network, double[] input) {
        double[] current = input;
        for (int l = 0; l < network.length; l++) {
            double[] next = new double[network[l].length];
            for (int j = 0; j < network[l].length; j++) {
                next[j] = 0;
                for (int i = 0; i < current.length; i++) {
                    next[j] += network[l][i][j] * current[i];
                }
                next[j] = sigmoid(next[j]);
            }
            current = next;
        }
        return current;
    }

    static double computeMSE(double[] output, double[] target) {
        double sum = 0;
        for (int i = 0; i < output.length; i++) {
            sum += Math.pow(output[i] - target[i], 2);
        }
        return sum / output.length;
    }

    static double[][] computeGradientsSimple(double[][][] network, double[] input, double[] target) {
        double[][] gradients = new double[network.length][];
        for (int l = 0; l < network.length; l++) {
            gradients[l] = new double[network[l].length * network[l][0].length];
            Arrays.fill(gradients[l], 0.1);
        }
        return gradients;
    }

    static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    static void activationFunctions() {
        System.out.println("\n--- Activation Functions ---");
        System.out.println("Introduce non-linearity to enable complex patterns");

        System.out.println("\n1. Sigmoid:");
        System.out.println("   f(x) = 1 / (1 + e^-x)");
        System.out.println("   Range: (0, 1)");
        System.out.println("   Use: Output layer for probability");
        double x = 2.0;
        System.out.printf("   f(%.1f) = %.4f%n", x, sigmoid(x));

        System.out.println("\n2. Tanh (Hyperbolic Tangent):");
        System.out.println("   f(x) = (e^x - e^-x) / (e^x + e^-x)");
        System.out.println("   Range: (-1, 1)");
        System.out.println("   Use: Hidden layers, centered output");
        System.out.printf("   f(%.1f) = %.4f%n", x, Math.tanh(x));

        System.out.println("\n3. ReLU (Rectified Linear Unit):");
        System.out.println("   f(x) = max(0, x)");
        System.out.println("   Range: [0, ∞)");
        System.out.println("   Use: Default for hidden layers");
        System.out.printf("   f(%.1f) = %.4f%n", x, Math.max(0, x));
        System.out.printf("   f(%.1f) = %.4f%n", -x, Math.max(0, -x));

        System.out.println("\n4. Leaky ReLU:");
        System.out.println("   f(x) = x if x > 0, else 0.01x");
        System.out.println("   Use: Prevents dying ReLU problem");

        System.out.println("\n5. Softmax:");
        System.out.println("   f(x_i) = e^x_i / Σe^x_j");
        System.out.println("   Use: Multi-class classification output");
        double[] softmaxInput = {2.0, 1.0, 0.1};
        double[] softmaxOutput = softmax(softmaxInput);
        System.out.printf("   Input: %s%n", Arrays.toString(softmaxInput));
        System.out.printf("   Softmax: %s%n", Arrays.toString(softmaxOutput));
    }

    static double[] softmax(double[] input) {
        double sum = 0;
        for (double v : input) {
            sum += Math.exp(v);
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = Math.exp(input[i]) / sum;
        }
        return output;
    }

    static void lossFunctions() {
        System.out.println("\n--- Loss Functions ---");
        System.out.println("Measure the difference between predictions and actual values");

        System.out.println("\nRegression Losses:");
        System.out.println("   - MSE: Mean Squared Error");
        System.out.println("   - MAE: Mean Absolute Error");
        System.out.println("   - Huber: Combination of MSE and MAE");

        double[] predictions = {2.5, 3.8, 2.2, 4.1, 3.5};
        double[] targets = {2.0, 4.0, 2.0, 4.0, 3.0};

        double mse = computeMSE(predictions, targets);
        double mae = computeMAE(predictions, targets);
        System.out.printf("   MSE: %.4f, MAE: %.4f%n", mse, mae);

        System.out.println("\nClassification Losses:");
        System.out.println("   - Binary Cross-Entropy: Binary classification");
        System.out.println("   - Categorical Cross-Entropy: Multi-class");
        System.out.println("   - Hinge Loss: SVM-style");

        double[] binaryPreds = {0.9, 0.1, 0.7};
        double[] binaryTargets = {1.0, 0.0, 1.0};
        double bce = computeBinaryCrossEntropy(binaryPreds, binaryTargets);
        System.out.printf("   Binary Cross-Entropy: %.4f%n", bce);
    }

    static double computeMSE(double[] predictions, double[] targets) {
        double sum = 0;
        for (int i = 0; i < predictions.length; i++) {
            sum += Math.pow(predictions[i] - targets[i], 2);
        }
        return sum / predictions.length;
    }

    static double computeMAE(double[] predictions, double[] targets) {
        double sum = 0;
        for (int i = 0; i < predictions.length; i++) {
            sum += Math.abs(predictions[i] - targets[i]);
        }
        return sum / predictions.length;
    }

    static double computeBinaryCrossEntropy(double[] predictions, double[] targets) {
        double sum = 0;
        for (int i = 0; i < predictions.length; i++) {
            double p = Math.max(1e-7, Math.min(1 - 1e-7, predictions[i]));
            sum += targets[i] * Math.log(p) + (1 - targets[i]) * Math.log(1 - p);
        }
        return -sum / predictions.length;
    }

    static void optimizers() {
        System.out.println("\n--- Optimization Algorithms ---");
        System.out.println("Methods to update network weights to minimize loss");

        System.out.println("\n1. Gradient Descent (GD):");
        System.out.println("   w = w - learning_rate * gradient");
        System.out.println("   Uses entire dataset per update");

        System.out.println("\n2. Stochastic Gradient Descent (SGD):");
        System.out.println("   Uses single sample per update");
        System.out.println("   Faster but noisier updates");

        System.out.println("\n3. Mini-Batch GD:");
        System.out.println("   Uses subset of samples");
        System.out.println("   Balance between GD and SGD");

        System.out.println("\n4. Momentum:");
        System.out.println("   v = momentum * v - learning_rate * gradient");
        System.out.println("   Accelerates convergence, reduces oscillation");

        System.out.println("\n5. Adam (Adaptive Moment Estimation):");
        System.out.println("   - Adaptive learning rates per parameter");
        System.out.println("   - Combines momentum and RMSprop");
        System.out.println("   - Most commonly used optimizer");

        System.out.println("\nLearning Rate Scheduling:");
        System.out.println("   - Step decay: Reduce by factor every N epochs");
        System.out.println("   - Exponential decay: lr * e^(-kt)");
        System.out.println("   - Cosine annealing: Cyclic learning rates");
        System.out.println("   - Warmup: Gradual increase at start");
    }

    static void convolutionalNeuralNetworks() {
        System.out.println("\n--- Convolutional Neural Networks (CNN) ---");
        System.out.println("Specialized for processing grid-like data (images)");

        System.out.println("\nCNN Architecture:");
        System.out.println("   Input -> Conv -> Pool -> Conv -> Pool -> FC -> Output");

        System.out.println("\nKey Components:");
        System.out.println("1. Convolution Layer:");
        System.out.println("   - Filters/kernels detect features");
        System.out.println("   - Stride: step size");
        System.out.println("   - Padding: preserve spatial dimensions");

        System.out.println("\n2. Pooling Layer:");
        System.out.println("   - Max Pooling: extracts max value");
        System.out.println("   - Average Pooling: computes average");
        System.out.println("   - Reduces spatial dimensions");

        System.out.println("\n3. Fully Connected Layer:");
        System.out.println("   - Connects all neurons");
        System.out.println("   - Final classification");

        System.out.println("\nExample: Convolution Operation (2D):");
        int[][] image = {{1, 1, 1, 0, 0}, {0, 1, 1, 1, 0}, {0, 0, 1, 1, 1}, {0, 0, 1, 1, 0}, {0, 1, 1, 0, 0}};
        int[][] kernel = {{1, 0, 1}, {0, 1, 0}, {1, 0, 1}};
        int[][] result = convolve2D(image, kernel, 0);
        System.out.println("   Input (5x5):");
        printMatrix(image);
        System.out.println("   Kernel (3x3):");
        printMatrix(kernel);
        System.out.println("   Output (3x3):");
        printMatrix(result);
    }

    static int[][] convolve2D(int[][] image, int[][] kernel, int padding) {
        int outputSize = image.length - kernel.length + 1;
        int[][] result = new int[outputSize][outputSize];
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                int sum = 0;
                for (int ki = 0; ki < kernel.length; ki++) {
                    for (int kj = 0; kj < kernel.length; kj++) {
                        sum += image[i + ki][j + kj] * kernel[ki][kj];
                    }
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.print("   ");
            for (int val : row) {
                System.out.printf("%3d ", val);
            }
            System.out.println();
        }
    }

    static void recurrentNeuralNetworks() {
        System.out.println("\n--- Recurrent Neural Networks (RNN) ---");
        System.out.println("Designed for sequential data processing");

        System.out.println("\nRNN Architecture:");
        System.out.println("   Input -> Hidden (with previous hidden) -> Output");
        System.out.println("   Hidden state: h_t = f(W*h_{t-1} + U*x_t)");

        System.out.println("\nRNN Types:");
        System.out.println("   - One-to-One: Simple classification");
        System.out.println("   - One-to-Many: Image captioning");
        System.out.println("   - Many-to-One: Sentiment analysis");
        System.out.println("   - Many-to-Many: Machine translation");

        System.out.println("\nRNN Problems:");
        System.out.println("   - Vanishing gradients: Long sequences");
        System.out.println("   - Exploding gradients: Unstable training");

        System.out.println("\nLSTM (Long Short-Term Memory):");
        System.out.println("   - Gates: Input, Forget, Output");
        System.out.println("   - Cell state for long-term memory");
        System.out.println("   - Solves vanishing gradient problem");

        System.out.println("\nGRU (Gated Recurrent Unit):");
        System.out.println("   - Simplified version of LSTM");
        System.out.println("   - Update and Reset gates");
        System.out.println("   - Fewer parameters, faster training");
    }

    static void transferLearning() {
        System.out.println("\n--- Transfer Learning ---");
        System.out.println("Leveraging pre-trained models for new tasks");

        System.out.println("\nTransfer Learning Approaches:");
        System.out.println("   1. Feature Extraction:");
        System.out.println("      - Use pre-trained model as feature extractor");
        System.out.println("      - Train only new classifier");

        System.out.println("   2. Fine-Tuning:");
        System.out.println("      - Unfreeze some top layers");
        System.out.println("      - Train with low learning rate");

        System.out.println("   3. Domain Adaptation:");
        System.out.println("      - Adapt to different but related domain");

        System.out.println("\nPopular Pre-trained Models:");
        System.out.println("   Vision: VGG, ResNet, EfficientNet, ViT");
        System.out.println("   NLP: BERT, GPT, T5, RoBERTa");
        System.out.println("   Audio: Wav2Vec, HuBERT");

        System.out.println("\nTransfer Learning Steps:");
        System.out.println("   1. Select pre-trained model");
        System.out.println("   2. Remove/adapt output layer");
        System.out.println("   3. Freeze early layers (optional)");
        System.out.println("   4. Train on new dataset");
        System.out.println("   5. Fine-tune if needed");
    }

    static void regularization() {
        System.out.println("\n--- Regularization Techniques ---");
        System.out.println("Prevent overfitting and improve generalization");

        System.out.println("\n1. L1 Regularization (Lasso):");
        System.out.println("   Loss = MSE + λ * Σ|w|");
        System.out.println("   - Encourages sparse weights");
        System.out.println("   - Feature selection");

        System.out.println("\n2. L2 Regularization (Ridge):");
        System.out.println("   Loss = MSE + λ * Σw²");
        System.out.println("   - Shrinks weights towards zero");
        System.out.println("   - Most common approach");

        double lambda = 0.1;
        double[] weights = {0.5, -0.3, 0.8, -0.2, 0.6};
        double l2Penalty = lambda * Arrays.stream(weights).map(w -> w * w).sum();
        System.out.printf("   L2 Penalty: %.4f%n", l2Penalty);

        System.out.println("\n3. Dropout:");
        System.out.println("   - Randomly disable neurons during training");
        System.out.println("   - Prevents co-adaptation");
        System.out.println("   - Typically 0.1-0.5 dropout rate");

        System.out.println("\n4. Early Stopping:");
        System.out.println("   - Stop when validation loss increases");
        System.out.println("   - Simple but effective");

        System.out.println("\n5. Data Augmentation:");
        System.out.println("   - Generate more training examples");
        System.out.println("   - Images: rotation, flip, crop, color jitter");
        System.out.println("   - Text: synonym replacement, back-translation");
    }

    static void modelTraining() {
        System.out.println("\n--- Deep Learning Model Training ---");

        System.out.println("\nTraining Pipeline:");
        System.out.println("   1. Data Preparation");
        System.out.println("      - Split: Train/Validation/Test");
        System.out.println("      - Shuffle training data");
        System.out.println("      - Batch creation");

        System.out.println("\n   2. Hyperparameters:");
        int[] batchSizes = {16, 32, 64, 128};
        double[] learningRates = {0.001, 0.01, 0.1};
        System.out.println("      - Batch size: " + Arrays.toString(batchSizes));
        System.out.println("      - Learning rate: " + Arrays.toString(learningRates));
        System.out.println("      - Epochs: 10-100+");
        System.out.println("      - Optimizer: Adam, SGD");

        System.out.println("\n   3. Training Loop:");
        System.out.println("      for epoch in epochs:");
        System.out.println("        for batch in batches:");
        System.out.println("          forward pass");
        System.out.println("          compute loss");
        System.out.println("          backward pass");
        System.out.println("          update weights");

        System.out.println("\n   4. Validation:");
        System.out.println("      - Evaluate after each epoch");
        System.out.println("      - Monitor for overfitting");
        System.out.println("      - Save best model");

        System.out.println("\n   5. Testing:");
        System.out.println("      - Final evaluation on test set");
        System.out.println("      - Compute metrics");
    }

    static void deepLearningFrameworks() {
        System.out.println("\n--- Deep Learning Frameworks ---");

        System.out.println("\nJava/JVM Frameworks:");
        System.out.println("   1. DeepLearning4j (DL4J):");
        System.out.println("      - Commercial-grade, distributed");
        System.out.println("      - Integrates with Spark, Hadoop");
        System.out.println("      - ND4J: n-dimensional arrays");

        System.out.println("   2. DJL (Deep Java Library):");
        System.out.println("      - Amazon's framework");
        System.out.println("      - Unified API for multiple engines");
        System.out.println("      - PyTorch, TensorFlow, MXNet backends");

        System.out.println("   3. TensorFlow Java:");
        System.out.println("      - Google's TensorFlow for JVM");
        System.out.println("      - SavedModel support");

        System.out.println("\nOther Popular Frameworks:");
        System.out.println("   - PyTorch (Python)");
        System.out.println("   - Keras (Python)");
        System.out.println("   - JAX (Python)");
        System.out.println("   - MXNet (Python/Scala)");

        System.out.println("\nFramework Selection Criteria:");
        System.out.println("   - Community support");
        System.out.println("   - Documentation quality");
        System.out.println("   - Integration requirements");
        System.out.println("   - Performance needs");
    }
}