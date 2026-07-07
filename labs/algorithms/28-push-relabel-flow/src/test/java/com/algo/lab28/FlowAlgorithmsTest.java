package com.algo.lab28;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlowAlgorithmsTest {

    @Test
    void testPushRelabel() {
        PushRelabel pr = new PushRelabel(4);
        pr.addEdge(0, 1, 10);
        pr.addEdge(0, 2, 10);
        pr.addEdge(1, 2, 5);
        pr.addEdge(1, 3, 10);
        pr.addEdge(2, 3, 10);
        assertEquals(20, pr.maxFlow(0, 3));
    }

    @Test
    void testPushRelabelNoFlow() {
        PushRelabel pr = new PushRelabel(3);
        pr.addEdge(0, 1, 5);
        assertEquals(0, pr.maxFlow(0, 2));
    }

    @Test
    void testMinCostMaxFlow() {
        MinCostMaxFlow mcmf = new MinCostMaxFlow(4);
        mcmf.addEdge(0, 1, 10, 2);
        mcmf.addEdge(0, 2, 10, 4);
        mcmf.addEdge(1, 2, 5, 1);
        mcmf.addEdge(1, 3, 10, 3);
        mcmf.addEdge(2, 3, 10, 2);
        long[] result = mcmf.minCostFlow(0, 3, 10);
        assertEquals(10, result[0]);
    }

    @Test
    void testCapacityScaling() {
        CapacityScaling cs = new CapacityScaling(4);
        cs.addEdge(0, 1, 10);
        cs.addEdge(0, 2, 10);
        cs.addEdge(1, 2, 5);
        cs.addEdge(1, 3, 10);
        cs.addEdge(2, 3, 10);
        assertEquals(20, cs.maxFlow(0, 3));
    }

    @Test
    void testPushRelabelSimple() {
        PushRelabel pr = new PushRelabel(2);
        pr.addEdge(0, 1, 5);
        assertEquals(5, pr.maxFlow(0, 1));
    }
}
