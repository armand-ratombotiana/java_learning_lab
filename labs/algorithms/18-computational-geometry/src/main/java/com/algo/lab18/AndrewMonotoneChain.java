package com.algo.lab18;

import java.util.*;

/**
 * Andrew's Monotone Chain algorithm for convex hull.
 * Builds the upper and lower hulls separately for a clean O(n log n) implementation.
 * Time: O(n log n), Space: O(n)
 */
public class AndrewMonotoneChain {

    private AndrewMonotoneChain() {}

    public static List<Point2D> convexHull(List<Point2D> points) {
        if (points == null || points.size() < 3) {
            return points == null ? List.of() : new ArrayList<>(points);
        }
        List<Point2D> sorted = new ArrayList<>(points);
        sorted.sort((a, b) -> {
            int cmp = Double.compare(a.getX(), b.getX());
            return cmp != 0 ? cmp : Double.compare(a.getY(), b.getY());
        });
        List<Point2D> lower = new ArrayList<>();
        for (Point2D p : sorted) {
            while (lower.size() >= 2 && Point2D.cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(p);
        }
        List<Point2D> upper = new ArrayList<>();
        for (int i = sorted.size() - 1; i >= 0; i--) {
            Point2D p = sorted.get(i);
            while (upper.size() >= 2 && Point2D.cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.remove(upper.size() - 1);
            }
            upper.add(p);
        }
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);
        return lower;
    }
}
