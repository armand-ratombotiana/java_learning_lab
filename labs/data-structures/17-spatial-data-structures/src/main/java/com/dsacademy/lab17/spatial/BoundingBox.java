package com.dsacademy.lab17.spatial;

public class BoundingBox {
    public final double xMin, yMin, xMax, yMax;

    public BoundingBox(double xMin, double yMin, double xMax, double yMax) {
        if (xMin > xMax || yMin > yMax)
            throw new IllegalArgumentException("Invalid bounds");
        this.xMin = xMin; this.yMin = yMin; this.xMax = xMax; this.yMax = yMax;
    }

    public boolean contains(SpatialPoint p) {
        return p.x >= xMin && p.x <= xMax && p.y >= yMin && p.y <= yMax;
    }

    public boolean intersects(BoundingBox other) {
        return !(other.xMin > xMax || other.xMax < xMin || other.yMin > yMax || other.yMax < yMin);
    }

    public double width() { return xMax - xMin; }
    public double height() { return yMax - yMin; }
    public double area() { return width() * height(); }

    public boolean contains(BoundingBox other) {
        return other.xMin >= xMin && other.xMax <= xMax && other.yMin >= yMin && other.yMax <= yMax;
    }
}
