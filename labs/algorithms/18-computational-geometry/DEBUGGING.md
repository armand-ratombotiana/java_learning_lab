# Debugging — Computational Geometry

## Visual Verification

The most effective debugging for geometry algorithms is visual. Use simple plotting or ASCII art to verify convex hull and closest pair results.

## Cross Product Checks

`java
// Verify convex hull
boolean isConvex(List<Point> hull) {
    int n = hull.size();
    for (int i = 0; i < n; i++)
        if (orientation(hull.get(i), hull.get((i+1)%n), 
                       hull.get((i+2)%n)) != 2)
            return false;
    return true;
}
`

## Print Point Sets

`java
void printPoints(List<Point> pts) {
    pts.forEach(p -> System.out.println("(" + p.x + "," + p.y + ")"));
}
`
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
