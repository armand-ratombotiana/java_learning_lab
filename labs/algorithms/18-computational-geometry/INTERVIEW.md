# Interview Questions: Computational Geometry

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 587 Erect the Fence | Hard | Google, Amazon | Convex hull (Monotone Chain) |
| LC 223 Rectangle Area | Medium | Google, Apple | Intersection area |
| LC 939 Minimum Area Rectangle | Medium | Google, Amazon | HashMap of points |
| LC 963 Minimum Area Rectangle II | Medium | Google | Geometry + slope |
| LC 1453 Maximum Number of Darts | Hard | Google | Angular sweep |
| LC 1610 Maximum Visible Points | Hard | Google | Sliding angle window |

## NeetCode Reference
Not extensively covered in NeetCode 150. Relevant to Google-specific preparation.

## Company-Specific Questions
### Google
- Convex hull problems (Erect the Fence) are Google signatures
- Maximum Visible Points tests angle windowing with sliding window
- Expect grid-based geometry problems with optimization constraints
- Design an algorithm to find the closest pair of points (D&C)

### Microsoft
- Rectangle area and intersection for UI layout calculations
- How would you detect if a point is inside a polygon?
- Geometry for graphics and game development (Xbox)

### Meta
- Less common; when it appears, it's usually 2D grid problems
- How would you compute the bounding box of a set of points?
- Social check-in clustering using geometric proximity

### Amazon
- Warehouse robot path planning with obstacle avoidance
- Minimum area rectangle for shelf space optimization
- Geometry for drone delivery routing

### Apple
- Graphics processing geometry (Core Graphics, Metal)
- How would you rotate a rectangle on screen efficiently?
- Touch detection: is a point inside a rounded rectangle?
- Memory-constrained geometric computations on device

### Oracle
- Spatial database queries (Oracle Spatial)
- How does Oracle index geospatial data (R-tree)?
- Design a spatial join algorithm for GIS applications

## Real Production Scenarios
- Scenario 1: Map clustering - using angular sweep to cluster millions of GPS points into geohashes for real-time location-based services with sub-degree accuracy
- Scenario 2: Obstacle avoidance - computing convex hull of obstacles for drone delivery route planning to ensure safe navigation with minimum detour
- Scenario 3: UI hit testing - debugging a touch detection system where the hit area calculation in a graphics editor produces incorrect results for rotated elements

## Interview Tips
- Know Graham Scan and Monotone Chain (Andrew's algorithm) for convex hull
- Cross product is fundamental: orientation(a,b,c) = (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x)
- For angle-based problems, convert to polar coordinates and sort by angle
- Common edge cases: collinear points, duplicate points, integer overflow in cross product

## Java-Specific Considerations
- Use `int[][] points` or `class Point { int x, y; }` with custom `Comparator`
- Cross product: use `long` for intermediate multiplication to avoid overflow
- `Arrays.sort(points, (a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1])` for sorting by x then y
- `Math.atan2(dy, dx)` for angle computation; beware of floating-point precision
- Pitfall: integer overflow in area calculations (use `long` for coordinate multiplication)
- Pitfall: floating-point comparison with ==; use epsilon: `Math.abs(a - b) < 1e-9`
