$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\18-computational-geometry"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Computational Geometry — Overview

This lab covers fundamental computational geometry algorithms: 2D points and vectors, convex hull construction (Graham Scan, Andrew's monotone chain, Jarvis March), closest pair of points, and line segment intersection. Computational geometry is the foundation of computer graphics, robotics, GIS, and computer-aided design.

## Learning Objectives

- Implement 2D point and vector operations
- Build convex hull using Graham Scan and monotone chain
- Solve closest pair of points using divide and conquer
- Detect line segment intersections
- Understand orientation and cross product tests

## Prerequisites

- Basic trigonometry and coordinate geometry
- Sorting algorithms
- Divide and conquer paradigm
"@

wf "THEORY.md" @"
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
"@

wf "WHY_IT_EXISTS.md" @"
# Why Computational Geometry Algorithms Exist

Computational geometry emerged as a distinct field in the 1970s, driven by the need to solve geometric problems efficiently on computers. Before specialized algorithms, geometric problems were solved using general computational approaches that failed to leverage the structure of geometric space.

The field was formalized by Michael Shamos in his 1978 Ph.D. thesis, which introduced many fundamental problems and their solutions. The convex hull problem is the "sorting" of computational geometry — it is a fundamental building block that appears in countless applications.

The closest pair problem has applications in collision detection in physics simulations, clustering in data mining, and proximity analysis in GIS. The naive O(n^2) solution becomes impractical for large point sets (millions of points from LIDAR scans).

Line segment intersection detection is essential for VLSI design (checking wire crossings), GIS (overlay analysis), and computer graphics (visibility determination). The sweep-line algorithm by Shamos and Hoey (1976) provided an O((n+k) log n) solution where k is the number of intersections.

Graham Scan (1972) was one of the first efficient convex hull algorithms. It demonstrated how sorting could be leveraged to reduce computational complexity from O(n^2) to O(n log n). Ronald Graham developed it for computational number theory, not realizing it would become a cornerstone of computational geometry.

Andrew's monotone chain (1979) simplified Graham Scan by avoiding polar angle computation, instead using just x-coordinate sorting. This eliminates the need for trigonometric functions and handle collinear points more gracefully.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Computational Geometry Matters

Computational geometry algorithms are essential infrastructure in graphics, CAD, robotics, and GIS. A video game rendering a 3D scene performs millions of geometric computations per frame. A self-driving car uses geometric algorithms to detect obstacles and plan paths.

## Practical Applications

Convex hulls are used in collision detection to simplify complex shapes into convex bounding polygons. The closest pair algorithm is used in astronomy to find pairs of stars that might be binary systems. Line intersection detection is crucial in PCB design to detect unwanted wire crossings.

## Performance at Scale

For 100 million points from a LIDAR scan, the naive O(n^2) closest pair algorithm would take years. The O(n log n) divide-and-conquer algorithm takes minutes. For real-time applications like collision detection in physics engines, nanosecond-level geometric tests are needed.

## Foundation for Advanced Topics

Computational geometry provides the foundation for Delaunay triangulation, Voronoi diagrams, range searching, point location, and mesh generation. These are used in finite element analysis, terrain modeling, weather simulation, and medical imaging.
"@

wf "HISTORY.md" @"
# History of Computational Geometry

1972: Ronald Graham published Graham Scan for convex hull construction.

1975: Michael Shamos introduced the closest pair divide-and-conquer algorithm.

1976: Shamos and Hoey published the line segment intersection sweep-line algorithm.

1978: Shamos' Ph.D. thesis "Computational Geometry" established the field.

1979: Andrew's monotone chain simplified convex hull construction.

1983: Kirkpatrick's hierarchical triangulation for point location.

1985: Chazelle's linear-time triangulation of simple polygons.

1988: de Berg et al. "Computational Geometry: Algorithms and Applications" became the standard textbook.

1990s: Randomized incremental algorithms for Delaunay triangulation and convex hull.

2000s: CGAL (Computational Geometry Algorithms Library) provided robust implementations.

2010s: GPU-accelerated geometric algorithms for real-time graphics and simulation.

2020s: Learned geometry processing and neural implicit surfaces.
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for Computational Geometry

## Orientation Test — "The Turn"

When driving from point A to B to C, the orientation test tells you whether you turn left (counterclockwise), turn right (clockwise), or go straight (collinear). This is fundamental to all geometric algorithms.

## Convex Hull — "Rubber Band"

Imagine wrapping a rubber band around a set of nails on a board. The rubber band contracts to touch only the outermost nails, forming the convex hull. Any nail inside the band is not part of the hull.

## Closest Pair — "Split and Conquer"

Divide the points vertically by x-coordinate, find the closest pair in each half, then check only a narrow strip around the division line. This is analogous to sorting: split, solve, merge.

## Line Intersection — "Orientation Check"

Two line segments intersect if and only if each segment straddles the line containing the other segment. The orientation test at the endpoints determines straddling.
"@

wf "HOW_IT_WORKS.md" @"
# How Computational Geometry Works

## Graham Scan

1. Find the point with lowest y (lowest x for ties) as pivot
2. Sort all other points by polar angle from pivot
3. Push pivot and first two sorted points onto stack
4. For each remaining point:
   a. While stack top doesn't make a left turn with current point, pop
   b. Push current point onto stack
5. Stack contains convex hull vertices

## Monotone Chain

1. Sort points by x (then y)
2. Build lower hull: scan left to right, maintain left turns
3. Build upper hull: scan right to left, maintain left turns
4. Remove duplicate endpoints (first and last points)
5. Concatenate lower and upper hulls

## Closest Pair

1. Sort points by x-coordinate
2. Recursively divide into left and right halves
3. Find minimum distance delta = min(left_min, right_min)
4. Create strip of points within delta of midline
5. Sort strip by y-coordinate
6. For each point in strip, check next 7 points in y-order
7. Return minimum distance found
"@

wf "INTERNALS.md" @"
# Computational Geometry — Internal Mechanics

## Point and Vector Operations

```java
public record Point(double x, double y) {
    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }
    public double cross(Point p) {
        return x * p.y - y * p.x;
    }
    public double dot(Point p) {
        return x * p.x + y * p.y;
    }
    public double distance(Point p) {
        double dx = x - p.x, dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
```

## Orientation Test

```java
int orientation(Point p, Point q, Point r) {
    double val = (q.x - p.x) * (r.y - p.y) - (q.y - p.y) * (r.x - p.x);
    if (Math.abs(val) < EPS) return 0;  // collinear
    return (val > 0) ? 1 : 2;           // CCW or CW
}
```

## Monotone Chain Implementation

```java
List<Point> convexHull(Point[] points) {
    int n = points.length;
    Arrays.sort(points, (a, b) -> 
        a.x != b.x ? Double.compare(a.x, b.x) : Double.compare(a.y, b.y));
    List<Point> hull = new ArrayList<>();
    for (int phase = 0; phase < 2; phase++) {
        int start = hull.size();
        for (Point p : points) {
            while (hull.size() >= start + 2 &&
                   orientation(hull.get(hull.size()-2), 
                              hull.get(hull.size()-1), p) != 2)
                hull.remove(hull.size() - 1);
            hull.add(p);
        }
        hull.remove(hull.size() - 1);
        if (phase == 0) Collections.reverse(Arrays.asList(points));
    }
    return hull;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Computational Geometry

## Cross Product Properties

The cross product a x b = |a||b|sin(theta) gives:
- Positive if b is counterclockwise from a
- Negative if b is clockwise from a
- Zero if a and b are collinear
- Magnitude gives twice the area of triangle (0, a, b)

## Convex Hull Size

For n points uniformly distributed in a convex region, the expected number of hull vertices is O(log n). For points on a circle, the hull includes all n points.

## Closest Pair Strip

The strip width delta ensures that any pair closer than delta must lie within delta of the midline. In a delta x 2delta rectangle, at most 8 points can exist with mutual distances >= delta (pigeonhole principle), so only 7 comparisons per point in the strip are needed.

## Line Intersection Determinant

The intersection point of lines AB and CD can be computed using Cramer's rule with determinants. The denominator is zero if lines are parallel.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Computational Geometry

## Convex Hull Construction (Monotone Chain)

Points: [(0,0), (1,1), (2,0), (1,-1), (3,3), (0,3)]

Sorted by x: (0,0), (0,3), (1,-1), (1,1), (2,0), (3,3)

Lower hull (left to right):
- Push (0,0), (0,3)
- (1,-1): orientation(0,0), (0,3), (1,-1) = right -> pop (0,3)
- Push (1,-1). Continue: (1,1) -> right turn -> pop (1,-1), push (1,1)
- (2,0): pop (1,1), push (2,0). (3,3): left turn -> push (3,3)

Lower hull: (0,0), (2,0), (3,3)

Upper hull (right to left):
- Start at rightmost, similar process

Final hull: (0,0), (2,0), (3,3), (0,3)
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Computational Geometry

## Robustness Issues with Floating Point

Floating-point arithmetic introduces precision errors. The EPS (epsilon) constant is used for comparisons. Choosing the right epsilon is crucial: too small and legitimate near-collinearities are missed; too large and non-collinear points are incorrectly classified. A typical epsilon is 1e-9 for double precision.

## Integer Coordinate Alternative

For exact computation, use integer coordinates and compare cross products without division. This avoids floating-point issues entirely but limits the coordinate range. Java's BigInteger can handle arbitrary precision.

## Closest Pair Implementation Detail

The strip check only needs 7 comparisons per point because the points in the strip are sorted by y, and any point can have at most 7 other points within a delta x delta square. This is a key optimization that maintains the O(n log n) bound.

## Monotone Chain vs Graham Scan

Monotone chain avoids computing polar angles (which require trigonometric functions or atan2), making it faster and more numerically stable. It also handles collinear points more consistently.
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Computational Geometry Implementation

## Graham Scan Steps

1. Find lowest point (and leftmost if tie) as the pivot
2. Sort remaining points by polar angle relative to pivot
3. Initialize a stack with pivot and first two sorted points
4. For i = 3 to n-1:
   a. Take top two stack elements and current point
   b. While orientation of these three is NOT counterclockwise: pop stack
   c. Push current point onto stack
5. Return stack contents as convex hull

## Monotone Chain Steps

1. Sort all points by x (then y) ascending
2. Build lower hull:
   a. Start from leftmost point
   b. For each point, pop while last two hull points + current make a right turn
   c. Push current point
3. Build upper hull:
   a. Start from rightmost point
   b. Same process scanning right to left
4. Remove duplicates at endpoints
5. Concatenate lower (minus last) and upper (minus last)
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Computational Geometry

- **EPS too small or too large** — Can cause false collinear or non-collinear results
- **Integer overflow** — Cross product of large coordinates can overflow 32-bit int
- **Monotone chain duplicate endpoints** — Both lower and upper hull include first/last; must remove one
- **Graham Scan collinear points** — Sorting by polar angle with collinear points needs tie-breaking by distance
- **Closest pair strip sorting** — Re-sorting strip by y each recursive step adds log factor if done wrong
- **Line intersection collinear case** — Special case for overlapping collinear segments often forgotten
- **Cross product order** — (q-p) x (r-p) vs (r-p) x (q-p) changes orientation direction
- **Handling duplicate points** — Should be removed or handled early in all algorithms
- **Returning hull in wrong order** — Typically counterclockwise, with first point repeated for closed polygon
- **Stack overflow on large inputs** — Recursive closest pair may stack overflow on >10K points
"@

wf "DEBUGGING.md" @"
# Debugging — Computational Geometry

## Visual Verification

The most effective debugging for geometry algorithms is visual. Use simple plotting or ASCII art to verify convex hull and closest pair results.

## Cross Product Checks

```java
// Verify convex hull
boolean isConvex(List<Point> hull) {
    int n = hull.size();
    for (int i = 0; i < n; i++)
        if (orientation(hull.get(i), hull.get((i+1)%n), 
                       hull.get((i+2)%n)) != 2)
            return false;
    return true;
}
```

## Print Point Sets

```java
void printPoints(List<Point> pts) {
    pts.forEach(p -> System.out.println("(" + p.x + "," + p.y + ")"));
}
```
"@

wf "REFACTORING.md" @"
# Refactoring — Computational Geometry

## Generic Point Type

Support both integer and floating point coordinates using Java generics or a Number hierarchy.

## Comparable Points

Implement Comparable for points to simplify sorting in monotone chain and closest pair.

## Vector Operations as Separate Class

Extract vector mathematics into a Vector2D class used by all geometry algorithms.

## Lazy Sorting

Sort points lazily only when needed, caching results for reuse across multiple algorithms.

## Stream Pipeline

Use Java streams for filtering the strip in closest pair and for processing sorted points.
"@

wf "PERFORMANCE.md" @"
# Performance — Computational Geometry

## Algorithm Comparison

| Algorithm | Complexity | Notes |
|-----------|-----------|-------|
| Graham Scan | O(n log n) | Requires polar angle sorting |
| Monotone Chain | O(n log n) | More stable, avoids trig functions |
| Jarvis March | O(nh) | Fast when hull h is small |
| Quickhull | O(n log n) avg | Worst-case O(n^2) |
| Closest Pair | O(n log n) | Divide and conquer |
| Line Intersection (sweep) | O((n+k) log n) | k = number of intersections |

## Benchmark Data

For 1 million random points:
- Monotone chain: ~200ms
- Graham Scan: ~350ms (due to polar angle)
- Closest pair: ~500ms
- Naive O(n^2): > 1 hour

## Optimization Tips

- Use primitive double arrays instead of Point objects for tight loops
- Pre-allocate array lists with expected capacity
- Use iterative (non-recursive) divide and conquer for closest pair
"@

wf "SECURITY.md" @"
# Security — Computational Geometry

## Denial of Service

Pathological point distributions can cause worst-case O(n^2) behavior in algorithms with poor average-case guarantees. Use monotone chain instead of Jarvis March for predictable performance.

## Floating Point Injection

User-controlled coordinates with extreme values (NaN, Infinity) can crash algorithms or cause infinite loops. Validate and sanitize coordinate inputs.

## Algorithm Subversion

Crafted inputs can cause stack overflow in recursive algorithms (closest pair). Use iterative implementations or limit recursion depth.

## Precision Attacks

Adversarial inputs near the epsilon threshold can cause inconsistent orientation results. Use exact rational arithmetic for security-critical applications.
"@

wf "ARCHITECTURE.md" @"
# Architecture — Computational Geometry

## Library Design

```
Geometry Library
├── Primitives
│   ├── Point
│   ├── Vector
│   ├── Segment
│   └── Polygon
├── Algorithms
│   ├── ConvexHull (GrahamScan, MonotoneChain, JarvisMarch)
│   ├── ClosestPair
│   ├── LineIntersection
│   └── Triangulation
└── Utilities
    ├── Orientation
    ├── Distance
    └── Epsilon
```

## Testing Strategy

- Test on known geometric configurations (regular polygons, random sets, pathological cases)
- Compare hull implementations for consistency
- Test with degenerate cases (collinear, duplicate points, single point, empty set)
"@

wf "EXERCISES.md" @"
# Exercises — Computational Geometry

## Beginner
1. Implement a Point2D class with subtract, cross, dot, and distance methods
2. Write orientation test function
3. Determine if three points are collinear
4. Check if a point lies on a line segment

## Intermediate
5. Implement Graham Scan for convex hull
6. Implement Andrew's monotone chain
7. Implement closest pair using divide and conquer
8. Check if two line segments intersect

## Advanced
9. Implement Jarvis March (gift wrapping) and compare performance
10. Compute the area of a convex polygon
11. Implement a point-in-polygon test
12. Find the minimum bounding rectangle of a set of points
"@

wf "QUIZ.md" @"
# Quiz — Computational Geometry

1. What does the cross product of two vectors represent?
2. How does orientation test work using cross products?
3. What is the worst-case complexity of Jarvis March?
4. Why does Graham Scan sort points by polar angle?
5. How does the strip optimization work in closest pair?
6. What is the difference between Graham Scan and monotone chain?
7. How do you check if two line segments intersect?
8. What is the role of epsilon in geometric algorithms?
"@

wf "FLASHCARDS.md" @"
# Flashcards — Computational Geometry

- Q: Cross product sign meaning? -> A: Positive = CCW turn, Negative = CW
- Q: Convex hull definition? -> A: Smallest convex polygon containing all points
- Q: Graham Scan complexity? -> A: O(n log n)
- Q: Monotone chain complexity? -> A: O(n log n)
- Q: Closest pair complexity? -> A: O(n log n)
- Q: Strip check points per point? -> A: 7 (next 7 in y-order)
- Q: Line segment intersection check? -> A: Orientation of endpoints
- Q: Convex polygon? -> A: All interior angles < 180 degrees
"@

wf "INTERVIEW.md" @"
# Interview Questions — Computational Geometry

1. "Find the convex hull of a set of points." — Standard coding problem
2. "Find the closest pair of points." — Divide and conquer application
3. "Check if two rectangles intersect." — Extension of line intersection
4. "Count the number of lattice points on a line segment." — GCD-based approach
5. "Determine if a point is inside a polygon." — Ray casting or winding number
6. "Find the minimum area rectangle enclosing points." — Uses convex hull property
"@

wf "REFLECTION.md" @"
# Reflection — Computational Geometry

- Why is the orientation test the fundamental operation in computational geometry?
- How does the divide and conquer approach differ for geometric vs sorting problems?
- Why are numeric precision issues more problematic in geometry than other domains?
- How would you extend these algorithms to 3D?
- What are the tradeoffs between randomized and deterministic geometric algorithms?
"@

wf "REFERENCES.md" @"
# References — Computational Geometry

- de Berg, M. et al. "Computational Geometry: Algorithms and Applications." Springer, 2008.
- Shamos, M. "Computational Geometry." Ph.D. Thesis, Yale University, 1978.
- Graham, R.L. "An Efficient Algorithm for Determining the Convex Hull of a Finite Planar Set." Information Processing Letters, 1972.
- Andrew, A.M. "Another Efficient Algorithm for Convex Hulls in Two Dimensions." Information Processing Letters, 1979.
- Preparata, F.P., Shamos, M.I. "Computational Geometry: An Introduction." Springer, 1985.
- O'Rourke, J. "Computational Geometry in C." Cambridge University Press, 1998.
"@

Write-Host "18-computational-geometry: All 24 markdown files created"
