# Code Deep Dive — Computational Geometry

## Robustness Issues with Floating Point

Floating-point arithmetic introduces precision errors. The EPS (epsilon) constant is used for comparisons. Choosing the right epsilon is crucial: too small and legitimate near-collinearities are missed; too large and non-collinear points are incorrectly classified. A typical epsilon is 1e-9 for double precision.

## Integer Coordinate Alternative

For exact computation, use integer coordinates and compare cross products without division. This avoids floating-point issues entirely but limits the coordinate range. Java's BigInteger can handle arbitrary precision.

## Closest Pair Implementation Detail

The strip check only needs 7 comparisons per point because the points in the strip are sorted by y, and any point can have at most 7 other points within a delta x delta square. This is a key optimization that maintains the O(n log n) bound.

## Monotone Chain vs Graham Scan

Monotone chain avoids computing polar angles (which require trigonometric functions or atan2), making it faster and more numerically stable. It also handles collinear points more consistently.
