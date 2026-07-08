package com.ai22;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GANTest {
    @Test void testGANInitialization() {
        GAN gan = new GAN(5, 3, 8);
        assertNotNull(gan);
    }

    @Test void testGenerateSample() {
        GAN gan = new GAN(5, 3, 8);
        double[] latent = gan.sampleLatent();
        assertEquals(5, latent.length);
        double[] generated = gan.generate(latent);
        assertEquals(3, generated.length);
    }

    @Test void testDiscriminate() {
        GAN gan = new GAN(5, 3, 8);
        double[] data = {0.5, 0.3, 0.8};
        double score = gan.discriminate(data);
        assertTrue(score >= 0 && score <= 1);
    }

    @Test void testTrainStep() {
        GAN gan = new GAN(5, 3, 8);
        double[] realData = {0.5, 0.3, 0.8};
        gan.trainStep(realData, 0.01);
        assertNotNull(gan);
    }

    @Test void testSampleLatent() {
        GAN gan = new GAN(10, 4, 16);
        double[] z = gan.sampleLatent();
        assertEquals(10, z.length);
    }

    @Test void testWGANInitialization() {
        WGAN wgan = new WGAN(5, 3, 8, 0.01);
        assertNotNull(wgan);
    }

    @Test void testWGanGenerate() {
        WGAN wgan = new WGAN(5, 3, 8, 0.01);
        double[] gen = wgan.generate(wgan.sampleLatent());
        assertEquals(3, gen.length);
    }

    @Test void testCGANInitialization() {
        cGAN cgan = new cGAN(5, 2, 3, 8);
        assertNotNull(cgan);
    }

    @Test void testCGANGenerate() {
        cGAN cgan = new cGAN(5, 2, 3, 8);
        double[] cond = {0.5, 0.3};
        double[] gen = cgan.generate(cgan.sampleLatent(), cond);
        assertEquals(3, gen.length);
    }
}
