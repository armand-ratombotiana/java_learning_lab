package com.ai28;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntegratedGradientsTest {
    @Test void testIGInitialization() {
        IntegratedGradients.SimpleNN model = new IntegratedGradients.SimpleNN(4, 16);
        IntegratedGradients ig = new IntegratedGradients(model, 50);
        assertNotNull(ig);
    }

    @Test void testExplain() {
        IntegratedGradients.SimpleNN model = new IntegratedGradients.SimpleNN(4, 16);
        IntegratedGradients ig = new IntegratedGradients(model, 50);
        double[] input = {1.0, 0.5, 0.3, 0.8};
        double[] baseline = {0, 0, 0, 0};
        double[] attributions = ig.explain(input, baseline);
        assertEquals(4, attributions.length);
    }

    @Test void testCompleteness() {
        IntegratedGradients.SimpleNN model = new IntegratedGradients.SimpleNN(2, 8);
        IntegratedGradients ig = new IntegratedGradients(model, 50);
        double[] input = {1.0, 0.5};
        double[] baseline = {0, 0};
        double[] attributions = ig.explain(input, baseline);
        double sumAttr = attributions[0] + attributions[1];
        double pred = model.predict(input);
        double basePred = model.predict(baseline);
        assertEquals(pred - basePred, sumAttr, 0.5);
    }

    @Test void testSimpleNNPredict() {
        IntegratedGradients.SimpleNN model = new IntegratedGradients.SimpleNN(3, 8);
        double pred = model.predict(new double[]{0.5, 0.3, 0.8});
        assertTrue(pred >= 0 && pred <= 1);
    }

    @Test void testGradientComputation() {
        IntegratedGradients.SimpleNN model = new IntegratedGradients.SimpleNN(2, 4);
        IntegratedGradients ig = new IntegratedGradients(model, 20);
        double[] grads = ig.computeGradients(new double[]{0.5, 0.3});
        assertEquals(2, grads.length);
    }
}
