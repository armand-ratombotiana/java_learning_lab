package com.databases.advsql;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class RecursiveCteTest {
    @Test void shouldTraverseTree() {
        var nodes = RecursiveCte.sampleOrg();
        var result = RecursiveCte.traverseTree(nodes, 1);
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).level()); // CEO at level 1
    }

    @Test void shouldFindReachableNodes() {
        var graph = RecursiveCte.sampleGraph();
        var reachable = RecursiveCte.reachableNodes(graph, 1);
        assertEquals(6, reachable.size()); // all 6 nodes reachable from 1
    }

    @Test void shouldFindAllPaths() {
        var graph = RecursiveCte.sampleGraph();
        var paths = RecursiveCte.findAllPaths(graph, 1, 6);
        assertFalse(paths.isEmpty());
        assertTrue(paths.size() >= 2); // 1->2->4->6? Actually 1->2->5->6 and 1->3->6
    }

    @Test void shouldTopologicalSort() {
        var graph = RecursiveCte.sampleGraph();
        var sorted = RecursiveCte.topologicalSort(graph);
        assertEquals(6, sorted.size());
        // verify order: 1 before its children
        assertTrue(sorted.indexOf(1) < sorted.indexOf(2));
        assertTrue(sorted.indexOf(1) < sorted.indexOf(3));
    }
}