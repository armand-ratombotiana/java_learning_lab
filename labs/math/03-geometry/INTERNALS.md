# Internals of Geometry

## Computational Geometry Data Structures

### KD-Tree for Nearest Neighbor

```java
public class KDTree {
    private record Node(Point point, Node left, Node right) {}

    public Node build(List<Point> points, int depth) {
        if (points.isEmpty()) return null;
        int axis = depth % 2; // 0=x, 1=y
        points.sort(Comparator.comparingDouble(p -> axis == 0 ? p.x() : p.y()));
        int mid = points.size() / 2;
        return new Node(
            points.get(mid),
            build(points.subList(0, mid), depth + 1),
            build(points.subList(mid + 1, points.size()), depth + 1)
        );
    }
}
```

### Convex Hull (Graham Scan)

```java
public static List<Point> convexHull(List<Point> points) {
    Point pivot = points.stream().min(Comparator.comparing(Point::y)
        .thenComparing(Point::x)).get();
    points.sort((a, b) -> {
        double angleA = Math.atan2(a.y() - pivot.y(), a.x() - pivot.x());
        double angleB = Math.atan2(b.y() - pivot.y(), b.x() - pivot.x());
        return Double.compare(angleA, angleB);
    });
    Stack<Point> hull = new Stack<>();
    hull.push(points.get(0));
    hull.push(points.get(1));
    for (int i = 2; i < points.size(); i++) {
        while (crossProduct(hull.get(hull.size()-2), hull.peek(), points.get(i)) <= 0)
            hull.pop();
        hull.push(points.get(i));
    }
    return hull;
}
```

## Geometric Transformations as Matrices

Translation, rotation, scaling all expressed as $4 \times 4$ matrices (homogeneous coordinates) for composition.
