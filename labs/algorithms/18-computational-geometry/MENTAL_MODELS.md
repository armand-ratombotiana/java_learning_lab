# Mental Models for Computational Geometry

## Orientation Test — "The Turn"

When driving from point A to B to C, the orientation test tells you whether you turn left (counterclockwise), turn right (clockwise), or go straight (collinear). This is fundamental to all geometric algorithms.

## Convex Hull — "Rubber Band"

Imagine wrapping a rubber band around a set of nails on a board. The rubber band contracts to touch only the outermost nails, forming the convex hull. Any nail inside the band is not part of the hull.

## Closest Pair — "Split and Conquer"

Divide the points vertically by x-coordinate, find the closest pair in each half, then check only a narrow strip around the division line. This is analogous to sorting: split, solve, merge.

## Line Intersection — "Orientation Check"

Two line segments intersect if and only if each segment straddles the line containing the other segment. The orientation test at the endpoints determines straddling.
