# Computational Geometry — Internal Mechanics

## Point and Vector Operations

`java
public record Point(double x, double y) {
    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }
    public double cross(Point p) {
        return x * p.y - y * p.x;
    }
    public double dot(Point p) {
        return x * p.x + y * p.y;
    }
    public double distance(Point p) {
        double dx = x - p.x, dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
`

## Orientation Test

`java
int orientation(Point p, Point q, Point r) {
    double val = (q.x - p.x) * (r.y - p.y) - (q.y - p.y) * (r.x - p.x);
    if (Math.abs(val) < EPS) return 0;  // collinear
    return (val > 0) ? 1 : 2;           // CCW or CW
}
`

## Monotone Chain Implementation

`java
List<Point> convexHull(Point[] points) {
    int n = points.length;
    Arrays.sort(points, (a, b) -> 
        a.x != b.x ? Double.compare(a.x, b.x) : Double.compare(a.y, b.y));
    List<Point> hull = new ArrayList<>();
    for (int phase = 0; phase < 2; phase++) {
        int start = hull.size();
        for (Point p : points) {
            while (hull.size() >= start + 2 &&
                   orientation(hull.get(hull.size()-2), 
                              hull.get(hull.size()-1), p) != 2)
                hull.remove(hull.size() - 1);
            hull.add(p);
        }
        hull.remove(hull.size() - 1);
        if (phase == 0) Collections.reverse(Arrays.asList(points));
    }
    return hull;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Computational Geometry

## Cross Product Properties

The cross product a x b = |a||b|sin(theta) gives:
- Positive if b is counterclockwise from a
- Negative if b is clockwise from a
- Zero if a and b are collinear
- Magnitude gives twice the area of triangle (0, a, b)

## Convex Hull Size

For n points uniformly distributed in a convex region, the expected number of hull vertices is O(log n). For points on a circle, the hull includes all n points.

## Closest Pair Strip

The strip width delta ensures that any pair closer than delta must lie within delta of the midline. In a delta x 2delta rectangle, at most 8 points can exist with mutual distances >= delta (pigeonhole principle), so only 7 comparisons per point in the strip are needed.

## Line Intersection Determinant

The intersection point of lines AB and CD can be computed using Cramer's rule with determinants. The denominator is zero if lines are parallel.
