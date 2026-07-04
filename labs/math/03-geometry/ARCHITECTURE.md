# Architecture of Geometry

## Java Geometry Libraries

```
java.awt.geom
├── Point2D (abstract)
│   ├── Point2D.Double
│   └── Point2D.Float
├── Shape (interface)
│   ├── Rectangle2D
│   ├── Ellipse2D
│   ├── Line2D
│   ├── CubicCurve2D
│   ├── QuadCurve2D
│   ├── Path2D
│   └── Polygon
├── AffineTransform
└── Area (boolean ops)
```

## Geometry Pipeline (Computer Graphics)

```
3D Scene
  → Model Transform (local → world)
  → View Transform (world → camera)
  → Projection Transform (3D → 2D)
  → Clipping
  → Rasterization (2D → pixels)
```

## Custom Geometry Hierarchy

```java
interface Geometry {
    double area();
    double perimeter();
    boolean contains(Point p);
    Geometry transform(Transform t);
}
```
