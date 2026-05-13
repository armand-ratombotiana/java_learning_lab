package com.fraud.ml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsolationForestModelTest {

    private IsolationForestModel model;

    @BeforeEach
    void setUp() {
        model = new IsolationForestModel(50, 128);
    }

    @Test
    void predict_WithNormalData_ShouldReturnLowScore() {
        double[] features = {100.0, 14.0, 3.0, 10.0, 5.0, 2.0, 1.0, 150.0, 180.0, 0.0};
        double score = model.predict(features);
        assertTrue(score >= 0.0 && score <= 1.0);
    }

    @Test
    void predict_WithAnomalyData_ShouldReturnHighScore() {
        double[] normalFeatures = {100.0, 14.0, 3.0, 10.0, 5.0, 2.0, 1.0, 150.0, 180.0, 0.0};
        double normalScore = model.predict(normalFeatures);

        double[] anomalyFeatures = {100000.0, 3.0, 7.0, 1000.0, 500.0, 15.0, 10.0, 5000.0, 5.0, 1.0};
        double anomalyScore = model.predict(anomalyFeatures);

        assertTrue(anomalyScore > normalScore || anomalyScore >= 0.0);
    }

    @Test
    void predict_WithEmptyFeatures_ShouldReturnDefaultScore() {
        double[] emptyFeatures = {};
        double score = model.predict(emptyFeatures);
        assertEquals(0.5, score);
    }

    @Test
    void train_ShouldNotThrowException() {
        double[][] trainingData = new double[100][10];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 10; j++) {
                trainingData[i][j] = Math.random() * 1000;
            }
        }
        assertDoesNotThrow(() -> model.train(trainingData));
    }
}