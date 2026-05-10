package com.learning.deeplearning;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.MaxPooling2DLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public static class NeuralNetworkExample {

        public MultiLayerNetwork createSimpleFeedForwardNetwork(int inputSize, int outputSize) {
            MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new DenseLayer.Builder()
                    .nIn(inputSize)
                    .nOut(128)
                    .activation(Activation.RELU)
                    .build())
                .layer(1, new DenseLayer.Builder()
                    .nIn(128)
                    .nOut(64)
                    .activation(Activation.RELU)
                    .build())
                .layer(2, new OutputLayer.Builder()
                    .nIn(64)
                    .nOut(outputSize)
                    .activation(Activation.SOFTMAX)
                    .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .build())
                .build();

            MultiLayerNetwork model = new MultiLayerNetwork(config);
            model.init();
            model.setListeners(new ScoreIterationListener(100));
            return model;
        }

        public INDArray forwardPass(MultiLayerNetwork model, INDArray input) {
            return model.output(input);
        }

        public void trainNetwork(MultiLayerNetwork model, DataSetIterator iterator, int epochs) {
            for (int i = 0; i < epochs; i++) {
                model.fit(iterator);
            }
        }

        public double evaluateAccuracy(MultiLayerNetwork model, DataSetIterator iterator) {
            int correct = 0;
            int total = 0;
            while (iterator.hasNext()) {
                DataSet batch = iterator.next();
                INDArray predictions = model.output(batch.getFeatures());
                INDArray labels = batch.getLabels();
                for (int i = 0; i < predictions.rows(); i++) {
                    int predictedClass = predictions.getRow(i).argMax().getInt();
                    int actualClass = labels.getRow(i).argMax().getInt();
                    if (predictedClass == actualClass) {
                        correct++;
                    }
                    total++;
                }
            }
            return (double) correct / total;
        }
    }

    public static class ConvolutionalNeuralNetworkExample {

        public MultiLayerNetwork createCNN(int height, int width, int channels, int numClasses) {
            MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new ConvolutionLayer.Builder()
                    .nIn(channels)
                    .nOut(32)
                    .kernelSize(3, 3)
                    .stride(1, 1)
                    .padding(1, 1)
                    .activation(Activation.RELU)
                    .build())
                .layer(1, new MaxPooling2DLayer.Builder()
                    .kernelSize(2, 2)
                    .stride(2, 2)
                    .build())
                .layer(2, new ConvolutionLayer.Builder()
                    .nIn(32)
                    .nOut(64)
                    .kernelSize(3, 3)
                    .stride(1, 1)
                    .padding(1, 1)
                    .activation(Activation.RELU)
                    .build())
                .layer(3, new MaxPooling2DLayer.Builder()
                    .kernelSize(2, 2)
                    .stride(2, 2)
                    .build())
                .layer(4, new DenseLayer.Builder()
                    .nOut(256)
                    .activation(Activation.RELU)
                    .build())
                .layer(5, new OutputLayer.Builder()
                    .nIn(256)
                    .nOut(numClasses)
                    .activation(Activation.SOFTMAX)
                    .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .build())
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

            MultiLayerNetwork model = new MultiLayerNetwork(config);
            model.init();
            return model;
        }

        public INDArray preprocessImage(INDArray image) {
            INDArray normalized = image.div(255.0);
            INDArray meanCentered = normalized.sub(Nd4j.mean(normalized));
            return meanCentered;
        }

        public int[] getFeatureMapShape(MultiLayerNetwork model, int layerIndex) {
            return model.getLayer(layerIndex).getParam("W").shape();
        }
    }

    public static class RecurrentNeuralNetworkExample {

        public MultiLayerNetwork createLSTMNetwork(int inputSize, int hiddenSize, int outputSize, int timeSteps) {
            MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new LSTM.Builder()
                    .nIn(inputSize)
                    .nOut(hiddenSize)
                    .activation(Activation.TANH)
                    .build())
                .layer(1, new RnnOutputLayer.Builder()
                    .nIn(hiddenSize)
                    .nOut(outputSize)
                    .activation(Activation.SOFTMAX)
                    .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .build())
                .build();

            MultiLayerNetwork model = new MultiLayerNetwork(config);
            model.init();
            return model;
        }

        public INDArray createTimeSeriesData(int numSamples, int timeSteps, int features) {
            Random rand = new Random(42);
            INDArray featuresArray = Nd4j.create(numSamples, features, timeSteps);
            INDArray labelsArray = Nd4j.create(numSamples, 2, timeSteps);

            for (int i = 0; i < numSamples; i++) {
                double baseValue = rand.nextDouble();
                for (int t = 0; t < timeSteps; t++) {
                    featuresArray.putScalar(i, 0, t, baseValue + rand.nextDouble() * 0.3);
                    featuresArray.putScalar(i, 1, t, Math.sin(t * 0.1) + rand.nextDouble() * 0.2);
                    int label = (baseValue > 0.5) ? 1 : 0;
                    labelsArray.putScalar(i, label, t, 1.0);
                }
            }
            return Nd4j.concat(0, featuresArray, labelsArray);
        }

        public INDArray predictSequence(MultiLayerNetwork model, INDArray inputSequence) {
            return model.rnnOutput(inputSequence);
        }

        public INDArray getLastTimeStepOutput(MultiLayerNetwork model, INDArray input) {
            INDArray output = model.rnnOutput(input);
            return output.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(output.size(2) - 1));
        }
    }

    public static class ModelTrainingExample {

        public void trainWithEarlyStopping(MultiLayerNetwork model, DataSetIterator trainIterator,
                                           DataSetIterator validationIterator, int maxEpochs) {
            model.setListeners(new EarlyStoppingListener(validationIterator, 5));

            for (int epoch = 0; epoch < maxEpochs; epoch++) {
                model.fit(trainIterator);

                double trainScore = model.score();
                System.out.println("Epoch " + epoch + " - Train Score: " + trainScore);

                if (model.getListeners() instanceof EarlyStoppingListener) {
                    break;
                }
            }
        }

        public void evaluateModel(MultiLayerNetwork model, DataSetIterator testIterator) {
            ConfusionMatrix matrix = new ConfusionMatrix(10);

            while (testIterator.hasNext()) {
                DataSet batch = testIterator.next();
                INDArray predictions = model.output(batch.getFeatures());
                INDArray labels = batch.getLabels();

                for (int i = 0; i < predictions.rows(); i++) {
                    int predicted = predictions.getRow(i).argMax().getInt();
                    int actual = labels.getRow(i).argMax().getInt();
                    matrix.increment(actual, predicted);
                }
            }

            System.out.println("Accuracy: " + matrix.getAccuracy());
            System.out.println("Precision: " + matrix.getAveragePrecision());
            System.out.println("Recall: " + matrix.getAverageRecall());
            System.out.println("F1 Score: " + matrix.getAverageF1());
        }

        public void saveModel(MultiLayerNetwork model, String path) throws Exception {
            model.save(new File(path));
        }

        public MultiLayerNetwork loadModel(String path) throws Exception {
            return MultiLayerNetwork.load(new File(path), true);
        }
    }

    public static class DataProcessingExample {

        public INDArray normalizeData(INDArray data) {
            INDArray mean = data.mean(0);
            INDArray std = data.std(0);
            return data.sub(mean).div(std.add(1e-8));
        }

        public INDArray oneHotEncode(INDArray labels, int numClasses) {
            int numSamples = (int) labels.size(0);
            INDArray oneHot = Nd4j.zeros(numSamples, numClasses);
            for (int i = 0; i < numSamples; i++) {
                int label = (int) labels.getDouble(i);
                oneHot.putScalar(i, label, 1.0);
            }
            return oneHot;
        }

        public DataSetIterator createBatchIterator(List<INDArray> features, List<INDArray> labels, int batchSize) {
            DataSet allData = new DataSet(
                Nd4j.vstack(features),
                Nd4j.vstack(labels)
            );
            return new ListDataSetIterator<>(allData.asList(), batchSize);
        }

        public INDArray augmentData(INDArray data, int numAugmentations) {
            INDArray augmented = Nd4j.create();
            for (int i = 0; i < numAugmentations; i++) {
                INDArray augmentedSample = addNoise(data.dup(), 0.1);
                augmented = Nd4j.concat(0, augmented, augmentedSample);
            }
            return augmented;
        }

        private INDArray addNoise(INDArray data, double noiseLevel) {
            Random rand = new Random();
            INDArray noise = Nd4j.create(data.shape());
            for (int i = 0; i < noise.length(); i++) {
                noise.putScalar(i, (rand.nextDouble() - 0.5) * noiseLevel);
            }
            return data.add(noise);
        }
    }

    static class ConfusionMatrix {
        private final int[][] matrix;
        private final int numClasses;

        public ConfusionMatrix(int numClasses) {
            this.numClasses = numClasses;
            this.matrix = new int[numClasses][numClasses];
        }

        public void increment(int actual, int predicted) {
            matrix[actual][predicted]++;
        }

        public double getAccuracy() {
            int diagonal = 0;
            int total = 0;
            for (int i = 0; i < numClasses; i++) {
                for (int j = 0; j < numClasses; j++) {
                    total += matrix[i][j];
                    if (i == j) diagonal += matrix[i][j];
                }
            }
            return (double) diagonal / total;
        }

        public double getAveragePrecision() {
            double sumPrecision = 0;
            for (int i = 0; i < numClasses; i++) {
                int truePositives = matrix[i][i];
                int falsePositives = 0;
                for (int j = 0; j < numClasses; j++) {
                    if (j != i) falsePositives += matrix[j][i];
                }
                if (truePositives + falsePositives > 0) {
                    sumPrecision += (double) truePositives / (truePositives + falsePositives);
                }
            }
            return sumPrecision / numClasses;
        }

        public double getAverageRecall() {
            double sumRecall = 0;
            for (int i = 0; i < numClasses; i++) {
                int truePositives = matrix[i][i];
                int falseNegatives = 0;
                for (int j = 0; j < numClasses; j++) {
                    if (j != i) falseNegatives += matrix[i][j];
                }
                if (truePositives + falseNegatives > 0) {
                    sumRecall += (double) truePositives / (truePositives + falseNegatives);
                }
            }
            return sumRecall / numClasses;
        }

        public double getAverageF1() {
            double precision = getAveragePrecision();
            double recall = getAverageRecall();
            return 2 * (precision * recall) / (precision + recall + 1e-8);
        }
    }

    static class EarlyStoppingListener implements org.deeplearning4j.optimize.listeners.IterationListener {
        private final DataSetIterator validationIterator;
        private final int patience;
        private double bestScore = Double.MAX_VALUE;
        private int epochsWithoutImprovement = 0;
        private boolean stop = false;

        public EarlyStoppingListener(DataSetIterator validationIterator, int patience) {
            this.validationIterator = validationIterator;
            this.patience = patience;
        }

        @Override
        public void iterationDone(org.deeplearning4j.nn.api.Model model, int iteration, int epoch) {
            double currentScore = model.score();
            if (currentScore < bestScore) {
                bestScore = currentScore;
                epochsWithoutImprovement = 0;
            } else {
                epochsWithoutImprovement++;
                if (epochsWithoutImprovement >= patience) {
                    stop = true;
                }
            }
        }

        public boolean shouldStop() {
            return stop;
        }
    }

    static class InputType {
        public static convolutional(int height, int width, int channels) {
            return null;
        }
    }

    static class NDArrayIndex {
        public static NDArrayIndex all() { return null; }
        public static NDArrayIndex point(int index) { return null; }
    }

    static class File {
        private final String path;
        public File(String path) { this.path = path; }
        public String getPath() { return path; }
    }

    public static void main(String[] args) {
        System.out.println("Deep Learning Solutions - DL4J Examples");
        System.out.println("===========================================");

        NeuralNetworkExample nnExample = new NeuralNetworkExample();
        MultiLayerNetwork simpleNet = nnExample.createSimpleFeedForwardNetwork(784, 10);
        System.out.println("Created simple feed-forward network");

        ConvolutionalNeuralNetworkExample cnnExample = new ConvolutionalNeuralNetworkExample();
        MultiLayerNetwork cnn = cnnExample.createCNN(28, 28, 1, 10);
        System.out.println("Created CNN for image classification");

        RecurrentNeuralNetworkExample rnnExample = new RecurrentNeuralNetworkExample();
        MultiLayerNetwork lstm = rnnExample.createLSTMNetwork(2, 32, 2, 100);
        System.out.println("Created LSTM for sequence modeling");

        DataProcessingExample dataProc = new DataProcessingExample();
        INDArray sampleData = Nd4j.create(100, 10);
        INDArray normalized = dataProc.normalizeData(sampleData);
        System.out.println("Data normalization completed");

        ModelTrainingExample training = new ModelTrainingExample();
        System.out.println("Model training utilities available");
    }
}