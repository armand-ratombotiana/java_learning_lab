# LeetCode Solution: Maximum Subarray Sum (Vector API)

**Problem:** [53. Maximum Subarray (Kadane's Algorithm)](https://leetcode.com/problems/maximum-subarray/)

Uses the Vector API to compute Kadane's algorithm with SIMD parallelism.

## Approach

Process 8 ints at a time using `IntVector`. Maintain running max using lane-wise operations, then reduce across lanes at the end.

## Java 22+ Solution

```java
import jdk.incubator.vector.*;

public class MaxSubarrayVector {

    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public int maxSubArray(int[] nums) {
        int n = nums.length;
        int scalar = Integer.MIN_VALUE;

        // Use scalar Kadane for small arrays
        if (n < SPECIES.length()) {
            int maxEnding = 0;
            for (int v : nums) {
                maxEnding = Math.max(v, maxEnding + v);
                scalar = Math.max(scalar, maxEnding);
            }
            return scalar;
        }

        // Vectorized: track 8 running maxes
        int[] maxEnding = new int[SPECIES.length()];
        int[] maxSoFar  = new int[SPECIES.length()];

        int i = 0;
        for (; i <= n - SPECIES.length(); i += SPECIES.length()) {
            IntVector v = IntVector.fromArray(SPECIES, nums, i);

            // Update each lane's maxEnding
            IntVector prev = IntVector.fromArray(SPECIES, maxEnding, 0);
            IntVector candidate = prev.add(v);
            IntVector newMaxEnding = candidate.max(v);
            newMaxEnding.intoArray(maxEnding, 0);

            // Update each lane's maxSoFar
            IntVector prevMax = IntVector.fromArray(SPECIES, maxSoFar, 0);
            IntVector newMaxSoFar = prevMax.max(newMaxEnding);
            newMaxSoFar.intoArray(maxSoFar, 0);
        }

        // Reduce: get max across all lanes
        IntVector finalMax = IntVector.fromArray(SPECIES, maxSoFar, 0);
        int vectorResult = finalMax.reduceLanes(VectorOperators.MAX);

        // Handle remaining elements
        int maxEnd = 0;
        for (; i < n; i++) {
            maxEnd = Math.max(nums[i], maxEnd + nums[i]);
            vectorResult = Math.max(vectorResult, maxEnd);
        }

        return Math.max(scalar, vectorResult);
    }
}
```

> **Note:** This is a pedagogical example. Kadane's algorithm has a data dependency that limits vectorization speedup. Better candidates are embarrassingly parallel operations like dot products, matrix multiplication, or convolution.

## Key Takeaway

The Vector API brings **portable SIMD** to Java — the same code runs efficiently on AVX2, AVX-512, NEON, and SVE without modification.
