package com.algorithms.geometry;

import java.util.Arrays;
import java.util.Stack;

/**
 * Custom: Convex Hull using Andrew's Monotone Chain Algorithm
 * Find the convex hull of a set of 2D points.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class ConvexHullMonotoneChain {

    static class Point implements Comparable<Point> {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        public int compareTo(Point p) { return x != p.x ? x - p.x : y - p.y; }
    }

    private int cross(Point o, Point a, Point b) {
        return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
    }

    public Point[] hull(Point[] points) {
        int n = points.length;
        if (n <= 1) return points;
        Arrays.sort(points);

        Stack<Point> lower = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.peek(), points[i]) <= 0) lower.pop();
            lower.push(points[i]);
        }

        Stack<Point> upper = new Stack<>();
        for (int i = n - 1; i >= 0; i--) {
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.peek(), points[i]) <= 0) upper.pop();
            upper.push(points[i]);
        }

        Point[] result = new Point[lower.size() + upper.size() - 2];
        int idx = 0;
        for (int i = 0; i < lower.size(); i++) result[idx++] = lower.get(i);
        for (int i = 1; i < upper.size() - 1; i++) result[idx++] = upper.get(i);
        return Arrays.copyOf(result, idx);
    }

    public static void main(String[] args) {
        ConvexHullMonotoneChain ch = new ConvexHullMonotoneChain();
        Point[] pts = { new Point(0, 0), new Point(1, 1), new Point(2, 2), new Point(2, 0), new Point(0, 2) };
        Point[] hull = ch.hull(pts);
        System.out.print("Convex hull: ");
        for (Point p : hull) System.out.print("(" + p.x + "," + p.y + ") ");
        System.out.println();
    }
}
