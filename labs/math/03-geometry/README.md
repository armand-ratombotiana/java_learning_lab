# Geometry

The study of shapes, sizes, positions, and properties of space.

## Scope

- Euclidean geometry: points, lines, angles, polygons, circles
- Coordinate geometry: distance, midpoint, slope
- Transformations: translation, rotation, scaling, reflection
- 3D geometry: spheres, cubes, polyhedra
- Computational geometry: convex hull, closest pair

## Java Implementation

```java
public record Point(double x, double y) {
    public double distanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }
}
```
