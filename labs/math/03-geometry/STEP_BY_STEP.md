# Step-by-Step: Geometry in Java

## Compute Distance Between Two Points

```java
public static double distance(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
}
```

## Check if Three Points are Collinear

```java
public static boolean areCollinear(Point a, Point b, Point c) {
    // Area of triangle = 0 means collinear (shoelace)
    double area = a.x() * (b.y() - c.y())
                + b.x() * (c.y() - a.y())
                + c.x() * (a.y() - b.y());
    return Math.abs(area) < 1e-10;
}
```

## Rotate a Point Around Origin

```java
public static Point rotate(Point p, double angleRadians) {
    double cos = Math.cos(angleRadians);
    double sin = Math.sin(angleRadians);
    return new Point(
        p.x() * cos - p.y() * sin,
        p.x() * sin + p.y() * cos
    );
}
```

## Line-Line Intersection

```java
public static Point lineIntersection(Point p1, Point p2, Point p3, Point p4) {
    double d = (p1.x() - p2.x()) * (p3.y() - p4.y())
             - (p1.y() - p2.y()) * (p3.x() - p4.x());
    if (Math.abs(d) < 1e-10) return null; // parallel
    double t = ((p1.x() - p3.x()) * (p3.y() - p4.y())
              - (p1.y() - p3.y()) * (p3.x() - p4.x())) / d;
    return new Point(p1.x() + t * (p2.x() - p1.x()),
                     p1.y() + t * (p2.y() - p1.y()));
}
```
