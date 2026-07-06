package com.algo.lab18;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ComputationalGeometryTest {

    @Test
    void testPoint2DAdd() {
        Point2D a = new Point2D(1, 2);
        Point2D b = new Point2D(3, 4);
        assertEquals(new Point2D(4, 6), a.add(b));
    }

    @Test
    void testPoint2DSubtract() {
        Point2D a = new Point2D(5, 7);
        Point2D b = new Point2D(2, 3);
        assertEquals(new Point2D(3, 4), a.subtract(b));
    }

    @Test
    void testPoint2DDot() {
        Point2D a = new Point2D(1, 2);
        Point2D b = new Point2D(3, 4);
        assertEquals(11, a.dot(b), 1e-9);
    }

    @Test
    void testPoint2DCross() {
        Point2D a = new Point2D(1, 2);
        Point2D b = new Point2D(3, 4);
        assertEquals(-2, a.cross(b), 1e-9);
    }

    @Test
    void testPoint2DDistance() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(3, 4);
        assertEquals(5, a.distance(b), 1e-9);
    }

    @Test
    void testGrahamScanBasic() {
        List<Point2D> points = Arrays.asList(
            new Point2D(0, 0), new Point2D(1, 1), new Point2D(2, 0),
            new Point2D(1, -1), new Point2D(2, 2), new Point2D(0, 2)
        );
        List<Point2D> hull = GrahamScan.convexHull(points);
        assertTrue(hull.size() >= 3);
    }

    @Test
    void testGrahamScanLessThan3() {
        List<Point2D> points = Arrays.asList(new Point2D(0, 0), new Point2D(1, 1));
        assertEquals(2, GrahamScan.convexHull(points).size());
    }

    @Test
    void testGrahamScanNull() {
        assertTrue(GrahamScan.convexHull(null).isEmpty());
    }

    @Test
    void testAndrewMonotoneChainBasic() {
        List<Point2D> points = Arrays.asList(
            new Point2D(0, 0), new Point2D(1, 1), new Point2D(2, 0),
            new Point2D(1, -1), new Point2D(2, 2), new Point2D(0, 2)
        );
        List<Point2D> hull = AndrewMonotoneChain.convexHull(points);
        assertTrue(hull.size() >= 3);
    }

    @Test
    void testAndrewMonotoneChainCollinear() {
        List<Point2D> points = Arrays.asList(
            new Point2D(0, 0), new Point2D(1, 0), new Point2D(2, 0)
        );
        List<Point2D> hull = AndrewMonotoneChain.convexHull(points);
        assertEquals(2, hull.size());
    }

    @Test
    void testClosestPairBasic() {
        List<Point2D> points = Arrays.asList(
            new Point2D(0, 0), new Point2D(1, 1), new Point2D(3, 3), new Point2D(0.5, 0.5)
        );
        double d = ClosestPair.closestDistance(points);
        assertTrue(d > 0 && d < 1);
    }

    @Test
    void testClosestPairSinglePoint() {
        List<Point2D> points = List.of(new Point2D(0, 0));
        assertEquals(Double.POSITIVE_INFINITY, ClosestPair.closestDistance(points), 1e-9);
    }

    @Test
    void testClosestPairNull() {
        assertEquals(Double.POSITIVE_INFINITY, ClosestPair.closestDistance(null), 1e-9);
    }

    @Test
    void testClosestPairSamePoint() {
        List<Point2D> points = Arrays.asList(new Point2D(1, 1), new Point2D(1, 1));
        assertEquals(0, ClosestPair.closestDistance(points), 1e-9);
    }

    @Test
    void testLineIntersectionBasic() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(2, 2);
        Point2D c = new Point2D(0, 2);
        Point2D d = new Point2D(2, 0);
        assertTrue(LineIntersection.intersect(a, b, c, d));
    }

    @Test
    void testLineIntersectionParallel() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(1, 0);
        Point2D c = new Point2D(0, 1);
        Point2D d = new Point2D(1, 1);
        assertFalse(LineIntersection.intersect(a, b, c, d));
    }

    @Test
    void testLineIntersectionEndpoint() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(1, 1);
        Point2D c = new Point2D(1, 1);
        Point2D d = new Point2D(2, 0);
        assertTrue(LineIntersection.intersect(a, b, c, d));
    }

    @Test
    void testLineIntersectionPoint() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(2, 2);
        Point2D c = new Point2D(0, 2);
        Point2D d = new Point2D(2, 0);
        Point2D p = LineIntersection.intersectionPoint(a, b, c, d);
        assertNotNull(p);
        assertEquals(1, p.getX(), 1e-9);
        assertEquals(1, p.getY(), 1e-9);
    }

    @Test
    void testLineIntersectionNoPoint() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(1, 0);
        Point2D c = new Point2D(0, 1);
        Point2D d = new Point2D(1, 1);
        assertNull(LineIntersection.intersectionPoint(a, b, c, d));
    }
}
