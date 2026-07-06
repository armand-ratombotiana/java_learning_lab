package com.dsacademy.lab17.spatial;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int CAPACITY = 4;

    private final BoundingBox boundary;
    private final List<SpatialPoint> points;
    private QuadTree northWest, northEast, southWest, southEast;
    private boolean divided;

    public QuadTree(BoundingBox boundary) {
        this.boundary = boundary;
        this.points = new ArrayList<>();
        this.divided = false;
    }

    public boolean insert(SpatialPoint p) {
        if (!boundary.contains(p)) return false;
        if (points.size() < CAPACITY) { points.add(p); return true; }
        if (!divided) subdivide();
        return northWest.insert(p) || northEast.insert(p) || southWest.insert(p) || southEast.insert(p);
    }

    private void subdivide() {
        double xMid = (boundary.xMin + boundary.xMax) / 2;
        double yMid = (boundary.yMin + boundary.yMax) / 2;
        northWest = new QuadTree(new BoundingBox(boundary.xMin, yMid, xMid, boundary.yMax));
        northEast = new QuadTree(new BoundingBox(xMid, yMid, boundary.xMax, boundary.yMax));
        southWest = new QuadTree(new BoundingBox(boundary.xMin, boundary.yMin, xMid, yMid));
        southEast = new QuadTree(new BoundingBox(xMid, boundary.yMin, boundary.xMax, yMid));
        divided = true;
    }

    public List<SpatialPoint> queryRange(BoundingBox range) {
        List<SpatialPoint> found = new ArrayList<>();
        if (!boundary.intersects(range)) return found;
        for (SpatialPoint p : points) { if (range.contains(p)) found.add(p); }
        if (divided) {
            found.addAll(northWest.queryRange(range));
            found.addAll(northEast.queryRange(range));
            found.addAll(southWest.queryRange(range));
            found.addAll(southEast.queryRange(range));
        }
        return found;
    }

    public SpatialPoint nearestNeighbor(SpatialPoint target) {
        return nearestNeighbor(target, null, Double.MAX_VALUE);
    }

    private SpatialPoint nearestNeighbor(SpatialPoint target, SpatialPoint best, double bestDist) {
        for (SpatialPoint p : points) {
            double d = target.distanceSquared(p);
            if (d < bestDist) { best = p; bestDist = d; }
        }
        if (divided) {
            QuadTree[] children = {northWest, northEast, southWest, southEast};
            for (QuadTree child : children) {
                double minDist = minDistToBoundary(target, child.boundary);
                if (minDist < bestDist) {
                    best = child.nearestNeighbor(target, best, bestDist);
                    if (best != null) bestDist = target.distanceSquared(best);
                }
            }
        }
        return best;
    }

    private double minDistToBoundary(SpatialPoint p, BoundingBox b) {
        double dx = Math.max(0, Math.max(b.xMin - p.x, p.x - b.xMax));
        double dy = Math.max(0, Math.max(b.yMin - p.y, p.y - b.yMax));
        return dx * dx + dy * dy;
    }

    public int totalPoints() {
        int count = points.size();
        if (divided) {
            count += northWest.totalPoints() + northEast.totalPoints();
            count += southWest.totalPoints() + southEast.totalPoints();
        }
        return count;
    }
}
