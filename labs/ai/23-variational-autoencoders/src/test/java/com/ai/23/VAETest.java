package com.ai23;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VAETest {
    @Test void testVAEInitialization() {
        VAE vae = new VAE(8, 3, 16);
        assertNotNull(vae);
    }

    @Test void testEncodeDecode() {
        VAE vae = new VAE(8, 3, 16);
        double[] input = {1, 0, 1, 0, 1, 0, 1, 0};
        double[] mean = vae.encode(input);
        assertEquals(3, mean.length);
        double[] z = vae.reparameterize(mean, new double[]{-1, -1, -1});
        double[] recon = vae.decode(z);
        assertEquals(8, recon.length);
    }

    @Test void testForward() {
        VAE vae = new VAE(8, 3, 16);
        double[] input = {1, 0, 1, 0, 1, 0, 1, 0};
        VAE.VAEResult result = vae.forward(input);
        assertNotNull(result);
        assertEquals(8, result.reconstructed().length);
        assertEquals(3, result.mean().length);
        assertEquals(3, result.logVar().length);
        assertTrue(result.loss() > 0);
    }

    @Test void testBetaVAEInitialization() {
        BetaVAE bvae = new BetaVAE(8, 3, 16, 4.0);
        assertNotNull(bvae);
    }

    @Test void testBetaVAEForward() {
        BetaVAE bvae = new BetaVAE(8, 3, 16, 4.0);
        double[] input = {1, 0, 1, 0, 1, 0, 1, 0};
        double[] recon = bvae.forward(input);
        assertEquals(8, recon.length);
    }

    @Test void testBetaVAELoss() {
        BetaVAE bvae = new BetaVAE(8, 3, 16, 4.0);
        double[] input = {1, 0, 1, 0, 1, 0, 1, 0};
        double[] mean = {0.1, -0.2, 0.3};
        double[] logVar = {-0.5, 0.1, -0.3};
        double[] recon = bvae.forward(input);
        double loss = bvae.computeLoss(input, recon, mean, logVar);
        assertTrue(loss > 0);
    }
}
