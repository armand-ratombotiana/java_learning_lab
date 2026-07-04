# Theory: Arrays

## Static Arrays

A static array is a contiguous block of memory storing elements of the same type. Each element occupies a fixed number of bytes, enabling O(1) random access via pointer arithmetic:

```
address = base_address + (index × element_size)
```

In Java, `int[] arr = new int[5];` allocates 20 bytes (5 × 4 bytes) on the heap. The JVM stores the length separately and bounds-checks every access.

## Dynamic Arrays

A dynamic array (e.g. `java.util.ArrayList<E>`) wraps a static array and resizes when capacity is exhausted. The resizing strategy determines amortized cost:

- **Geometric growth**: double capacity each resize → O(1) amortized insert
- **Arithmetic growth**: add fixed size each resize → O(n) amortized insert

### Amortized Analysis

Inserting n elements into a dynamic array that doubles at each resize:

```
Cost per insert: 1 (write) + occasional 2^k copy operations
Total cost for n inserts: n + (1 + 2 + 4 + ... + n) ≈ 3n
Amortized cost per insert: O(1)
```

## Multi-dimensional Arrays

Java supports two forms:
- **Rectangular**: `int[][] matrix = new int[3][4];` — contiguous array of arrays
- **Jagged**: `int[][] jagged = new int[3][]; jagged[0] = new int[2];` — each row independently sized

Row-major order is used: `matrix[row][col]` accesses position `(row × cols + col)`.

## Time Complexity

| Operation | Static Array | Dynamic Array |
|-----------|-------------|---------------|
| Access    | O(1)        | O(1)          |
| Search    | O(n)        | O(n)          |
| Insert at end | N/A     | O(1)*         |
| Insert at index | N/A   | O(n)          |
| Delete at end | N/A     | O(1)*         |
| Delete at index | N/A   | O(n)          |

\*Amortized

## Cache Locality

Arrays exploit spatial locality: adjacent elements are stored adjacently in memory. CPU cache lines (typically 64 bytes) prefetch sequential data, making array traversal significantly faster than linked data structures.
