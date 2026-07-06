package com.algo.lab18;

/**
 * Line segment intersection detection using orientation.
 * Checks whether two line segments intersect (including endpoints).
 * Time: O(1), Space: O(1)
 */
public class LineIntersection {

    private LineIntersection() {}

    public static boolean intersect(Point2D a, Point2D b, Point2D c, Point2D d) {
        int o1 = orientation(a, b, c);
        int o2 = orientation(a, b, d);
        int o3 = orientation(c, d, a);
        int o4 = orientation(c, d, b);
        if (o1 != o2 && o3 != o4) return true;
        if (o1 == 0 && onSegment(a, b, c)) return true;
        if (o2 == 0 && onSegment(a, b, d)) return true;
        if (o3 == 0 && onSegment(c, d, a)) return true;
        if (o4 == 0 && onSegment(c, d, b)) return true;
        return false;
    }

    public static Point2D intersectionPoint(Point2D a, Point2D b, Point2D c, Point2D d) {
        if (!intersect(a, b, c, d)) return null;
        double a1 = b.getY() - a.getY();
        double b1 = a.getX() - b.getX();
        double c1 = a1 * a.getX() + b1 * a.getY();
        double a2 = d.getY() - c.getY();
        double b2 = c.getX() - d.getX();
        double c2 = a2 * c.getX() + b2 * c.getY();
        double det = a1 * b2 - a2 * b1;
        if (Math.abs(det) < 1e-9) return null;
        double x = (b2 * c1 - b1 * c2) / det;
        double y = (a1 * c2 - a2 * c1) / det;
        return new Point2D(x, y);
    }

    private static int orientation(Point2D p, Point2D q, Point2D r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (Math.abs(val) < 1e-9) return 0;
        return val > 0 ? 1 : 2;
    }

    private static boolean onSegment(Point2D p, Point2D q, Point2D r) {
        return r.getX() <= Math.max(p.getX(), q.getX()) && r.getX() >= Math.min(p.getX(), q.getX())
            && r.getY() <= Math.max(p.getY(), q.getY()) && r.getY() >= Math.min(p.getY(), q.getY());
    }
}
