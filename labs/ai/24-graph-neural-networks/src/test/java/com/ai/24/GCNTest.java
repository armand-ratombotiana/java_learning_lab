package com.ai24;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GCNTest {
    @Test void testGCNInitialization() {
        GCN gcn = new GCN(3, 8, 4);
        assertNotNull(gcn);
    }

    @Test void testForwardPass() {
        GCN gcn = new GCN(3, 8, 4);
        int n = 4;
        double[][] features = new double[n][3];
        double[][] adj = new double[n][n];
        for (int i = 0; i < n; i++) {
            features[i][0] = 1; features[i][1] = 0.5; features[i][2] = 0;
            if (i < n - 1) { adj[i][i+1] = 1; adj[i+1][i] = 1; }
        }
        double[][] output = gcn.forward(features, adj);
        assertEquals(n, output.length);
        assertEquals(4, output[0].length);
    }

    @Test void testNodeEmbeddings() {
        GCN gcn = new GCN(2, 4, 2);
        double[][] features = {{1,0}, {0,1}, {1,1}};
        double[][] adj = {{0,1,0}, {1,0,1}, {0,1,0}};
        double[][] emb = gcn.getNodeEmbeddings(features, adj);
        assertEquals(3, emb.length);
        assertEquals(2, emb[0].length);
    }

    @Test void testGraphSAGEInitialization() {
        GraphSAGE sage = new GraphSAGE(4, 8, 3);
        assertNotNull(sage);
    }

    @Test void testGraphSAGEFoward() {
        GraphSAGE sage = new GraphSAGE(4, 8, 3);
        double[] node = {0.5, 0.3, 0.8, 0.2};
        var neighbors = java.util.List.of(new double[]{0.1,0.2,0.3,0.4}, new double[]{0.5,0.6,0.7,0.8});
        double[] emb = sage.forward(node, neighbors);
        assertEquals(3, emb.length);
    }

    @Test void testGraphSAGEEmbeddingNorm() {
        GraphSAGE sage = new GraphSAGE(2, 4, 3);
        double[] node = {1, 0};
        var neighbors = java.util.List.of(new double[]{0, 1});
        double[] emb = sage.forward(node, neighbors);
        double norm = Math.sqrt(emb[0]*emb[0] + emb[1]*emb[1] + emb[2]*emb[2]);
        assertTrue(Math.abs(norm - 1.0) < 0.01);
    }
}
