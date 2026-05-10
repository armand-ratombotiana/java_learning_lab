package com.learning.deeplearning;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running Deep Learning Tests\n");

        testNeuralNetworkCreation();
        testForwardPass();
        testCNNConstruction();
        testLSTMNetwork();
        testDataNormalization();
        testOneHotEncoding();
        testModelPrediction();

        System.out.println("\nAll tests passed!");
    }

    private static void testNeuralNetworkCreation() {
        System.out.println("Test: Neural Network Creation");
        Solution.NeuralNetworkExample nnExample = new Solution.NeuralNetworkExample();

        MultiLayerNetwork network = nnExample.createSimpleFeedForwardNetwork(784, 10);

        assert network != null : "Network should not be null";
        assert network.getnLayers() == 3 : "Network should have 3 layers";
        System.out.println("  - Feed-forward network created with 3 layers");
    }

    private static void testForwardPass() {
        System.out.println("Test: Forward Pass");
        Solution.NeuralNetworkExample nnExample = new Solution.NeuralNetworkExample();
        MultiLayerNetwork network = nnExample.createSimpleFeedForwardNetwork(10, 3);

        INDArray input = Nd4j.create(5, 10);
        INDArray output = nnExample.forwardPass(network, input);

        assert output != null : "Output should not be null";
        assert output.shape()[0] == 5 : "Output batch size should match input";
        assert output.shape()[1] == 3 : "Output classes should match";
        System.out.println("  - Forward pass completed, output shape: " + output.shape()[0] + "x" + output.shape()[1]);
    }

    private static void testCNNConstruction() {
        System.out.println("Test: CNN Construction");
        Solution.ConvolutionalNeuralNetworkExample cnnExample = new Solution.ConvolutionalNeuralNetworkExample();

        MultiLayerNetwork cnn = cnnExample.createCNN(28, 28, 1, 10);

        assert cnn != null : "CNN should not be null";
        System.out.println("  - CNN created for 28x28 grayscale images with 10 classes");
    }

    private static void testLSTMNetwork() {
        System.out.println("Test: LSTM Network");
        Solution.RecurrentNeuralNetworkExample rnnExample = new Solution.RecurrentNeuralNetworkExample();

        MultiLayerNetwork lstm = rnnExample.createLSTMNetwork(2, 32, 2, 100);

        assert lstm != null : "LSTM should not be null";
        System.out.println("  - LSTM network created with 32 hidden units");
    }

    private static void testDataNormalization() {
        System.out.println("Test: Data Normalization");
        Solution.DataProcessingExample dataProc = new Solution.DataProcessingExample();

        INDArray data = Nd4j.create(new double[][] {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        });

        INDArray normalized = dataProc.normalizeData(data);

        assert normalized != null : "Normalized data should not be null";
        assert normalized.shape()[0] == 3 : "Row count should match";
        assert normalized.shape()[1] == 3 : "Column count should match";
        System.out.println("  - Data normalized successfully");
    }

    private static void testOneHotEncoding() {
        System.out.println("Test: One-Hot Encoding");
        Solution.DataProcessingExample dataProc = new Solution.DataProcessingExample();

        INDArray labels = Nd4j.create(new double[]{0, 1, 2, 1, 0});
        INDArray oneHot = dataProc.oneHotEncode(labels, 3);

        assert oneHot.shape()[0] == 5 : "Should have 5 samples";
        assert oneHot.shape()[1] == 3 : "Should have 3 classes";
        assert oneHot.getDouble(0, 0) == 1.0 : "First sample should be class 0";
        assert oneHot.getDouble(1, 1) == 1.0 : "Second sample should be class 1";
        System.out.println("  - One-hot encoding created 5x3 matrix");
    }

    private static void testModelPrediction() {
        System.out.println("Test: Model Prediction");
        Solution.NeuralNetworkExample nnExample = new Solution.NeuralNetworkExample();
        MultiLayerNetwork network = nnExample.createSimpleFeedForwardNetwork(10, 3);

        INDArray input = Nd4j.create(1, 10);
        INDArray output = network.output(input);

        int predictedClass = output.argMax().getInt();

        assert predictedClass >= 0 && predictedClass < 3 : "Predicted class should be in range [0, 2]";
        System.out.println("  - Prediction made, class: " + predictedClass);
    }
}