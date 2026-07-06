package com.algo.lab18;

import java.util.*;

/**
 * Graham Scan algorithm for computing the convex hull of a set of points.
 * Sorts by polar angle and uses a stack to maintain the hull.
 * Time: O(n log n), Space: O(n)
 */
public class GrahamScan {

    private GrahamScan() {}

    public static List<Point2D> convexHull(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            return points == null ? List.of() : new ArrayList<>(points);
        }
        Point2D pivot = points.get(0);
        for (Point2D p : points) {
            if (p.getY() < pivot.getY() || (p.getY() == pivot.getY() && p.getX() < pivot.getX())) {
                pivot = p;
            }
        }
        final Point2D finalPivot = pivot;
        List<Point2D> sorted = new ArrayList<>(points);
        sorted.remove(pivot);
        sorted.sort((a, b) -> {
            double angle = Point2D.cross(finalPivot, a, b);
            if (Math.abs(angle) < 1e-9) {
                return Double.compare(finalPivot.distance(a), finalPivot.distance(b));
            }
            return angle > 0 ? -1 : 1;
        });
        Deque<Point2D> stack = new ArrayDeque<>();
        stack.push(pivot);
        for (Point2D p : sorted) {
            while (stack.size() >= 2) {
                Point2D top = stack.pop();
                Point2D second = stack.peek();
                if (Point2D.cross(second, top, p) > 1e-9) {
                    stack.push(top);
                    break;
                }
            }
            stack.push(p);
        }
        return new ArrayList<>(stack);
    }
}
