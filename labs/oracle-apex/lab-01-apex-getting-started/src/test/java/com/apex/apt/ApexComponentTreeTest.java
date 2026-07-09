package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexComponentTreeTest {
    private ApexComponentTree tree;
    @BeforeEach void setUp() { tree = ApexComponentTree.createSampleTree(); }

    @Test void shouldBuildSampleTree() {
        assertEquals(6, tree.size());
    }

    @Test void shouldFindPageChildren() {
        var children = tree.getChildren("p1");
        assertEquals(2, children.size());
        assertTrue(children.stream().anyMatch(c -> c.type() == ApexComponentTree.ComponentType.REGION));
    }

    @Test void shouldFindByType() {
        var regions = tree.getByType(ApexComponentTree.ComponentType.REGION);
        assertEquals(2, regions.size());
        var items = tree.getByType(ApexComponentTree.ComponentType.ITEM);
        assertEquals(1, items.size());
    }

    @Test void shouldGetComponent() {
        var comp = tree.getComponent("p1");
        assertNotNull(comp);
        assertEquals("Dashboard", comp.name());
    }

    @Test void shouldBuildTreeMap() {
        var built = tree.buildTree();
        assertTrue(built.containsKey("p1"));
    }
}