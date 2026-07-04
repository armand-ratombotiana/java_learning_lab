# How Geometry Works

## Euclidean Axioms

Euclid built geometry on 5 postulates:
1. A straight line can be drawn between any two points
2. Any line can be extended indefinitely
3. A circle can be drawn with any center and radius
4. All right angles are equal
5. The parallel postulate (controversial → non-Euclidean geometries)

## Key Formulas

### Distance
$$
d = \sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2}
$$

### Area of Polygon
Shoelace formula:
$$
A = \frac{1}{2} \left|\sum_{i=1}^{n} (x_i y_{i+1} - x_{i+1} y_i)\right|
$$

```java
public static double polygonArea(List<Point> points) {
    double sum = 0;
    int n = points.size();
    for (int i = 0; i < n; i++) {
        Point p1 = points.get(i);
        Point p2 = points.get((i + 1) % n);
        sum += p1.x() * p2.y() - p2.x() * p1.y();
    }
    return Math.abs(sum) / 2.0;
}
```

### Point in Polygon (Ray Casting)

```java
public static boolean pointInPolygon(Point p, List<Point> polygon) {
    boolean inside = false;
    int n = polygon.size();
    for (int i = 0, j = n - 1; i < n; j = i++) {
        if ((polygon.get(i).y() > p.y()) != (polygon.get(j).y() > p.y()) &&
            p.x() < (polygon.get(j).x() - polygon.get(i).x()) *
            (p.y() - polygon.get(i).y()) /
            (polygon.get(j).y() - polygon.get(i).y()) +
            polygon.get(i).x())
            inside = !inside;
    }
    return inside;
}
```
