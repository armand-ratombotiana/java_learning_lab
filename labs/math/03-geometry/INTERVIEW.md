# Interview Questions on Geometry

## Easy

1. Check if two rectangles overlap.
2. Compute the area of a triangle given three points.
3. Determine if a point is inside a rectangle.

## Medium

4. Find the closest pair of points (divide and conquer).
5. Compute the convex hull of a set of points.
6. Determine if a polygon is convex.
7. Line segment intersection test.

## Hard

8. Implement Fortune's algorithm for Voronoi diagrams.
9. Delaunay triangulation.
10. Ray-triangle intersection in 3D.
11. Find the maximum number of collinear points.

## Java: Rectangle Overlap

```java
public boolean rectanglesOverlap(Point r1TopLeft, Point r1BotRight,
                                 Point r2TopLeft, Point r2BotRight) {
    return !(r1BotRight.x() <= r2TopLeft.x() ||
             r1TopLeft.x() >= r2BotRight.x() ||
             r1BotRight.y() <= r2TopLeft.y() ||
             r1TopLeft.y() >= r2BotRight.y());
}
```

## Java: Closest Pair (Divide & Conquer)

```java
public static double closestPair(List<Point> points) {
    List<Point> sortedX = points.stream()
        .sorted(Comparator.comparingDouble(Point::x)).toList();
    return closestPairRecursive(sortedX);
}
```
