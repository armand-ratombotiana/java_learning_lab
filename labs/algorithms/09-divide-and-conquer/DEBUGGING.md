# Debugging — Divide and Conquer

## Print Recursion Tree
`java
private static int depth = 0;
void debug(String msg) {
    System.out.println("  ".repeat(depth) + msg);
}
// At start: debug("Dividing [" + lo + "," + hi + ")"); depth++;
// At end: depth--; debug("Merged [" + lo + "," + hi + ")");
`

## Verify Sortedness After Divide
`java
assert isSorted(arr, lo, mid) && isSorted(arr, mid, hi);
`
"@

wf "REFACTORING.md" @"
# Refactoring — Divide and Conquer

## Bottom-Up (Iterative) D&C
`java
// Iterative merge sort
for (int size = 1; size < n; size *= 2)
    for (int lo = 0; lo < n - size; lo += 2 * size)
        merge(arr, lo, lo + size, Math.min(lo + 2 * size, n));
`

## Parallel D&C
`java
RecursiveAction task = new RecursiveAction() {
    protected void compute() {
        if (hi - lo < THRESHOLD) computeDirectly();
        else invokeAll(leftTask, rightTask);
    }
};
`
"@

wf "PERFORMANCE.md" @"
# Performance — Divide and Conquer

| Algorithm | Time | Space | Notes |
|-----------|------|-------|-------|
| Merge Sort | O(n log n) | O(n) | Stable |
| Quick Sort | O(n log n) avg | O(log n) | Unstable |
| Binary Search | O(log n) | O(1) | Requires sorted |
| Closest Pair | O(n log n) | O(n) | Optimal |
| Karatsuba | O(n¹·⁵⁸) | O(n) | Integer multiply |
| Strassen | O(n²·⁸¹) | O(n²) | Matrix multiply |

## D&C Overhead
- Function call overhead: O(log n) depth recursion
- Auxiliary space: often O(n) or O(log n)
- Parallelizable: natural for multi-core
