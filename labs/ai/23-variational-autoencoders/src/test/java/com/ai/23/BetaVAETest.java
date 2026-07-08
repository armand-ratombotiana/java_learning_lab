package com.ai23;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BetaVAETest {
    @Test void testReparameterize() {
        BetaVAE bvae = new BetaVAE(4, 2, 8, 1.0);
        double[] z = bvae.reparameterize(new double[]{0.5, -0.3}, new double[]{-0.5, 0.2});
        assertEquals(2, z.length);
    }

    @Test void testForwardConsistency() {
        BetaVAE bvae = new BetaVAE(4, 2, 8, 1.0);
        double[] input = {1, 0, 1, 0};
        double[] recon1 = bvae.forward(input);
        double[] recon2 = bvae.forward(input);
        assertNotNull(recon1);
        assertNotNull(recon2);
    }
}
