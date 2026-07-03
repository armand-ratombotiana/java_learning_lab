# Debugging — Advanced Sorting

## Recursion Visualization
`java
private static int depth = 0;
System.out.println("  ".repeat(depth) + "Sorting [" + lo + "," + hi + ")");
depth++;
// sort code
depth--;
`

## Assertions
`java
assert isSorted(arr, lo, mid) : "Left half not sorted";
assert isSorted(arr, mid, hi) : "Right half not sorted";
`

## Partition Verification
`java
for (int i = lo; i < pivotIdx; i++)
    assert arr[i].compareTo(arr[pivotIdx]) <= 0;
`
"@

wf "REFACTORING.md" @"
# Refactoring — Advanced Sorting

## Strategy Pattern
`java
public interface SortStrategy {
    <T extends Comparable<T>> void sort(T[] arr);
}
`

## Parallelization with Fork/Join
`java
class MergeSortTask extends RecursiveAction {
    protected void compute() {
        if (hi - lo < THRESHOLD) {
            sequentialSort(arr, lo, hi);
            return;
        }
        invokeAll(new MergeSortTask(lo, mid), new MergeSortTask(mid, hi));
        merge(arr, aux, lo, mid, hi);
    }
}
`
"@

wf "PERFORMANCE.md" @"
# Performance — Advanced Sorting

## n=1,000,000 Benchmarks
| Algorithm | Time | Space | Stable |
|-----------|------|-------|--------|
| Merge Sort | ~120ms | O(n) | Yes |
| Quick Sort | ~90ms | O(log n) | No |
| Heap Sort | ~150ms | O(1) | No |

## Java's Choices
- Primitives: Dual-Pivot QuickSort (in-place, unstable, fast)
- Objects: TimSort (stable, O(n) for nearly-sorted data)
- Parallel: Arrays.parallelSort() uses ForkJoin
