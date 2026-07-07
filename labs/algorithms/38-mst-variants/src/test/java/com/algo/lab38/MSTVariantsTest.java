package com.algo.lab38;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class MSTVariantsTest {

    @Test
    void testBoruvka() {
        BoruvkaMST bmst = new BoruvkaMST(4);
        bmst.addEdge(0, 1, 1); bmst.addEdge(1, 2, 2); bmst.addEdge(2, 3, 3);
        bmst.addEdge(0, 3, 4); bmst.addEdge(0, 2, 5);
        bmst.compute();
        assertEquals(6.0, bmst.getMstWeight(), 1e-9);
        assertEquals(3, bmst.getMst().size());
    }

    @Test
    void testBoruvkaDisconnected() {
        BoruvkaMST bmst = new BoruvkaMST(3);
        bmst.addEdge(0, 1, 1);
        bmst.compute();
        assertTrue(bmst.getMst().size() <= 2);
    }

    @Test
    void testSteinerApprox() {
        SteinerTreeApprox sta = new SteinerTreeApprox(5);
        sta.addEdge(0, 1, 1); sta.addEdge(1, 2, 2); sta.addEdge(2, 3, 3);
        sta.addEdge(3, 4, 4); sta.addEdge(0, 4, 10);
        Set<Integer> terminals = new HashSet<>(Arrays.asList(0, 2, 4));
        Set<Integer> steiner = sta.approximateSteiner(terminals);
        assertTrue(steiner.containsAll(terminals));
    }

    @Test
    void testMinBottleneck() {
        MinBottleneckSpanningTree mb = new MinBottleneckSpanningTree(4);
        mb.addEdge(0, 1, 5); mb.addEdge(1, 2, 3); mb.addEdge(2, 3, 2);
        mb.addEdge(0, 3, 8); mb.addEdge(0, 2, 10);
        double bottleneck = mb.findMinBottleneck();
        assertTrue(bottleneck >= 4.9 && bottleneck <= 5.1);
    }

    @Test
    void testEuclideanMST() {
        EuclideanMST emst = new EuclideanMST();
        emst.addPoint(0, 0); emst.addPoint(1, 0); emst.addPoint(0, 1);
        emst.addPoint(1, 1);
        List<int[]> mst = emst.computeMst();
        assertEquals(3, mst.size());
    }

    @Test
    void testEuclideanMSTSinglePoint() {
        EuclideanMST emst = new EuclideanMST();
        emst.addPoint(0, 0);
        List<int[]> mst = emst.computeMst();
        assertTrue(mst.isEmpty());
    }
}
