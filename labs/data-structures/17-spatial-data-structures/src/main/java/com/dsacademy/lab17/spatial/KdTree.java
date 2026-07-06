package com.dsacademy.lab17.spatial;

import java.util.ArrayList;
import java.util.List;

public class KdTree {
    private static class Node {
        SpatialPoint point;
        Node left, right;
        Node(SpatialPoint p) { this.point = p; }
    }

    private Node root;
    private int size;

    public void insert(SpatialPoint p) { root = insert(root, p, 0); size++; }

    private Node insert(Node node, SpatialPoint p, int depth) {
        if (node == null) return new Node(p);
        int cd = depth % 2;
        if (cd == 0) {
            if (p.x < node.point.x) node.left = insert(node.left, p, depth + 1);
            else node.right = insert(node.right, p, depth + 1);
        } else {
            if (p.y < node.point.y) node.left = insert(node.left, p, depth + 1);
            else node.right = insert(node.right, p, depth + 1);
        }
        return node;
    }

    public SpatialPoint nearestNeighbor(SpatialPoint target) {
        if (root == null) return null;
        return nearestNeighbor(root, target, null, Double.MAX_VALUE, 0);
    }

    private SpatialPoint nearestNeighbor(Node node, SpatialPoint target, SpatialPoint best, double bestDist, int depth) {
        if (node == null) return best;
        double d = target.distanceSquared(node.point);
        if (d < bestDist) { best = node.point; bestDist = d; }
        int cd = depth % 2;
        Node first, second;
        double delta;
        if (cd == 0) {
            delta = target.x - node.point.x;
            if (delta < 0) { first = node.left; second = node.right; }
            else { first = node.right; second = node.left; }
        } else {
            delta = target.y - node.point.y;
            if (delta < 0) { first = node.left; second = node.right; }
            else { first = node.right; second = node.left; }
        }
        best = nearestNeighbor(first, target, best, bestDist, depth + 1);
        if (best != null) bestDist = target.distanceSquared(best);
        if (delta * delta < bestDist) {
            best = nearestNeighbor(second, target, best, bestDist, depth + 1);
        }
        return best;
    }

    public List<SpatialPoint> rangeSearch(BoundingBox range) {
        List<SpatialPoint> result = new ArrayList<>();
        rangeSearch(root, range, result, 0);
        return result;
    }

    private void rangeSearch(Node node, BoundingBox range, List<SpatialPoint> result, int depth) {
        if (node == null) return;
        if (range.contains(node.point)) result.add(node.point);
        int cd = depth % 2;
        double val = (cd == 0) ? node.point.x : node.point.y;
        double min = (cd == 0) ? range.xMin : range.yMin;
        double max = (cd == 0) ? range.xMax : range.yMax;
        if (val >= min) rangeSearch(node.left, range, result, depth + 1);
        if (val <= max) rangeSearch(node.right, range, result, depth + 1);
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
