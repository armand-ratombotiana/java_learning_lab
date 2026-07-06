package com.dsacademy.lab17.spatial;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class SpatialDataStructuresTest {

    @Test
    void testSpatialPoint() {
        SpatialPoint p1 = new SpatialPoint(0, 0);
        SpatialPoint p2 = new SpatialPoint(3, 4);
        assertEquals(5.0, p1.distanceTo(p2), 1e-9);
        assertEquals(25.0, p1.distanceSquared(p2), 1e-9);
    }

    @Test
    void testBoundingBox() {
        BoundingBox box = new BoundingBox(0, 0, 10, 10);
        assertTrue(box.contains(new SpatialPoint(5, 5)));
        assertFalse(box.contains(new SpatialPoint(15, 5)));
        assertTrue(box.intersects(new BoundingBox(5, 5, 15, 15)));
        assertFalse(box.intersects(new BoundingBox(20, 20, 30, 30)));
    }

    @Test
    void testQuadTreeInsertAndCount() {
        BoundingBox world = new BoundingBox(0, 0, 100, 100);
        QuadTree qt = new QuadTree(world);
        qt.insert(new SpatialPoint(25, 25));
        qt.insert(new SpatialPoint(75, 75));
        qt.insert(new SpatialPoint(10, 10));
        qt.insert(new SpatialPoint(90, 90));
        assertEquals(4, qt.totalPoints());
    }

    @Test
    void testQuadTreeOutOfBounds() {
        BoundingBox world = new BoundingBox(0, 0, 100, 100);
        QuadTree qt = new QuadTree(world);
        assertFalse(qt.insert(new SpatialPoint(150, 150)));
        assertEquals(0, qt.totalPoints());
    }

    @Test
    void testQuadTreeRangeQuery() {
        BoundingBox world = new BoundingBox(0, 0, 100, 100);
        QuadTree qt = new QuadTree(world);
        for (int i = 10; i <= 90; i += 20)
            for (int j = 10; j <= 90; j += 20)
                qt.insert(new SpatialPoint(i, j));
        BoundingBox query = new BoundingBox(0, 0, 50, 50);
        List<SpatialPoint> found = qt.queryRange(query);
        assertTrue(found.size() >= 4);
    }

    @Test
    void testQuadTreeNearestNeighbor() {
        BoundingBox world = new BoundingBox(0, 0, 100, 100);
        QuadTree qt = new QuadTree(world);
        qt.insert(new SpatialPoint(10, 10));
        qt.insert(new SpatialPoint(50, 50));
        qt.insert(new SpatialPoint(90, 90));
        SpatialPoint nearest = qt.nearestNeighbor(new SpatialPoint(55, 55));
        assertNotNull(nearest);
        assertEquals(50.0, nearest.x, 0.1);
    }

    @Test
    void testKdTreeInsertAndNearest() {
        KdTree kd = new KdTree();
        kd.insert(new SpatialPoint(10, 10));
        kd.insert(new SpatialPoint(50, 50));
        kd.insert(new SpatialPoint(90, 90));
        SpatialPoint nearest = kd.nearestNeighbor(new SpatialPoint(55, 55));
        assertNotNull(nearest);
        assertEquals(50.0, nearest.x, 0.1);
    }

    @Test
    void testKdTreeRangeSearch() {
        KdTree kd = new KdTree();
        for (int i = 0; i < 20; i++) kd.insert(new SpatialPoint(i * 5, i * 5));
        BoundingBox range = new BoundingBox(0, 0, 40, 40);
        List<SpatialPoint> result = kd.rangeSearch(range);
        assertFalse(result.isEmpty());
    }

    @Test
    void testKdTreeEmpty() {
        KdTree kd = new KdTree();
        assertTrue(kd.isEmpty());
        assertEquals(0, kd.size());
        assertNull(kd.nearestNeighbor(new SpatialPoint(0, 0)));
    }
}
