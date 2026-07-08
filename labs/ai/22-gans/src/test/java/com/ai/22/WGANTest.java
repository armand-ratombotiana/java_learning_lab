package com.ai22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WGANTest {
    @Test void testWGANInitialization() {
        WGAN wgan = new WGAN(10, 5, 16, 0.01);
        assertNotNull(wgan);
    }

    @Test void testSampleAndGenerate() {
        WGAN wgan = new WGAN(10, 5, 16, 0.01);
        double[] z = wgan.sampleLatent();
        assertEquals(10, z.length);
        double[] gen = wgan.generate(z);
        assertEquals(5, gen.length);
    }

    @Test void testCriticForward() {
        WGAN.Critic critic = new WGAN.Critic(4, 8);
        double[] data = {0.5, 0.3, 0.8, 0.2};
        double score = critic.forward(data);
        assertFalse(Double.isNaN(score));
    }

    @Test void testCriticWeightClipping() {
        WGAN.Critic critic = new WGAN.Critic(3, 6);
        critic.clipWeights(0.01);
        assertNotNull(critic);
    }
}
