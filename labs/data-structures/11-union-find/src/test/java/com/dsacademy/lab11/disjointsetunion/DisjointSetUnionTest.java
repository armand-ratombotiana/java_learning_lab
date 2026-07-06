package com.dsacademy.lab11.disjointsetunion;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

public class DisjointSetUnionTest {

    private DisjointSetUnion dsu;

    @BeforeEach
    void setUp() {
        dsu = new DisjointSetUnion(10);
    }

    @Test
    void newStructureAllIsolated() {
        assertEquals(10, dsu.getSets());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, dsu.find(i));
        }
    }

    @Test
    void unionTwoElements() {
        assertTrue(dsu.union(0, 1));
        assertEquals(9, dsu.getSets());
        assertTrue(dsu.connected(0, 1));
    }

    @Test
    void unionSameElement() {
        assertFalse(dsu.union(3, 3));
        assertEquals(10, dsu.getSets());
    }

    @Test
    void unionAlreadyConnected() {
        dsu.union(0, 1);
        assertFalse(dsu.union(0, 1));
        assertEquals(9, dsu.getSets());
    }

    @Test
    void transitiveConnectivity() {
        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(2, 3);
        assertTrue(dsu.connected(0, 3));
        assertTrue(dsu.connected(1, 3));
        assertEquals(7, dsu.getSets());
    }

    @Test
    void multipleDisjointSets() {
        dsu.union(0, 1);
        dsu.union(2, 3);
        dsu.union(4, 5);
        assertEquals(7, dsu.getSets());
        assertTrue(dsu.connected(0, 1));
        assertTrue(dsu.connected(2, 3));
        assertTrue(dsu.connected(4, 5));
        assertFalse(dsu.connected(0, 2));
    }

    @Test
    void largeUnionSequence() {
        for (int i = 0; i < 9; i++) {
            dsu.union(i, i + 1);
        }
        assertEquals(1, dsu.getSets());
        for (int i = 0; i < 10; i++) {
            assertTrue(dsu.connected(0, i));
        }
    }

    @Test
    void findAfterPathCompression() {
        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(2, 3);
        dsu.find(3);
        assertEquals(dsu.find(0), dsu.find(3));
    }

    @Test
    void negativeSizeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new DisjointSetUnion(-1));
    }

    @Test
    void indexOutOfBoundsThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.find(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> dsu.find(100));
    }

    @Test
    void kruskalMST() {
        List<KruskalMST.Edge> edges = List.of(
            new KruskalMST.Edge(0, 1, 2),
            new KruskalMST.Edge(1, 2, 3),
            new KruskalMST.Edge(0, 2, 5),
            new KruskalMST.Edge(2, 3, 1)
        );
        List<KruskalMST.Edge> mst = KruskalMST.findMST(edges, 4);
        assertEquals(3, mst.size());
        int expectedWeight = 2 + 3 + 1;
        assertEquals(expectedWeight, KruskalMST.mstWeight(edges, 4));
    }

    @Test
    void kruskalDisconnectedGraph() {
        List<KruskalMST.Edge> edges = List.of(
            new KruskalMST.Edge(0, 1, 1),
            new KruskalMST.Edge(2, 3, 1)
        );
        assertFalse(KruskalMST.hasSpanningTree(edges, 5));
    }

    @Test
    void connectedComponents() {
        int[][] edges = {{0, 1}, {2, 3}, {4, 5}};
        assertEquals(7, ConnectedComponents.countComponents(10, edges));
        Map<Integer, List<Integer>> components = ConnectedComponents.getComponents(10, edges);
        assertEquals(7, components.size());
    }

    @Test
    void cycleDetection() {
        int[][] edges = {{0, 1}, {1, 2}, {2, 0}};
        assertTrue(ConnectedComponents.hasCycle(3, edges));
    }

    @Test
    void noCycle() {
        int[][] edges = {{0, 1}, {1, 2}, {2, 3}};
        assertFalse(ConnectedComponents.hasCycle(4, edges));
    }

    @Test
    void numberOfIslands() {
        int[][] grid = {
            {1, 1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
        assertEquals(3, ConnectedComponents.islandCount(grid));
    }

    @Test
    void dsuBySize() {
        DisjointSetUnionBySize dsuSize = new DisjointSetUnionBySize(10);
        dsuSize.union(0, 1);
        dsuSize.union(1, 2);
        dsuSize.union(2, 3);
        assertEquals(4, dsuSize.componentSize(0));
        assertEquals(7, dsuSize.getSets());
    }

    @Test
    void iterativeFind() {
        dsu.union(0, 1);
        dsu.union(1, 2);
        assertEquals(dsu.findIterative(0), dsu.findIterative(2));
    }
}
