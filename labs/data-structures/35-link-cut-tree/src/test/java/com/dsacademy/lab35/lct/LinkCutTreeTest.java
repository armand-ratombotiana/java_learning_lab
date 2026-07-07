package com.dsacademy.lab35.lct;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class LinkCutTreeTest {

    private LinkCutTree lct;
    private DynamicConnectivityLCT dcl;

    @BeforeEach
    void setUp() {
        lct = new LinkCutTree();
        dcl = new DynamicConnectivityLCT();
    }

    @Test
    void testMakeNode() {
        LctNode node = lct.makeNode(1, 10);
        assertEquals(1, node.getId());
        assertEquals(10, node.getValue());
        assertEquals(10, node.getAggregate());
    }

    @Test
    void testMakeNodeDuplicate() {
        lct.makeNode(1);
        assertThrows(IllegalArgumentException.class, () -> lct.makeNode(1));
    }

    @Test
    void testLinkAndConnected() {
        lct.makeNode(1);
        lct.makeNode(2);
        lct.link(1, 2);
        assertTrue(lct.connected(1, 2));
    }

    @Test
    void testNotConnected() {
        lct.makeNode(1);
        lct.makeNode(2);
        assertFalse(lct.connected(1, 2));
    }

    @Test
    void testCut() {
        lct.makeNode(1);
        lct.makeNode(2);
        lct.link(1, 2);
        assertTrue(lct.connected(1, 2));
        lct.cut(1);
        assertFalse(lct.connected(1, 2));
    }

    @Test
    void testFindRoot() {
        lct.makeNode(1);
        lct.makeNode(2);
        lct.makeNode(3);
        lct.link(1, 2);
        lct.link(2, 3);
        LctNode root = lct.findRoot(1);
        assertEquals(3, root.getId());
    }

    @Test
    void testMakeRoot() {
        lct.makeNode(1);
        lct.makeNode(2);
        lct.link(1, 2);
        lct.makeRoot(1);
        LctNode root = lct.findRoot(2);
        assertEquals(1, root.getId());
    }

    @Test
    void testPathAggregate() {
        lct.makeNode(1, 5);
        lct.makeNode(2, 10);
        lct.makeNode(3, 15);
        lct.link(1, 2);
        lct.link(2, 3);
        assertEquals(30, lct.pathAggregate(1, 3));
    }

    @Test
    void testUpdateNode() {
        lct.makeNode(1, 5);
        lct.updateNode(1, 100);
        assertEquals(100, lct.getNode(1).getValue());
    }

    @Test
    void testDynamicConnectivityAddEdge() {
        dcl.addVertex(1);
        dcl.addVertex(2);
        dcl.addVertex(3);
        dcl.addEdge(1, 2);
        dcl.addEdge(2, 3);
        assertTrue(dcl.isConnected(1, 3));
    }

    @Test
    void testDynamicConnectivityRemoveEdge() {
        dcl.addVertex(1);
        dcl.addVertex(2);
        dcl.addVertex(3);
        dcl.addEdge(1, 2);
        dcl.addEdge(2, 3);
        dcl.removeEdge(1, 2);
        assertFalse(dcl.isConnected(1, 2));
        assertTrue(dcl.isConnected(2, 3));
    }

    @Test
    void testDynamicConnectivityPathSum() {
        dcl.addVertex(1, 3);
        dcl.addVertex(2, 7);
        dcl.addVertex(3, 10);
        dcl.addEdge(1, 2);
        dcl.addEdge(2, 3);
        assertEquals(20, dcl.pathSum(1, 3));
    }

    @Test
    void testDynamicConnectivityUpdate() {
        dcl.addVertex(1, 5);
        dcl.addVertex(2, 10);
        dcl.addEdge(1, 2);
        dcl.updateVertex(1, 20);
        assertEquals(30, dcl.pathSum(1, 2));
    }

    @Test
    void testComplexTreeOperations() {
        for (int i = 1; i <= 10; i++) {
            lct.makeNode(i, i);
        }
        for (int i = 2; i <= 10; i++) {
            lct.link(i, i - 1);
        }
        assertTrue(lct.connected(1, 10));
        assertEquals(55, lct.pathAggregate(1, 10));
        lct.cut(5);
        assertFalse(lct.connected(4, 5));
    }
}
