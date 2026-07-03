$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\09-divide-and-conquer"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Divide and Conquer — Overview

Covers divide-and-conquer paradigm: Merge Sort, Quick Sort, and closest pair of points.

## Learning Objectives
- Master divide-and-conquer problem-solving approach
- Implement D&C algorithms in Java
- Analyze using recurrence relations and Master Theorem
- Solve geometric problems (closest pair) with D&C

## Prerequisites
- Recursion fundamentals
- Basic sorting and searching
- Recurrence relations

## Estimated Time
- **Total**: 4–6 hours
"@

wf "THEORY.md" @"
# Divide and Conquer — Theoretical Foundation

## The Paradigm
1. **Divide**: Break problem into smaller subproblems
2. **Conquer**: Solve subproblems recursively
3. **Combine**: Merge subproblem solutions into final solution

## Classic Examples
- Merge Sort: Divide array, sort halves, merge
- Quick Sort: Partition array, sort partitions
- Binary Search: Divide search space in half
- Closest Pair: Divide points, find closest in halves and strip
- Strassen's Matrix Multiplication: O(n²·⁸¹)
- Karatsuba Multiplication: O(n¹·⁵⁸)

## Complexity Analysis
Use Master Theorem: T(n) = aT(n/b) + f(n)
- Merge Sort: T(n) = 2T(n/2) + O(n) → O(n log n)
- Binary Search: T(n) = T(n/2) + O(1) → O(log n)
- Closest Pair: T(n) = 2T(n/2) + O(n) → O(n log n)
"@

wf "WHY_IT_EXISTS.md" @"
# Why Divide and Conquer Exists

D&C is a natural problem-solving strategy: break complex problems into manageable pieces. It was formalized in the 1960s with the development of Merge Sort and became a foundational algorithm design paradigm. Many optimal algorithms are D&C-based.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Divide and Conquer Matters

- Optimality: Many D&C algorithms are asymptotically optimal
- Parallelism: Subproblems are independent → easy to parallelize
- Cache Efficiency: Good data locality (e.g., Merge Sort sequential access)
- Foundation: Understanding D&C is prerequisite for advanced topics
- Real Applications: Closest pair in GIS, FFT in signal processing, Strassen in graphics
"@

wf "HISTORY.md" @"
# History of Divide and Conquer

- 1945: Von Neumann described Merge Sort (first D&C sort)
- 1960: Quick Sort (Hoare)
- 1962: Strassen's matrix multiplication (first sub-cubic)
- 1962: Karatsuba multiplication (sub-quadratic integer multiplication)
- 1965: Cooley-Tukey FFT (D&C for signal processing)
- 1975: Closest pair O(n log n) by Shamos and Hoey
- 1978: Quickhull (D&C for convex hull)
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## The Great Divide
Like splitting a large army into smaller battalions, each battalion fights its own battle (conquer), then reports back to combine intelligence (combine).

## Russian Dolls as Assembly
Opening dolls is divide (break apart), reassembling is combine (put back together).

## Tournament Tree
Like a tennis tournament: matches are played in parallel, winners advance, and the final match determines the champion.
"@

wf "HOW_IT_WORKS.md" @"
# How Divide and Conquer Works

## Merge Sort
```
[38,27,43,3,9,82,10]
         ↓ divide
[38,27,43,3] [9,82,10]
    ↓ divide      ↓ divide
[38,27] [43,3] [9,82] [10]
  ↓        ↓      ↓     ↓
[38][27] [43][3] [9][82][10]
  ↓  ↓     ↓  ↓    ↓  ↓   ↓
  merge    merge   merge
[27,38] [3,43] [9,82] [10]
    ↓        ↓      ↓
    merge    merge
[3,27,38,43] [9,10,82]
        ↓    ↓
        merge
[3,9,10,27,38,43,82]
```

## Closest Pair of Points
1. Sort points by x-coordinate
2. Divide at median x into left/right halves
3. Recursively find δ = min(leftMin, rightMin)
4. Create strip of points within δ of median
5. Sort strip by y, compare each with next 7 points
"@

wf "INTERNALS.md" @"
# Divide and Conquer — Internal Mechanics

## Merge Sort Implementation
```java
public static <T extends Comparable<T>> void mergeSort(T[] arr, T[] aux, int lo, int hi) {
    if (hi - lo <= 1) return;
    int mid = lo + (hi - lo) / 2;
    mergeSort(arr, aux, lo, mid);
    mergeSort(arr, aux, mid, hi);
    merge(arr, aux, lo, mid, hi);
}
```

## Quick Sort Partition
```java
private static <T extends Comparable<T>> int partition(T[] arr, int lo, int hi) {
    T pivot = arr[hi];
    int i = lo;
    for (int j = lo; j < hi; j++) {
        if (arr[j].compareTo(pivot) <= 0) {
            swap(arr, i, j);
            i++;
        }
    }
    swap(arr, i, hi);
    return i;
}
```

## Closest Pair — Strip Check
```java
// strip is sorted by y, δ is current minimum
double minDist = δ;
for (int i = 0; i < strip.size(); i++) {
    for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < minDist; j++) {
        double dist = distance(strip.get(i), strip.get(j));
        minDist = Math.min(minDist, dist);
    }
}
return minDist;
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Divide and Conquer

## Master Theorem
T(n) = aT(n/b) + f(n)

1. If f(n) = O(n^c) where c < log_b(a): T(n) = Θ(n^{log_b(a)})
2. If f(n) = Θ(n^c log^k n) where c = log_b(a): T(n) = Θ(n^c log^{k+1} n)  
3. If f(n) = Ω(n^c) where c > log_b(a) and af(n/b) ≤ kf(n): T(n) = Θ(f(n))

## Closest Pair Correctness
The strip of width 2δ guarantees that at most 7 points need to be compared with each point — this is because points within the strip cannot be closer than δ in their respective halves.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Divide and Conquer

## Closest Pair Division
```
Points sorted by x:
   a    b    c    d | e    f    g    h
                    ↑ median
Left: δL           Right: δR
       δ = min(δL, δR)

Strip: points within δ of median
   b    d | e    g
   Check pairs across boundary
```

## D&C Algorithm Tree
```
         Problem
        /    |    \
       /     |     \
  SubP1   SubP2   SubP3
   / \     / \     / \
  S1  S2  S3  S4  S5  S6
   \ /     \ /     \ /
   C1      C2      C3
        \  |  /
       Solution
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Divide and Conquer

## Closest Pair of Points
```java
public class ClosestPair {
    record Point(double x, double y) {}

    public double closestPair(Point[] points) {
        Point[] px = points.clone();
        Point[] py = points.clone();
        Arrays.sort(px, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(py, Comparator.comparingDouble(p -> p.y));
        return closestPairRec(px, py, 0, px.length);
    }

    private double closestPairRec(Point[] px, Point[] py, int lo, int hi) {
        if (hi - lo <= 3) return bruteForce(px, lo, hi);
        int mid = lo + (hi - lo) / 2;
        double midX = px[mid].x;

        Point[] pyLeft = Arrays.stream(py).filter(p -> p.x <= midX).toArray(Point[]::new);
        Point[] pyRight = Arrays.stream(py).filter(p -> p.x > midX).toArray(Point[]::new);

        double δL = closestPairRec(px, pyLeft, lo, mid);
        double δR = closestPairRec(px, pyRight, mid, hi);
        double δ = Math.min(δL, δR);

        List<Point> strip = new ArrayList<>();
        for (Point p : py)
            if (Math.abs(p.x - midX) < δ) strip.add(p);

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < δ; j++) {
                double d = distance(strip.get(i), strip.get(j));
                δ = Math.min(δ, d);
            }
        }
        return δ;
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Divide and Conquer

## D&C Problem Solving
1. Identify if problem can be broken into independent subproblems
2. Define base case: smallest instance solved directly
3. Determine divide step: how to split input
4. Determine combine step: how to merge subproblem results
5. Analyze using recurrence relation
6. Consider whether D&C is optimal vs DP or greedy

## When NOT to Use D&C
- Subproblems are not independent (use DP instead)
- Combine step is more expensive than brute force
- Problem size is too small for recursion overhead to be worth it
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Not handling base cases correctly (infinite recursion)
- Subproblems that overlap (should be DP, not D&C)
- Inefficient combine step dominating the complexity
- Recomputing subproblem results (if overlapping)
- Not accounting for recursion overhead in small inputs
- Closest Pair: Not sorting strip by y, causing O(n²) strip check
- Off-by-one in array indices during divide step
"@

wf "DEBUGGING.md" @"
# Debugging — Divide and Conquer

## Print Recursion Tree
```java
private static int depth = 0;
void debug(String msg) {
    System.out.println("  ".repeat(depth) + msg);
}
// At start: debug("Dividing [" + lo + "," + hi + ")"); depth++;
// At end: depth--; debug("Merged [" + lo + "," + hi + ")");
```

## Verify Sortedness After Divide
```java
assert isSorted(arr, lo, mid) && isSorted(arr, mid, hi);
```
"@

wf "REFACTORING.md" @"
# Refactoring — Divide and Conquer

## Bottom-Up (Iterative) D&C
```java
// Iterative merge sort
for (int size = 1; size < n; size *= 2)
    for (int lo = 0; lo < n - size; lo += 2 * size)
        merge(arr, lo, lo + size, Math.min(lo + 2 * size, n));
```

## Parallel D&C
```java
RecursiveAction task = new RecursiveAction() {
    protected void compute() {
        if (hi - lo < THRESHOLD) computeDirectly();
        else invokeAll(leftTask, rightTask);
    }
};
```
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
"@

wf "SECURITY.md" @"
# Security — Divide and Conquer

- Stack overflow: Deep recursion on large inputs
- DoS via recursion depth: Attacker crafts large input for D&C
- Memory: Merge Sort allocates O(n) auxiliary space
- Integer overflow: (lo+hi)/2 on huge arrays
"@

wf "ARCHITECTURE.md" @"
# Architecture — Divide and Conquer

## Java Arrays.sort()
- Primitives: Dual-Pivot QuickSort (D&C)
- Objects: TimSort (merge-based D&C)
- Parallel: Arrays.parallelSort() (ForkJoin D&C)

## Real-World D&C Systems
- MapReduce: Distributed D&C for big data
- FFT: Signal processing, image compression
- Quickhull: Computer graphics convex hull
- Strassen: Graphics, physics simulations
- Closest pair: GIS, astronomy, collision detection
"@

wf "EXERCISES.md" @"
# Exercises — Divide and Conquer

## Beginner
1. Implement Merge Sort on int[]
2. Implement Quick Sort on Integer[]
3. Implement binary search recursively
4. Count inversions in array using D&C

## Intermediate
5. Find majority element using D&C
6. Implement closest pair of points
7. Find maximum subarray sum (D&C version)
8. Implement Karatsuba multiplication

## Advanced
9. Implement Strassen's matrix multiplication
10. Implement Quickhull for convex hull
11. Implement FFT using Cooley-Tukey algorithm
12. Implement skyline problem using D&C
"@

wf "QUIZ.md" @"
# Quiz — Divide and Conquer

1. What are the three steps of D&C?
2. What is the Master Theorem used for?
3. Why is Merge Sort O(n log n)?
4. How does D&C differ from DP?
5. Why is Quick Sort's worst case O(n²)?
6. How many points are checked in closest pair strip?
7. When is D&C a bad choice?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Three D&C steps? → A: Divide, conquer, combine
- Q: Merge Sort recurrence? → A: T(n) = 2T(n/2) + O(n)
- Q: Binary Search recurrence? → A: T(n) = T(n/2) + O(1)
- Q: Master Theorem? → A: T(n) = aT(n/b) + f(n)
- Q: Closest Pair complexity? → A: O(n log n)
- Q: D&C vs DP? → A: D&C: independent subproblems; DP: overlapping
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Count inversions." — Merge Sort modification
2. "Maximum subarray." — D&C version (also Kadane's)
3. "Closest pair of points." — Hard geometric D&C
4. "Majority element." — D&C or Boyer-Moore
5. "The skyline problem." — D&C merge
6. "Median of two sorted arrays." — D&C binary search
7. "Implement pow(x, n)." — D&C exponentiation
"@

wf "REFLECTION.md" @"
# Reflection

- How does D&C relate to other paradigms like DP and greedy?
- When are subproblems truly independent?
- How does the combine step determine overall complexity?
- Why is D&C naturally parallelizable?
- What real-world problems are best solved with D&C?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapters 4 (D&C), 30 (FFT), 33 (Computational Geometry)
- Sedgewick, R. "Algorithms", Chapter 2 (Sorting)
- Kleinberg & Tardos "Algorithm Design", Chapter 5
- Shamos & Hoey "Closest-point problems" (1975)
"@

Write-Host "09-divide-and-conquer: All 24 files created"
