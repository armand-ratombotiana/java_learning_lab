# Interview Questions: Space-Filling Curves

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — system design focus) | — | Google, Meta, Amazon, Microsoft, Apple | Geospatial indexing / N-dim to 1D mapping |

## NeetCode Reference
Not in NeetCode. Space-filling curves appear in system design for location-based services.

## Company-Specific Questions

### Google
- Explain how Z-order (Morton) curves map 2D coordinates to a 1D index — bit interleaving algorithm
- How does Google's S2 geometry use Hilbert curves for spatial indexing on the Earth's surface?
- Design a system to find all points of interest within a given radius (using space-filling curves + B-tree)
- Compare Z-order vs Hilbert curves — which preserves spatial locality better and why?

### Microsoft
- How does Bing Maps use Hilbert curves for tile indexing and proximity queries?
- Implement the Morton code (Z-order) for 2D coordinates — write the bit interleaving function
- How would you use space-filling curves to optimize a columnar database for geospatial queries?

### Meta
- Design a "nearby friends" feature using Z-order curves + database indexing
- How would you efficiently query all Instagram posts in a geographic bounding box using Hilbert curves?
- Compare GeoHash (Z-order variant) vs S2 cells for location-based services

### Amazon
- How does DynamoDB's GeoHash-based index work for "find restaurants near me" queries?
- Design a spatial index for warehouse robot navigation using Z-order curves
- How would you implement a range query on a space-filling curve index?

### Apple
- How does Apple Maps use Hilbert curves for map tile indexing?
- Design a geofencing system that triggers when a device enters/exits a geographic region
- How would you implement "find my friends" using space-filling curves?

### Oracle
- How does Oracle Spatial use Z-order curves for spatial indexing (SDO_INDEX)?
- Compare space-filling curves vs R-trees for spatial database indexing
- What is the math behind bit interleaving for Morton codes? How do you deinterleave for range queries?

## Real Production Scenarios

- **Scenario 1: Geospatial Indexing (Google S2)** — Google's S2 library projects the Earth's sphere onto a cube, then maps each face to a Hilbert curve. This produces a 64-bit cell ID for any point on Earth. Nearby points have nearby cell IDs, enabling efficient "nearby places" queries using a B-tree or sorted range scan on the cell IDs.

- **Scenario 2: Uber H3 Hexagonal Grid** — Uber's H3 library uses a hierarchical hexagonal grid indexed by a Hilbert-like space-filling curve. Each hexagon has a unique index; parent/child relationships are simple bit operations. The system supports efficient queries for "find all active drivers within this hexagon and neighboring hexagons."

- **Scenario 3: Columnar Database Sorting** — A columnar database (like Redshift, Snowflake) sorts data by Z-order to co-locate spatially close values. When a query filters on two dimensions (e.g., lat and lng), Z-order sorting ensures that relevant rows are stored in nearby disk blocks, reducing scan time.

## Interview Tips

- Time: O(1) for encoding a single point to Morton/Hilbert code, O(n) for range query (scan over continuous ID range)
- Space: O(n) for storing codes alongside data, same as the original data
- Common edge cases: integer overflow when interleaving 32-bit coordinates into 64-bit code, non-square bounding box queries
- Z-order: interleave bits of x and y coordinates — `morton = interleave(x, y)`
- Hilbert: more complex — recursive quadrant reordering preserves adjacency better than Z-order
- Range query on Z-order: a rectangular bounding box maps to multiple continuous segments in the 1D space (not a single segment)
- Bounding box → multiple Z-order ranges: use the "big min" / "big max" algorithm to find the covering intervals

## Java-Specific Considerations

- No standard space-filling curve classes in Java — implement from scratch
- Morton code (Z-order) bit interleaving:
  ```java
  long mortonEncode(int x, int y) {
      long z = 0;
      for (int i = 0; i < 32; i++) {
          z |= (long)(x & (1 << i)) << i | (long)(y & (1 << i)) << (i + 1);
      }
      return z;
  }
  ```
- More efficient: use magic bit masks for parallel bit interleaving (spread table)
- Hilbert curve encoding: `void xy2d(int x, int y, int n, int[] d)` — recursive quadrant sorting
- `BitSet` for bit manipulation; `Long.numberOfLeadingZeros()` / `Long.bitCount()` for bit operations
- `java.awt.geom.Point2D` for coordinate representation; custom `class Cell { long id; double lat, lng; }`
- `java.util.Arrays.binarySearch()` on sorted long[] of Morton codes for range queries
- `TreeMap<Long, Point>` for spatial index backed by balanced BST on Morton codes
- For high-precision coordinates: encode as `int` via scaling (e.g., lat * 10^7) before interleaving
- Third-party library: `com.google.common.geometry.S2CellId` (Google S2 Java library)
- Third-party library: `com.uber.h3core.H3Core` (Uber H3 Java library)
- Coordinate compression: map (lat, lng) double pairs to (x, y) integer grid via quantization for efficient interleaving
