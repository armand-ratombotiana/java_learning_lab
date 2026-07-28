# LeetCode Solution: Memory-Efficient Matrix (FFM API)

**Problem:** [1572. Matrix Diagonal Sum](https://leetcode.com/problems/matrix-diagonal-sum/)

Demonstrates off-heap memory allocation for a matrix using FFM API.

## Approach

Allocate a flat `MemorySegment` for a 2D matrix using `Arena`. Use `MemoryLayout` for element access. This avoids GC pressure for large matrices.

## Java 22+ Solution

```java
import java.lang.foreign.*;
import java.lang.invoke.*;

public class MatrixDiagonalSum {

    private static final ValueLayout.OfInt INT = ValueLayout.JAVA_INT;

    public int diagonalSum(int[][] mat) {
        int n = mat.length;
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += mat[i][i];
            if (i != n - 1 - i) sum += mat[i][n - 1 - i];
        }
        return sum;
    }

    // Off-heap variant for large matrices
    public int diagonalSumOffHeap(MemorySegment matrix, int n) {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            int primary = matrix.getAtIndex(INT, (long) i * n + i);
            sum += primary;
            int j = n - 1 - i;
            if (i != j) {
                sum += matrix.getAtIndex(INT, (long) i * n + j);
            }
        }
        return sum;
    }

    public MemorySegment allocateMatrix(int[][] mat, Arena arena) {
        int n = mat.length;
        MemorySegment seg = arena.allocate(
                MemoryLayout.sequenceLayout((long) n * n, INT));
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                seg.setAtIndex(INT, (long) i * n + j, mat[i][j]);
            }
        }
        return seg;
    }
}
```

## Key Takeaway

FFM API enables **GC-free, off-heap** data structures for performance-critical scenarios while maintaining type safety through `MemoryLayout` descriptors.
