# Refactoring Geometry Code

## Encapsulate Point Operations

```java
// BEFORE
double dx = x2 - x1;
double dy = y2 - y1;
double d = Math.sqrt(dx * dx + dy * dy);

// AFTER
record Point(double x, double y) {
    double distanceTo(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}
```

## Separate Shape Hierarchy

```java
// BEFORE: scattered area calculations
double circleArea = Math.PI * r * r;
double rectArea = w * h;

// AFTER
interface Shape { double area(); }
record Circle(double radius) implements Shape {
    public double area() { return Math.PI * radius * radius; }
}
record Rectangle(double w, double h) implements Shape {
    public double area() { return w * h; }
}
```

## Use Immutable Transforms

```java
public record Transform(double a, double b, double c, double d, double e, double f) {
    public Point apply(Point p) {
        return new Point(a * p.x() + c * p.y() + e,
                         b * p.x() + d * p.y() + f);
    }
    public Transform andThen(Transform t) { ... } // matrix multiply
}
```
