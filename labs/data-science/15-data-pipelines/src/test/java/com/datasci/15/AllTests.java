package com.datasci.15;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for Data Pipelines implementation.
 */
class CoreAlgorithmTest {

    private CoreAlgorithm algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new CoreAlgorithm(new CoreAlgorithm.AlgorithmConfig.Builder()
            .learningRate(0.01)
            .maxIterations(100)
            .tolerance(1e-6)
            .regularization(0.001)
            .validationRatio(0.2)
            .randomSeed(42)
            .build());
    }

    @Test
    @DisplayName("Should train on simple linear data")
    void testTraining() {
        double[][] features = new double[][]{{1.0}, {2.0}, {3.0}, {4.0}, {5.0}};
        double[] labels = new double[]{3.0, 5.0, 7.0, 9.0, 11.0};
        List<Double> lossHistory = algorithm.fit(features, labels);
        assertTrue(algorithm.isTrained());
        assertNotNull(lossHistory);
        assertFalse(lossHistory.isEmpty());
    }

    @Test
    @DisplayName("Should make predictions after training")
    void testPrediction() {
        double[][] features = new double[][]{{1.0}, {2.0}, {3.0}};
        double[] labels = new double[]{2.0, 4.0, 6.0};
        algorithm.fit(features, labels);
        double[][] testFeatures = new double[][]{{4.0}, {5.0}};
        double[] predictions = algorithm.predict(testFeatures);
        assertEquals(2, predictions.length);
        assertTrue(Double.isFinite(predictions[0]));
    }

    @Test
    @DisplayName("Should throw when predicting without training")
    void testPredictWithoutTraining() {
        assertThrows(IllegalStateException.class, () ->
            algorithm.predict(new double[][]{{1.0}}));
    }

    @Test
    @DisplayName("Should throw on null features")
    void testNullFeatures() {
        assertThrows(CoreAlgorithm.DataValidationException.class, () ->
            algorithm.fit(null, new double[]{1.0}));
    }

    @Test
    @DisplayName("Should throw on mismatched sizes")
    void testMismatchedSizes() {
        double[][] features = new double[][]{{1.0}, {2.0}};
        double[] labels = new double[]{1.0};
        assertThrows(CoreAlgorithm.DataValidationException.class, () ->
            algorithm.fit(features, labels));
    }

    @Test
    @DisplayName("Should handle single sample")
    void testSingleSample() {
        double[][] features = new double[][]{{3.0}};
        double[] labels = new double[]{7.0};
        algorithm.fit(features, labels);
        double[] pred = algorithm.predict(features);
        assertEquals(1, pred.length);
    }

    @Test
    @DisplayName("Default config creates valid algorithm")
    void testDefaultConfig() {
        CoreAlgorithm defaultAlgo = new CoreAlgorithm();
        assertNotNull(defaultAlgo.getConfig());
        assertFalse(defaultAlgo.isTrained());
    }
}

class MetricsTest {

    @Test
    @DisplayName("Confusion matrix computation")
    void testConfusionMatrix() {
        double[] predictions = new double[]{0.8, 0.3, 0.6, 0.2, 0.9};
        double[] labels = new double[]{1.0, 0.0, 1.0, 0.0, 1.0};
        var cm = Metrics.confusionMatrix(predictions, labels, 0.5);
        assertEquals(3, cm.tp());
        assertEquals(2, cm.tn());
        assertEquals(0, cm.fp());
        assertEquals(0, cm.fn());
    }

    @Test
    @DisplayName("Accuracy calculation")
    void testAccuracy() {
        assertEquals(0.75, Metrics.accuracy(2, 1, 1, 0), 1e-9);
    }

    @Test
    @DisplayName("MSE calculation")
    void testMSE() {
        double[] pred = new double[]{2.0, 4.0, 6.0};
        double[] actual = new double[]{1.0, 3.0, 5.0};
        assertEquals(1.0, Metrics.meanSquaredError(pred, actual), 1e-9);
    }

    @Test
    @DisplayName("R-squared calculation")
    void testRSquared() {
        double[] pred = new double[]{1.0, 2.0, 3.0};
        double[] actual = new double[]{1.0, 2.0, 3.0};
        assertEquals(1.0, Metrics.rSquared(pred, actual), 1e-9);
    }

    @Test
    @DisplayName("F1 score calculation")
    void testF1() {
        assertEquals(0.8, Metrics.f1Score(0.8, 0.8), 1e-9);
    }
}

class UtilsTest {

    @Test
    @DisplayName("Matrix transpose")
    void testTranspose() {
        double[][] m = new double[][]{{1, 2}, {3, 4}};
        double[][] t = Utils.transpose(m);
        assertEquals(1.0, t[0][0]);
        assertEquals(3.0, t[0][1]);
        assertEquals(2.0, t[1][0]);
        assertEquals(4.0, t[1][1]);
    }

    @Test
    @DisplayName("Euclidean distance")
    void testEuclideanDistance() {
        double[] a = new double[]{0, 0};
        double[] b = new double[]{3, 4};
        assertEquals(5.0, Utils.euclideanDistance(a, b), 1e-9);
    }

    @Test
    @DisplayName("Mean calculation")
    void testMean() {
        assertEquals(3.0, Utils.mean(new double[]{1, 2, 3, 4, 5}), 1e-9);
    }

    @Test
    @DisplayName("Correlation coefficient")
    void testCorrelation() {
        double[] x = new double[]{1, 2, 3, 4, 5};
        double[] y = new double[]{2, 4, 6, 8, 10};
        assertEquals(1.0, Utils.correlation(x, y), 1e-9);
    }

    @Test
    @DisplayName("Z-score normalization")
    void testZScoreNormalize() {
        double[] vals = new double[]{1, 2, 3, 4, 5};
        double[] norm = Utils.zScoreNormalize(vals);
        assertEquals(0.0, Utils.mean(norm), 1e-9);
        assertEquals(1.0, Utils.stdDev(norm), 1e-9);
    }
}

class DataPreprocessorTest {

    @Test
    @DisplayName("Fit and transform")
    void testFitTransform() {
        DataPreprocessor dp = new DataPreprocessor();
        double[][] data = new double[][]{{1.0, 2.0}, {3.0, 4.0}};
        dp.fit(data);
        double[][] transformed = dp.transform(data);
        assertEquals(2, transformed.length);
        assertEquals(2, transformed[0].length);
    }

    @Test
    @DisplayName("Impute mean values")
    void testImputeMean() {
        DataPreprocessor dp = new DataPreprocessor();
        double[][] data = new double[][]{{1.0}, {Double.NaN}, {3.0}};
        dp.fit(data);
        double[][] imputed = dp.imputeMean(data);
        assertEquals(2.0, imputed[1][0], 0.01);
    }

    @Test
    @DisplayName("Standardize features")
    void testStandardize() {
        DataPreprocessor dp = new DataPreprocessor();
        double[][] data = new double[][]{{1.0}, {2.0}, {3.0}};
        dp.fit(data);
        double[][] standardized = dp.standardize(data);
        double sum = standardized[0][0] + standardized[1][0] + standardized[2][0];
        assertEquals(0.0, sum, 1e-9);
    }

    @Test
    @DisplayName("Train/test split")
    void testTrainTestSplit() {
        double[][] features = new double[][]{{1}, {2}, {3}, {4}, {5}};
        double[] labels = new double[]{1, 2, 3, 4, 5};
        var split = DataPreprocessor.trainTestSplit(features, labels, 0.4, new java.util.Random(42));
        assertEquals(3, split.trainFeatures().length);
        assertEquals(2, split.testFeatures().length);
    }
}
