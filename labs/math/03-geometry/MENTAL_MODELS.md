# Mental Models for Geometry

## The Grid (Coordinate Plane)

Every point has an address $(x, y)$. Distance between points is the Pythagorean theorem:

$$
d = \sqrt{(x_2 - x_1)^2 + (y_2 - y_1)^2}
$$

## The Compass and Straightedge

Classical geometry: draw circles and straight lines. All constructions reduce to these two operations.

## Shapes as Constraints

A circle is the set of points at a fixed distance from a center. A square is a rectangle with equal sides. Formal geometry defines shapes by their defining equations.

## The Transformation Lens

Geometry can be studied through what stays invariant under transformations:

- **Isometry**: distance preserved (rotation, translation, reflection)
- **Similarity**: shape preserved, size may change (scaling)
- **Affine**: parallelism preserved (shear)
- **Projective**: straight lines remain straight (perspective)

## In Java: The Shape API

```java
// java.awt.geom models geometric primitives
Shape rect = new Rectangle2D.Double(x, y, w, h);
Shape circle = new Ellipse2D.Double(cx - r, cy - r, 2*r, 2*r);
```
