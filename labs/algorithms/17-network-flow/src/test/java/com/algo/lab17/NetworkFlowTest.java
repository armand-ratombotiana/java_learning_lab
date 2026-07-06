package com.algo.lab17;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NetworkFlowTest {

    @Test
    void testFordFulkersonBasic() {
        FordFulkerson ff = new FordFulkerson(6);
        ff.addEdge(0, 1, 16);
        ff.addEdge(0, 2, 13);
        ff.addEdge(1, 2, 10);
        ff.addEdge(1, 3, 12);
        ff.addEdge(2, 1, 4);
        ff.addEdge(2, 4, 14);
        ff.addEdge(3, 2, 9);
        ff.addEdge(3, 5, 20);
        ff.addEdge(4, 3, 7);
        ff.addEdge(4, 5, 4);
        assertEquals(23, ff.maxFlow(0, 5));
    }

    @Test
    void testFordFulkersonNoFlow() {
        FordFulkerson ff = new FordFulkerson(3);
        ff.addEdge(0, 1, 10);
        assertEquals(0, ff.maxFlow(0, 2));
    }

    @Test
    void testFordFulkersonSingleEdge() {
        FordFulkerson ff = new FordFulkerson(2);
        ff.addEdge(0, 1, 5);
        assertEquals(5, ff.maxFlow(0, 1));
    }

    @Test
    void testEdmondsKarpBasic() {
        EdmondsKarp ek = new EdmondsKarp(6);
        ek.addEdge(0, 1, 16);
        ek.addEdge(0, 2, 13);
        ek.addEdge(1, 2, 10);
        ek.addEdge(1, 3, 12);
        ek.addEdge(2, 1, 4);
        ek.addEdge(2, 4, 14);
        ek.addEdge(3, 2, 9);
        ek.addEdge(3, 5, 20);
        ek.addEdge(4, 3, 7);
        ek.addEdge(4, 5, 4);
        assertEquals(23, ek.maxFlow(0, 5));
    }

    @Test
    void testEdmondsKarpDisconnected() {
        EdmondsKarp ek = new EdmondsKarp(3);
        assertEquals(0, ek.maxFlow(0, 2));
    }

    @Test
    void testDinicBasic() {
        Dinic d = new Dinic(6);
        d.addEdge(0, 1, 16);
        d.addEdge(0, 2, 13);
        d.addEdge(1, 2, 10);
        d.addEdge(1, 3, 12);
        d.addEdge(2, 1, 4);
        d.addEdge(2, 4, 14);
        d.addEdge(3, 2, 9);
        d.addEdge(3, 5, 20);
        d.addEdge(4, 3, 7);
        d.addEdge(4, 5, 4);
        assertEquals(23, d.maxFlow(0, 5));
    }

    @Test
    void testDinicSingleEdge() {
        Dinic d = new Dinic(2);
        d.addEdge(0, 1, 100);
        assertEquals(100, d.maxFlow(0, 1));
    }

    @Test
    void testDinicNoPath() {
        Dinic d = new Dinic(4);
        d.addEdge(0, 1, 10);
        d.addEdge(2, 3, 10);
        assertEquals(0, d.maxFlow(0, 3));
    }

    @Test
    void testBipartiteMatching() {
        BipartiteMatching bm = new BipartiteMatching(4, 4);
        bm.addEdge(0, 1);
        bm.addEdge(0, 2);
        bm.addEdge(1, 0);
        bm.addEdge(1, 2);
        bm.addEdge(2, 2);
        bm.addEdge(2, 3);
        bm.addEdge(3, 2);
        assertEquals(3, bm.maxMatching());
    }

    @Test
    void testBipartiteMatchingEmpty() {
        BipartiteMatching bm = new BipartiteMatching(3, 3);
        assertEquals(0, bm.maxMatching());
    }

    @Test
    void testBipartiteMatchingGetMatching() {
        BipartiteMatching bm = new BipartiteMatching(2, 2);
        bm.addEdge(0, 0);
        bm.addEdge(0, 1);
        bm.addEdge(1, 0);
        List<int[]> matching = bm.getMatching();
        assertEquals(2, matching.size());
    }

    @Test
    void testMinCutValue() {
        MinCut mc = new MinCut(6);
        mc.addEdge(0, 1, 16);
        mc.addEdge(0, 2, 13);
        mc.addEdge(1, 2, 10);
        mc.addEdge(1, 3, 12);
        mc.addEdge(2, 1, 4);
        mc.addEdge(2, 4, 14);
        mc.addEdge(3, 2, 9);
        mc.addEdge(3, 5, 20);
        mc.addEdge(4, 3, 7);
        mc.addEdge(4, 5, 4);
        assertEquals(23, mc.minCutValue(0, 5));
    }

    @Test
    void testMinCutSourceSide() {
        MinCut mc = new MinCut(4);
        mc.addEdge(0, 1, 10);
        mc.addEdge(1, 2, 5);
        mc.addEdge(2, 3, 10);
        List<Integer> side = mc.getSourceSide(0, 3);
        assertTrue(side.contains(0));
        assertFalse(side.contains(3));
    }
}
