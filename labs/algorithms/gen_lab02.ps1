$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\02-advanced-sorting"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Advanced Sorting — Overview

Covers O(n log n) comparison-based sorting: Merge Sort, Quick Sort, and Heap Sort.

## Learning Objectives
- Implement Merge Sort, Quick Sort, and Heap Sort in Java
- Analyze time and space complexity
- Compare stability, in-place status, and constant factors

## Prerequisites
- Basic sorting (Bubble, Selection, Insertion)
- Recursion and divide-and-conquer
- Binary heap data structure

## Estimated Time
- **Total**: 5–6 hours
"@

wf "THEORY.md" @"
# Advanced Sorting — Theoretical Foundation

## Merge Sort
Divide and conquer: split array in half, sort each half recursively, merge.
- All Cases: O(n log n)
- Space: O(n) auxiliary array

## Quick Sort
Pick pivot, partition around pivot, recurse on partitions.
- Best/Average: O(n log n)
- Worst: O(n²) with bad pivot choice
- Space: O(log n) recursion stack

## Heap Sort
Build max-heap, repeatedly extract maximum.
- All Cases: O(n log n)
- Space: O(1) in-place
"@

wf "WHY_IT_EXISTS.md" @"
# Why Advanced Sorting Exists

The O(n log n) algorithms emerged from the need to sort large datasets efficiently. Comparison-based sorting lower bound is Ω(n log n). Merge Sort provides stable sorting (used by Java's Arrays.sort(Object[])). Quick Sort offers excellent average-case performance (used for primitives). Heap Sort guarantees O(n log n) with O(1) extra space.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Advanced Sorting Matters

- Optimal: O(n log n) is the theoretical best for comparison-based sorting
- Java Libraries: Arrays.sort() uses Dual-Pivot QuickSort and TimSort
- Stability: Merge Sort is stable for multi-key sorting
- Memory: Heap Sort sorts in-place with no extra memory
- External Sorting: Merge Sort extends naturally to disk-based sorting
- Parallelization: Can be efficiently parallelized
"@

wf "HISTORY.md" @"
# History of Advanced Sorting

- 1945: Von Neumann described Merge Sort
- 1960: C. A. R. Hoare developed Quick Sort
- 1964: J. W. J. Williams invented Heap Sort
- 1964: Floyd improved heap construction to O(n)
- 1991: Introsort (Musser) combined Quick, Heap, Insertion Sort
- 2002: Tim Peters created TimSort for Python
- 2009: Java adopted Dual-Pivot QuickSort
- 2011: TimSort replaced merge sort in Java 7 for objects
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Merge Sort — Tournament Brackets
Like a sports tournament: each game merges two sorted lists. Log₂(n) rounds.

## Quick Sort — Divide the Crowd
Pick a threshold (pivot). Shorter moves left, taller moves right. Repeat in each group.

## Heap Sort — Priority Queue Extraction
Max-heap as priority queue; repeatedly extract maximum for sorted order.
"@

wf "HOW_IT_WORKS.md" @"
# How Advanced Sorting Works

## Merge Sort Trace
[38,27,43,3,9,82,10]
Divide: [38,27,43,3] | [9,82,10]
        [38,27] | [43,3]    [9,82] | [10]
Merge:  [27,38] | [3,43]    [9,82] | [10]
        [3,27,38,43]      [9,10,82]
        [3,9,10,27,38,43,82]

## Quick Sort Trace (pivot = last)
[10,7,8,9,1,5] pivot=5 → [1,5,8,9,10,7]
Recurse: [1] | [8,9,10,7] → [7,8,9,10] → ...

## Heap Sort Trace
Build: [5,3,8,4,2] → [8,4,5,3,2]
Extract 8 → [5,4,2,3|8]
Extract 5 → [4,3,2|5,8]
Extract 4 → [3,2|4,5,8]
Extract 3 → [2|3,4,5,8]
"@

wf "INTERNALS.md" @"
# Advanced Sorting — Internal Mechanics

## Merge Sort
```java
public static <T extends Comparable<T>> void mergeSort(T[] arr, T[] aux, int lo, int hi) {
    if (hi - lo <= 1) return;
    int mid = lo + (hi - lo) / 2;
    mergeSort(arr, aux, lo, mid);
    mergeSort(arr, aux, mid, hi);
    merge(arr, aux, lo, mid, hi);
}

private static <T extends Comparable<T>> void merge(T[] arr, T[] aux, int lo, int mid, int hi) {
    System.arraycopy(arr, lo, aux, lo, hi - lo);
    int i = lo, j = mid;
    for (int k = lo; k < hi; k++) {
        if (i >= mid) arr[k] = aux[j++];
        else if (j >= hi) arr[k] = aux[i++];
        else if (aux[j].compareTo(aux[i]) < 0) arr[k] = aux[j++];
        else arr[k] = aux[i++];
    }
}
```

## Quick Sort with Median-of-Three
```java
public static <T extends Comparable<T>> void quickSort(T[] arr, int lo, int hi) {
    if (hi - lo <= 1) return;
    int pivotIdx = partition(arr, lo, hi - 1);
    quickSort(arr, lo, pivotIdx);
    quickSort(arr, pivotIdx + 1, hi);
}

private static <T extends Comparable<T>> int partition(T[] arr, int lo, int hi) {
    int mid = lo + (hi - lo) / 2;
    // median-of-three: sort lo, mid, hi
    if (arr[hi].compareTo(arr[lo]) < 0) swap(arr, lo, hi);
    if (arr[mid].compareTo(arr[lo]) < 0) swap(arr, lo, mid);
    if (arr[hi].compareTo(arr[mid]) < 0) swap(arr, mid, hi);
    swap(arr, mid, hi);  // place pivot at hi
    T pivot = arr[hi];
    int i = lo;
    for (int j = lo; j < hi; j++)
        if (arr[j].compareTo(pivot) <= 0) swap(arr, i++, j);
    swap(arr, i, hi);
    return i;
}
```

## Heap Sort
```java
public static <T extends Comparable<T>> void heapSort(T[] arr) {
    int n = arr.length;
    for (int i = n / 2 - 1; i >= 0; i--) siftDown(arr, n, i);
    for (int i = n - 1; i > 0; i--) {
        swap(arr, 0, i);
        siftDown(arr, i, 0);
    }
}

private static <T extends Comparable<T>> void siftDown(T[] arr, int n, int i) {
    while (true) {
        int largest = i, left = 2 * i + 1, right = 2 * i + 2;
        if (left < n && arr[left].compareTo(arr[largest]) > 0) largest = left;
        if (right < n && arr[right].compareTo(arr[largest]) > 0) largest = right;
        if (largest == i) break;
        swap(arr, i, largest);
        i = largest;
    }
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation

## Comparison Lower Bound
Any comparison-based sort requires Ω(n log n) comparisons in worst case (decision tree with n! leaves).

## Recurrence Relations
Merge Sort: T(n) = 2T(n/2) + O(n) → O(n log n)
Quick Sort (avg): T(n) = T(k) + T(n-k-1) + O(n) → O(n log n)
Heap Sort: Build O(n), each extraction O(log n) → O(n log n)

## Master Theorem
Merge Sort: a=2, b=2, f(n)=O(n) → case 2 → Θ(n log n)
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide

## Merge Sort Tree
```
          [38,27,43,3,9,82,10]
          /                 \
  [38,27,43,3]          [9,82,10]
   /        \            /      \
[38,27]    [43,3]     [9,82]   [10]
  /  \      /  \       /   \     |
[38][27] [43][3]    [9] [82]  [10]
  \  /      \  /       \   /     |
[27,38]    [3,43]     [9,82]   [10]
    \        /            \      /
  [3,27,38,43]          [9,10,82]
          \                 /
       [3,9,10,27,38,43,82]
```

## Heap Structure
```
       8
     /   \
    4     5
   / \   /
  3   2 1
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive

## Merge Sort Optimized
```java
public class MergeSort {
    private static final int INSERTION_THRESHOLD = 7;

    public static <T extends Comparable<T>> void sort(T[] arr) {
        T[] aux = arr.clone();
        sort(arr, aux, 0, arr.length);
    }

    private static <T extends Comparable<T>> void sort(T[] arr, T[] aux, int lo, int hi) {
        if (hi - lo <= INSERTION_THRESHOLD) {
            insertionSort(arr, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(aux, arr, lo, mid);
        sort(aux, arr, mid, hi);
        if (aux[mid - 1].compareTo(aux[mid]) <= 0) {
            System.arraycopy(aux, lo, arr, lo, hi - lo);
            return;
        }
        merge(arr, aux, lo, mid, hi);
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step

## Merge Sort
1. Create auxiliary array of same size
2. Recursively divide until subarrays have size 1
3. Merge using three-pointer technique
4. Optimize: use Insertion Sort for small subarrays
5. Optimize: skip merge if already sorted

## Quick Sort
1. Choose pivot (median-of-three)
2. Partition using Lomuto or Hoare scheme
3. Recurse on left and right partitions
4. Switch to Insertion Sort for small subarrays
5. Recurse on smaller partition first (tail recursion)

## Heap Sort
1. Build max-heap from last non-leaf node
2. Use sift-down (not sift-up) for O(n) construction
3. Swap root with last element and sift-down
4. Reduce heap size by 1 each iteration
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Merge Sort: Creating new arrays at each merge — use single auxiliary array
- Quick Sort: Poor pivot choice on sorted data — always use median-of-three
- Quick Sort: Stack overflow — switch to Insertion Sort for small partitions
- Heap Sort: Off-by-one in heap indexing — children are 2i+1, 2i+2
- Heap Sort: Using siftUp instead of siftDown for heap construction
- Stability assumptions — Quick Sort and Heap Sort are NOT stable
- Integer overflow: (lo+hi)/2 can overflow; use lo+(hi-lo)/2
"@

wf "DEBUGGING.md" @"
# Debugging — Advanced Sorting

## Recursion Visualization
```java
private static int depth = 0;
System.out.println("  ".repeat(depth) + "Sorting [" + lo + "," + hi + ")");
depth++;
// sort code
depth--;
```

## Assertions
```java
assert isSorted(arr, lo, mid) : "Left half not sorted";
assert isSorted(arr, mid, hi) : "Right half not sorted";
```

## Partition Verification
```java
for (int i = lo; i < pivotIdx; i++)
    assert arr[i].compareTo(arr[pivotIdx]) <= 0;
```
"@

wf "REFACTORING.md" @"
# Refactoring — Advanced Sorting

## Strategy Pattern
```java
public interface SortStrategy {
    <T extends Comparable<T>> void sort(T[] arr);
}
```

## Parallelization with Fork/Join
```java
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
```
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
"@

wf "SECURITY.md" @"
# Security — Advanced Sorting

- Quick Sort DoS: Attacker creates input causing O(n²). Mitigation: random pivot or Introsort
- Timing Side Channels: Comparison timing may leak information
- Comparator Injection: Malicious comparator could cause infinite loops
- Memory Exhaustion: Merge Sort O(n) space for large arrays
"@

wf "ARCHITECTURE.md" @"
# Architecture

## Java Collections Sorting
List.sort(Comparator) → TimSort
- Merge Sort with Insertion Sort for small runs
- Detects natural runs in data
- O(n) on already sorted data

## When to Choose
| Constraint | Best Choice |
|------------|-------------|
| Stable | Merge Sort / TimSort |
| Memory constrained | Heap Sort |
| Average speed | Quick Sort |
| Worst-case guarantee | Heap Sort / Merge Sort |
| Nearly-sorted | Insertion Sort / TimSort |
"@

wf "EXERCISES.md" @"
# Exercises — Advanced Sorting

## Beginner
1. Implement Merge Sort on int[]
2. Implement Quick Sort with Lomuto partition
3. Implement Heap Sort on Integer[]
4. Trace Merge Sort on [7,2,9,1,6,3,8,5]

## Intermediate
5. Quick Sort with Hoare partition
6. Median-of-three pivot selection
7. Iterative (non-recursive) Merge Sort
8. Count comparisons in Heap vs Merge Sort

## Advanced
9. Implement Introsort
10. External Merge Sort for disk-based data
11. Parallel Merge Sort using ForkJoinPool
12. Prove Ω(n log n) lower bound
"@

wf "QUIZ.md" @"
# Quiz — Advanced Sorting

1. Which advanced sorting algorithm is stable?
2. Worst-case space complexity of Quick Sort?
3. How does Heap Sort achieve O(1) extra space?
4. Why does Java use different sorts for primitives vs objects?
5. What is Introsort?
6. Why is median-of-three pivot useful?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Merge Sort time? → A: O(n log n) all cases
- Q: Quick Sort worst case? → A: O(n²) with bad pivot
- Q: Heap Sort space? → A: O(1) in-place
- Q: Stable O(n log n) sort? → A: Merge Sort
- Q: Java sort for primitives? → A: Dual-Pivot QuickSort
- Q: Java sort for objects? → A: TimSort
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Implement Quick Sort." — Classic coding question
2. "Why is Quick Sort faster than Merge Sort in practice?" — Cache locality
3. "How would you sort 10GB of data with 1GB RAM?" — External Merge Sort
4. "What sorting does Java use and why?" — Dual-Pivot QuickSort / TimSort
5. "Design a sorting algorithm with O(n log n) worst case that is stable." — Merge Sort
"@

wf "REFLECTION.md" @"
# Reflection

- Why can't we sort faster than O(n log n) with comparisons?
- How does pivot choice affect Quick Sort performance?
- Why is stability important in real-world sorting?
- How do constant factors affect algorithm choice despite asymptotic equivalence?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapters 6 (Heap Sort), 7 (Quick Sort), 8 (Merge Sort)
- Sedgewick, R. "Algorithms", Part II
- Musser, D. "Introsort: A fast, robust sorting algorithm" (1997)
- Java Arrays.sort() OpenJDK source
"@

Write-Host "02-advanced-sorting: All 24 files created"
