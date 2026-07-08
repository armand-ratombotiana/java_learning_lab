package com.ai25;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BYOLTest {
    @Test void testTargetUpdate() {
        BYOL byol = new BYOL(4, 16, 4, 0.99);
        byol.updateTarget();
        double[] input = {0.5, 0.3, 0.8, 0.2};
        double[] encoded = byol.encode(input);
        assertEquals(4, encoded.length);
    }

    @Test void testPredictor() {
        BYOL.Predictor pred = new BYOL.Predictor(4, 4);
        double[] input = {0.5, 0.3, 0.8, 0.2};
        double[] out = pred.forward(input);
        assertEquals(4, out.length);
        double norm = Math.sqrt(out[0]*out[0] + out[1]*out[1] + out[2]*out[2] + out[3]*out[3]);
        assertTrue(Math.abs(norm - 1.0) < 0.01);
    }
}
