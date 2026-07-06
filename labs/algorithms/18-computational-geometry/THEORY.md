# Computational Geometry — Theoretical Foundation

## Points and Vectors

A 2D point P = (x, y) can be treated as a vector from the origin. Vector operations include addition, subtraction, dot product, and cross product. The cross product of vectors a and b (a x b) gives the signed area of the parallelogram formed by the two vectors and the origin.

## Orientation Test

Given three points p, q, r, the orientation (clockwise, counterclockwise, or collinear) is determined by the cross product (q-p) x (r-p). A positive cross product indicates counterclockwise orientation; negative indicates clockwise; zero indicates collinear points.

## Convex Hull

The convex hull of a set of points is the smallest convex polygon containing all points. It is fundamental in computational geometry, appearing in collision detection, shape analysis, and optimization. Graham Scan and Andrew's monotone chain are O(n log n) algorithms.

## Graham Scan

Graham Scan sorts points by polar angle relative to the lowest point, then uses a stack to build the hull. It traverses sorted points, maintaining the invariant that consecutive triples make left turns. Right turns indicate that the middle point is not on the hull and is popped from the stack.

## Andrew's Monotone Chain

Andrew's algorithm sorts points by x (then y) coordinate and builds the upper and lower hulls separately. The lower hull is constructed by scanning left to right, maintaining left turns. The upper hull is constructed by scanning right to left. The two hulls are joined to form the complete convex hull.

## Closest Pair of Points

The divide-and-conquer algorithm recursively finds the closest pair in the left and right halves of points sorted by x-coordinate. The minimum distance delta from the two halves is used to filter points in the strip around the midline. Points in the strip are sorted by y, and only pairs within delta in y need to be checked.

## Line Segment Intersection

Two line segments AB and CD intersect if the orientations (A,B,C) and (A,B,D) are opposite and (C,D,A) and (C,D,B) are opposite. Collinear cases require checking bounding box overlap.
