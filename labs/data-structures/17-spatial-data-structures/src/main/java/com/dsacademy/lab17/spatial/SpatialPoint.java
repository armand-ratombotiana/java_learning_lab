package com.dsacademy.lab17.spatial;

import java.util.ArrayList;
import java.util.List;

public class SpatialPoint {
    public final double x;
    public final double y;

    public SpatialPoint(double x, double y) { this.x = x; this.y = y; }

    public double distanceTo(SpatialPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceSquared(SpatialPoint other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return dx * dx + dy * dy;
    }

    @Override
    public String toString() { return "(" + x + ", " + y + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpatialPoint)) return false;
        SpatialPoint that = (SpatialPoint) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
    }
}
