package com.algo.lab37;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GraphColoringCliqueTest {

    @Test
    void testGreedyColoring() {
        GraphColoring gc = new GraphColoring(4);
        gc.addEdge(0, 1); gc.addEdge(1, 2); gc.addEdge(2, 3); gc.addEdge(3, 0);
        int[] colors = gc.greedy();
        assertEquals(4, colors.length);
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (gc.adj[i][j]) assertNotEquals(colors[i], colors[j]);
            }
        }
    }

    @Test
    void testWelshPowell() {
        GraphColoring gc = new GraphColoring(5);
        gc.addEdge(0, 1); gc.addEdge(0, 2); gc.addEdge(1, 2); gc.addEdge(1, 3); gc.addEdge(2, 4);
        int[] colors = gc.welshPowell();
        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                if (gc.adj[i][j]) assertNotEquals(colors[i], colors[j]);
            }
        }
    }

    @Test
    void testDsatur() {
        GraphColoring gc = new GraphColoring(4);
        gc.addEdge(0, 1); gc.addEdge(0, 2); gc.addEdge(1, 2); gc.addEdge(1, 3);
        int[] colors = gc.dsatur();
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (gc.adj[i][j]) assertNotEquals(colors[i], colors[j]);
            }
        }
    }

    @Test
    void testChromaticNumber() {
        GraphColoring gc = new GraphColoring(4);
        gc.addEdge(0, 1); gc.addEdge(1, 2); gc.addEdge(2, 3); gc.addEdge(3, 0);
        int chi = gc.chromaticNumber();
        assertTrue(chi >= 2);
    }

    @Test
    void testBronKerbosch() {
        BronKerbosch bk = new BronKerbosch(4);
        bk.addEdge(0, 1); bk.addEdge(0, 2); bk.addEdge(1, 2); bk.addEdge(2, 3);
        List<Set<Integer>> cliques = bk.findAllMaximalCliques();
        assertFalse(cliques.isEmpty());
        boolean hasTriangle = cliques.stream().anyMatch(c -> c.size() >= 3);
        assertTrue(hasTriangle);
    }

    @Test
    void testMaximumClique() {
        MaximumClique mc = new MaximumClique(5);
        mc.addEdge(0, 1); mc.addEdge(0, 2); mc.addEdge(1, 2);
        mc.addEdge(1, 3); mc.addEdge(2, 3); mc.addEdge(3, 4);
        Set<Integer> maxClique = mc.findMaximum();
        assertTrue(maxClique.size() >= 3);
    }

    @Test
    void testEmptyGraph() {
        GraphColoring gc = new GraphColoring(3);
        int[] colors = gc.greedy();
        assertEquals(0, colors[0]);
        assertEquals(0, colors[1]);
        assertEquals(0, colors[2]);
    }
}
