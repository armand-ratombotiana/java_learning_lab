package com.ai29;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FedAvgTest {
    @Test void testInitialization() {
        FedAvg fedAvg = new FedAvg(1, 10, 1, 5, 0.01, 3);
        assertNotNull(fedAvg);
    }

    @Test void testPredict() {
        FedAvg fedAvg = new FedAvg(1, 10, 1, 2, 0.01, 1);
        double pred = fedAvg.predict(new double[]{1.0});
        assertFalse(Double.isNaN(pred));
    }

    @Test void testGlobalModel() {
        FedAvg.GlobalModel gm = new FedAvg.GlobalModel(2, 8, 1);
        double[] pred = gm.forward(new double[]{0.5, 0.3});
        assertEquals(1, pred.length);
    }

    @Test void testAggregation() {
        FedAvg fedAvg = new FedAvg(2, 8, 1, 3, 0.01, 1);
        java.util.List<java.util.List<double[]>> clientData = new java.util.ArrayList<>();
        var rng = new java.util.Random(42);
        for (int c = 0; c < 3; c++) {
            java.util.List<double[]> data = new java.util.ArrayList<>();
            for (int i = 0; i < 5; i++) {
                double x = rng.nextDouble();
                data.add(new double[]{2*x + 1, x});
            }
            clientData.add(data);
        }
        fedAvg.train(clientData, 5, 0.5);
        double pred = fedAvg.predict(new double[]{0.5});
        assertTrue(pred > -5 && pred < 10);
    }
}
