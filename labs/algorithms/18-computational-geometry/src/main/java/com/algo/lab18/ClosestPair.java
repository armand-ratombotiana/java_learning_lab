package com.algo.lab18;

import java.util.*;

/**
 * Closest Pair of Points using divide and conquer.
 * Recursively finds the minimum distance between any two points.
 * Time: O(n log n), Space: O(n)
 */
public class ClosestPair {

    private ClosestPair() {}

    public static double closestDistance(List<Point2D> points) {
        if (points == null || points.size() < 2) return Double.POSITIVE_INFINITY;
        List<Point2D> sortedByX = new ArrayList<>(points);
        sortedByX.sort(Comparator.comparingDouble(Point2D::getX));
        return closest(sortedByX, 0, sortedByX.size() - 1);
    }

    private static double closest(List<Point2D> points, int left, int right) {
        if (right - left <= 3) {
            return bruteForce(points, left, right);
        }
        int mid = (left + right) / 2;
        double midX = points.get(mid).getX();
        double dl = closest(points, left, mid);
        double dr = closest(points, mid + 1, right);
        double d = Math.min(dl, dr);
        List<Point2D> strip = new ArrayList<>();
        for (int i = left; i <= right; i++) {
            if (Math.abs(points.get(i).getX() - midX) < d) {
                strip.add(points.get(i));
            }
        }
        strip.sort(Comparator.comparingDouble(Point2D::getY));
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).getY() - strip.get(i).getY()) < d; j++) {
                double dist = strip.get(i).distance(strip.get(j));
                if (dist < d) d = dist;
            }
        }
        return d;
    }

    private static double bruteForce(List<Point2D> points, int left, int right) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                double dist = points.get(i).distance(points.get(j));
                if (dist < min) min = dist;
            }
        }
        return min;
    }
}
