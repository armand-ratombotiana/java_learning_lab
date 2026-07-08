package com.ai28;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SHAPExplainerTest {
    @Test void testSHAPInitialization() {
        double[] weights = {1.0, -1.0};
        SHAPExplainer.LinearModel model = new SHAPExplainer.LinearModel(weights, 0.0);
        double[][] background = {{0.5, 0.5}, {0.2, 0.8}};
        SHAPExplainer explainer = new SHAPExplainer(model, background, 50);
        assertNotNull(explainer);
    }

    @Test void testExplain() {
        double[] weights = {2.0, -1.0};
        SHAPExplainer.LinearModel model = new SHAPExplainer.LinearModel(weights, 0.0);
        double[][] background = {{0.5, 0.5}, {0.3, 0.7}, {0.8, 0.2}};
        SHAPExplainer explainer = new SHAPExplainer(model, background, 100);
        double[] instance = {0.8, 0.2};
        double[] shapValues = explainer.explain(instance);
        assertEquals(2, shapValues.length);
    }

    @Test void testLinearModelPredict() {
        double[] weights = {1.0, 2.0};
        SHAPExplainer.LinearModel model = new SHAPExplainer.LinearModel(weights, 0.5);
        double pred = model.predict(new double[]{0.5, 0.3});
        assertTrue(pred > 0 && pred < 1);
    }
}
