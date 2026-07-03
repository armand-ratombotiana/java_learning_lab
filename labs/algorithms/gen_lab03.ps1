$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\03-searching"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Searching Algorithms — Overview

Covers Linear Search, Binary Search, and Interpolation Search.

## Learning Objectives
- Implement all three search algorithms in Java
- Analyze time complexity for best, average, and worst cases
- Understand prerequisites (sorted data requirements)

## Prerequisites
- Basic Java arrays and loops
- Understanding of sorted vs unsorted data
- Basic Big O notation

## Estimated Time
- **Total**: 2–3 hours
"@

wf "THEORY.md" @"
# Searching — Theoretical Foundation

## Linear Search
Sequentially checks each element until target is found.
- Time: O(n) avg/worst, O(1) best
- Space: O(1)
- Prerequisite: None (works on unsorted data)

## Binary Search
Repeatedly divides sorted array in half.
- Time: O(log n)
- Space: O(1) iterative, O(log n) recursive
- Prerequisite: Sorted array

## Interpolation Search
Estimates position using probe formula (uniform distribution).
- Time: O(log log n) avg, O(n) worst
- Space: O(1)
- Prerequisite: Sorted, uniformly distributed data
"@

wf "WHY_IT_EXISTS.md" @"
# Why Searching Exists

Searching is fundamental to computing — virtually every program needs to find data. Linear search is simplest. Binary search was a breakthrough showing ordered data enables exponentially faster search. Interpolation search further optimizes for uniform distributions.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Searching Matters

- Database Indexing: B-trees and binary search power database queries
- Algorithm building block: Binary search used in bisection, root finding, debugging
- Sorted data advantage: Teaches why keeping data sorted is valuable
- Interview essential: Binary search appears in nearly every interview
"@

wf "HISTORY.md" @"
# History of Searching

- 1946: Binary search first described by John Mauchly
- 1949: First published binary search algorithm
- 1960s: Interpolation search developed for dictionary lookups
- 1962: Binary search bugs discovered — nearly all implementations had overflow errors
- 1988: Bentley's Programming Pearls highlighted binary search correctness
- 2006: Java's Arrays.binarySearch() in standard library
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Linear Search — "Looking for a Book"
Scan from left to right checking each book until you find the one you want.

## Binary Search — "Dictionary Lookup"
Open dictionary in middle. If word comes after, open right half; if before, open left half.

## Interpolation Search — "Phone Book"
Looking for Smith? Don't open in the middle — estimate based on alphabet (S is near end).
"@

wf "HOW_IT_WORKS.md" @"
# How Searching Works

## Binary Search
Array: [2,5,8,12,16,23,38,56,72,91], Target: 23
lo=0, hi=9, mid=4 → arr[4]=16 < 23 → lo=5
lo=5, hi=9, mid=7 → arr[7]=56 > 23 → hi=6
lo=5, hi=6, mid=5 → arr[5]=23 ✓ Found at index 5

## Interpolation Search Probe
pos = lo + ((target - arr[lo]) × (hi - lo)) / (arr[hi] - arr[lo])
For target=23: pos = 0 + (21 × 9) / 89 ≈ 2 → arr[2]=8 < 23
Next: pos = 5 → arr[5]=23 ✓
"@

wf "INTERNALS.md" @"
# Searching — Internal Mechanics

## Binary Search (Iterative)
```java
public static int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -(lo + 1);  // insertion point
}
```

## Interpolation Search
```java
public static int interpolationSearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi && target >= arr[lo] && target <= arr[hi]) {
        if (lo == hi) return arr[lo] == target ? lo : -1;
        int pos = lo + ((target - arr[lo]) * (hi - lo)) / (arr[hi] - arr[lo]);
        if (arr[pos] == target) return pos;
        if (arr[pos] < target) lo = pos + 1;
        else hi = pos - 1;
    }
    return -1;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Searching

## Binary Search Recurrence
T(n) = T(n/2) + O(1) → T(n) = O(log n)

## Interpolation Search
For uniformly distributed keys, expected probes is O(log log n).

## Safe Midpoint
mid = lo + (hi - lo) / 2 prevents overflow of (lo + hi) / 2 for arrays > 2³⁰.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Searching

## Binary Search Decision Tree
```
Searching for 23 in [2,5,8,12,16,23,38,56,72,91]

          mid=4 (16)
         /          \
      <16           >16
   lo=0,hi=3     lo=5,hi=9
   mid=1 (5)     mid=7 (56)
    /     \         /     \
  ...    ...      <56    >56
                lo=5    lo=8
                hi=6    hi=9
                mid=5=23 ✓
```

## Comparison
Unsorted → Linear O(n)
Sorted → Binary O(log n)
Uniform Sorted → Interpolation O(log log n)
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Searching

## Generic Binary Search
```java
public static <T extends Comparable<T>> int binarySearch(T[] arr, T target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int cmp = arr[mid].compareTo(target);
        if (cmp == 0) return mid;
        if (cmp < 0) lo = mid + 1;
        else hi = mid - 1;
    }
    return -(lo + 1);
}
```

## Search in Rotated Sorted Array
```java
public static int searchRotated(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        if (arr[lo] <= arr[mid]) {
            if (target >= arr[lo] && target < arr[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {
            if (target > arr[mid] && target <= arr[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Searching

## Binary Search
1. Set lo=0, hi=arr.length-1
2. While lo <= hi:
3. mid = lo + (hi - lo) / 2
4. If arr[mid] == target, return mid
5. If arr[mid] < target, lo = mid + 1
6. Else hi = mid - 1
7. Return -(lo + 1) for insertion point

## Interpolation Search
1. Set lo=0, hi=arr.length-1
2. While lo <= hi and target in range:
3. pos = lo + ((target-arr[lo]) × (hi-lo)) / (arr[hi]-arr[lo])
4. If arr[pos] == target, return pos
5. If arr[pos] < target, lo = pos + 1
6. Else hi = pos - 1
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Searching

- Binary Search: Integer overflow in (lo+hi)/2
- Binary Search: Infinite loop — off-by-one in lo/hi updates
- Binary Search: Unsorted array — verify input is sorted
- Interpolation Search: Division by zero when arr[hi] == arr[lo]
- Interpolation Search: Non-uniform distribution causes O(n) degradation
- Linear Search: Forgetting to return when found
"@

wf "DEBUGGING.md" @"
# Debugging — Searching

## Trace Output
```java
System.out.printf("lo=%d, hi=%d, mid=%d, arr[mid]=%d%n", lo, hi, mid, arr[mid]);
```

## Boundary Tests
```java
assertEquals(-1, binarySearch(new int[]{}, 5));     // empty
assertEquals(0, binarySearch(new int[]{5}, 5));      // single match
assertEquals(-1, binarySearch(new int[]{5}, 3));     // single no match
assertEquals(0, binarySearch(new int[]{3,5,7}, 3));  // first
assertEquals(2, binarySearch(new int[]{3,5,7}, 7));  // last
```
"@

wf "REFACTORING.md" @"
# Refactoring — Searching

## Extract Comparator
```java
public static <T> int binarySearch(T[] arr, T target, Comparator<T> cmp) {
    // use cmp.compare(arr[mid], target)
}
```

## Search Result Record
```java
public record SearchResult(int index, int comparisons) {}
```
"@

wf "PERFORMANCE.md" @"
# Performance — Searching

| Algorithm | Best | Average | Worst | Space |
|-----------|------|---------|-------|-------|
| Linear | O(1) | O(n) | O(n) | O(1) |
| Binary | O(1) | O(log n) | O(log n) | O(1) |
| Interpolation | O(1) | O(log log n) | O(n) | O(1) |

## Benchmarks (n=1,000,000)
- Linear: ~0.5ms avg, ~5ms worst
- Binary: ~30ns
- Interpolation: ~15ns avg
"@

wf "SECURITY.md" @"
# Security — Searching

- Binary search on untrusted data: If array is not sorted, results are unpredictable
- Interpolation search DoS: Non-uniform distribution causes O(n)
- Linear search timing: Can leak position through timing side channels
- Index bounds: Always validate computed indices
"@

wf "ARCHITECTURE.md" @"
# Architecture — Searching

## Java Standard Library
```java
java.util.Arrays.binarySearch(int[], int)
java.util.Arrays.binarySearch(Object[], Object)
java.util.Collections.binarySearch(List, T)
```

## Beyond Arrays
- TreeMap/TreeSet: Red-black tree O(log n)
- HashMap: O(1) average
- ConcurrentSkipListMap: O(log n) concurrent
"@

wf "EXERCISES.md" @"
# Exercises — Searching

## Beginner
1. Linear search returning all matching indices
2. Binary search on int[] (iterative)
3. Binary search (recursive)
4. Count comparisons for binary search

## Intermediate
5. First and last occurrence of target (duplicates)
6. Search in rotated sorted array
7. sqrt using binary search
8. Peak element in mountain array

## Advanced
9. Ternary search vs binary search
10. Exponential search (growing range)
11. Search in infinite sorted array
12. Auto-complete using trie
"@

wf "QUIZ.md" @"
# Quiz — Searching

1. What prerequisite does binary search require?
2. When does interpolation search degrade to O(n)?
3. How does interpolation search choose probe position?
4. Why use lo+(hi-lo)/2 instead of (lo+hi)/2?
5. When would you choose linear over binary search?
6. Space complexity of recursive binary search?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Binary search time? → A: O(log n)
- Q: Binary search prerequisite? → A: Sorted array
- Q: Interpolation search avg? → A: O(log log n)
- Q: Linear search best? → A: O(1)
- Q: Safe mid formula? → A: lo + (hi - lo) / 2
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Implement binary search." — Most common
2. "Find first bad version." — Binary search on boolean
3. "Search rotated sorted array." — Modified binary search
4. "Find integer square root." — Binary search on answer space
5. "Search in infinite array." — Exponential search
"@

wf "REFLECTION.md" @"
# Reflection

- How does sortedness fundamentally change the search problem?
- Why is binary search one of the most important algorithms?
- When would interpolation search be dangerous in production?
- What real-world systems depend on O(log n) search?
"@

wf "REFERENCES.md" @"
# References

- Bentley, J. "Programming Pearls" — Binary search correctness
- Knuth, D. "The Art of Computer Programming, Vol. 3"
- CLRS, Chapter 12
- Java Arrays.binarySearch() Documentation
"@

Write-Host "03-searching: All 24 files created"
