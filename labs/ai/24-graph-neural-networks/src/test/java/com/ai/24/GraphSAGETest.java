package com.ai24;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphSAGETest {
    @Test void testNoNeighbors() {
        GraphSAGE sage = new GraphSAGE(3, 6, 2);
        double[] node = {1, 0, 0.5};
        double[] emb = sage.forward(node, java.util.List.of());
        assertEquals(2, emb.length);
    }

    @Test void testMultipleNeighbors() {
        GraphSAGE sage = new GraphSAGE(2, 4, 2);
        double[] node = {0.5, 0.5};
        var neighbors = java.util.List.of(
            new double[]{1, 0}, new double[]{0, 1}, new double[]{0.5, 0.5}
        );
        double[] emb = sage.forward(node, neighbors);
        assertFalse(Double.isNaN(emb[0]));
        assertFalse(Double.isNaN(emb[1]));
    }
}
