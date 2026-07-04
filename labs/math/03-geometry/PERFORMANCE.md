# Geometry Performance

## Spatial Indexing

### Grid Bucketing

Divide space into cells; only check objects in same or adjacent cells.

```java
public class SpatialGrid {
    private final double cellSize;
    private final Map<Cell, List<Point>> grid = new HashMap<>();

    public void insert(Point p) {
        Cell c = new Cell((int)(p.x() / cellSize), (int)(p.y() / cellSize));
        grid.computeIfAbsent(c, k -> new ArrayList<>()).add(p);
    }

    public List<Point> queryNearby(Point p) {
        Cell c = new Cell((int)(p.x() / cellSize), (int)(p.y() / cellSize));
        List<Point> nearby = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                nearby.addAll(grid.getOrDefault(
                    new Cell(c.x() + dx, c.y() + dy), List.of()));
        return nearby;
    }
}
```

### BVH (Bounding Volume Hierarchy)

Tree of bounding boxes — $O(\log n)$ intersection tests for ray tracing.

## Avoid `Math.atan2` in Hot Loops

`atan2` is expensive. Prefer dot/cross products and compare squared distances:

```java
double distSq = dx * dx + dy * dy; // avoid sqrt if only comparing
```

## Use `double` over `BigDecimal`

For geometric computations, `double` with epsilon tolerance is standard. `BigDecimal` is 100x slower and unnecessary for physical measurements.
