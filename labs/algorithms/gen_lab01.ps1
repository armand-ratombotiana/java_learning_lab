$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\01-sorting-basics"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Sorting Basics — Overview

Covers fundamental comparison-based sorting algorithms: Bubble Sort, Selection Sort, and Insertion Sort.

## Learning Objectives

- Implement Bubble Sort, Selection Sort, and Insertion Sort in Java
- Analyze time and space complexity of each algorithm
- Understand when to use each sorting algorithm
- Identify stable vs unstable sorting algorithms

## Prerequisites

- Basic Java syntax and array manipulation
- Understanding of nested loops
- Basic complexity analysis

## Estimated Time

- **Theory**: 60 minutes
- **Practice**: 90 minutes
- **Exercises**: 60 minutes
- **Total**: 3–4 hours
"@

wf "THEORY.md" @"
# Sorting Basics — Theoretical Foundation

## Bubble Sort

Repeatedly steps through the list, compares adjacent elements, and swaps them if in wrong order.

### Complexity
- Best: O(n) — with optimization flag on sorted array
- Average/Worst: O(n²)
- Space: O(1)

## Selection Sort

Divides input into sorted and unsorted regions, repeatedly selects smallest from unsorted.

### Complexity
- All Cases: O(n²)
- Space: O(1)

## Insertion Sort

Builds sorted array one element at a time by inserting each into correct position.

### Complexity
- Best: O(n) — already sorted
- Average/Worst: O(n²)
- Space: O(1)
"@

wf "WHY_IT_EXISTS.md" @"
# Why Sorting Basics Exist

Sorting is fundamental to computing. Early computers needed organized data for efficient retrieval. Bubble Sort, Selection Sort, and Insertion Sort represent the simplest intuitive approaches, mirroring how humans sort physical objects. They are pedagogically essential for understanding algorithm design patterns.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Sorting Basics Matter

- **Algorithm Analysis**: Demonstrate O(n²) vs O(n) behavior clearly
- **Stability Concepts**: Insertion Sort is stable; Selection Sort is not
- **Adaptive Algorithms**: Insertion Sort's O(n) best case is optimal for nearly-sorted data
- **Building Blocks**: Used in hybrid sorts (Timsort, IntroSort)
- **Interview Foundation**: Sorting questions are extremely common
"@

wf "HISTORY.md" @"
# History of Basic Sorting

- **1945**: Von Neumann described Merge Sort
- **1956**: Bubble Sort first described in print
- **1959**: Shell Sort published (Insertion Sort optimization)
- **1962**: Hoare introduced Quick Sort
- **2002**: Timsort combined Merge + Insertion Sort
- **2009**: Java adopted Dual-Pivot QuickSort
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for Sorting Basics

## Bubble Sort — "Bubbles Rising"
Largest elements bubble up to the top like bubbles in a drink.

## Selection Sort — "Card Picker"
Scan all cards, pick smallest, place it. Repeat for remaining.

## Insertion Sort — "Organizing Cards"
Pick up one card at a time, insert into correct position among already-sorted cards.
"@

wf "HOW_IT_WORKS.md" @"
# How Sorting Basics Work

## Bubble Sort
Array: [5, 3, 8, 4, 2]
Pass 1: 3↔5 → [3,5,8,4,2]; 8↔4 → [3,5,4,8,2]; 8↔2 → [3,5,4,2,8]
Pass 2: [3,4,2,5,8]
Pass 3: [3,2,4,5,8]
Pass 4: [2,3,4,5,8]

## Selection Sort
Array: [5, 3, 8, 4, 2]
Pass 1: min=2 → swap → [2,3,8,4,5]
Pass 2: min=3 → no swap → [2,3,8,4,5]
Pass 3: min=4 → swap → [2,3,4,8,5]
Pass 4: min=5 → swap → [2,3,4,5,8]

## Insertion Sort
Array: [5, 3, 8, 4, 2]
key=3: shift 5 → insert 3 → [3,5,8,4,2]
key=8: no shift → [3,5,8,4,2]
key=4: shift 8,5 → insert 4 → [3,4,5,8,2]
key=2: shift all → insert 2 → [2,3,4,5,8]
"@

wf "INTERNALS.md" @"
# Sorting Basics — Internal Mechanics

## Bubble Sort with Early Exit
```java
public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
    int n = arr.length;
    boolean swapped;
    for (int i = 0; i < n - 1; i++) {
        swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j].compareTo(arr[j+1]) > 0) {
                T temp = arr[j];
                arr[j] = arr[j+1];
                arr[j+1] = temp;
                swapped = true;
            }
        }
        if (!swapped) break;
    }
}
```

## Selection Sort
```java
public static <T extends Comparable<T>> void selectionSort(T[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[j].compareTo(arr[minIdx]) < 0) minIdx = j;
        }
        T temp = arr[minIdx];
        arr[minIdx] = arr[i];
        arr[i] = temp;
    }
}
```

## Insertion Sort
```java
public static <T extends Comparable<T>> void insertionSort(T[] arr) {
    for (int i = 1; i < arr.length; i++) {
        T key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j].compareTo(key) > 0) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Sorting Basics

## Summations

Bubble Sort comparisons: n(n-1)/2
Selection Sort comparisons: n(n-1)/2

## Inversions

An inversion is a pair (i, j) where i < j but arr[i] > arr[j].
- Bubble Sort swaps one inversion per swap
- Insertion Sort runs in O(n + inversions) time
- Average inversions in random permutation: n(n-1)/4

## Stability

A sorting algorithm is stable if elements with equal keys maintain relative order. Bubble Sort and Insertion Sort are stable; Selection Sort is not.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Sorting Basics

## Bubble Sort Visualization
```
Initial: [5, 3, 8, 4, 2]
Pass 1:  3↔5        [3, 5, 8, 4, 2]
            5<8 ✓    [3, 5, 8, 4, 2]
               8↔4   [3, 5, 4, 8, 2]
                  8↔2 [3, 5, 4, 2, 8]
Pass 2:  3<5 ✓     [3, 5, 4, 2, 8]
            5↔4     [3, 4, 5, 2, 8]
               5↔2  [3, 4, 2, 5, 8]
Pass 3:  3<4 ✓     [3, 4, 2, 5, 8]
            4↔2     [3, 2, 4, 5, 8]
Pass 4:  3↔2       [2, 3, 4, 5, 8]
```

## Insertion Sort Visualization
```
Initial: [5, 3, 8, 4, 2]
Step 1: key=3 → shift 5 → [5, 5, 8, 4, 2] → insert 3 → [3, 5, 8, 4, 2]
Step 2: key=8 → no shift → [3, 5, 8, 4, 2]
Step 3: key=4 → shift 8 → [3, 5, 8, 8, 2] → shift 5 → [3, 5, 5, 8, 2] → insert 4 → [3, 4, 5, 8, 2]
Step 4: key=2 → shift all → [2, 3, 4, 5, 8]
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Sorting Basics

## Generic Sorting Utilities

```java
public class SortUtils {
    private SortUtils() {}

    public static <T extends Comparable<T>> void bubbleSort(T[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static <T extends Comparable<T>> void selectionSort(T[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].compareTo(arr[minIdx]) < 0) minIdx = j;
            }
            if (minIdx != i) swap(arr, i, minIdx);
        }
    }

    public static <T extends Comparable<T>> void insertionSort(T[] arr) {
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].compareTo(key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Sorting Basics

## Bubble Sort
1. Get array length n
2. Outer loop: i = 0 to n-2
3. Initialize swapped = false
4. Inner loop: j = 0 to n-i-2
5. If arr[j] > arr[j+1], swap, set swapped = true
6. If !swapped, break (already sorted)
7. Return sorted array

## Selection Sort
1. Outer loop: i = 0 to n-2
2. Set minIdx = i
3. Inner loop: j = i+1 to n-1
4. If arr[j] < arr[minIdx], update minIdx
5. Swap arr[i] with arr[minIdx] if different
6. Return sorted array

## Insertion Sort
1. Loop i = 1 to n-1
2. Store key = arr[i], j = i - 1
3. While j >= 0 and arr[j] > key: shift arr[j+1] = arr[j], j--
4. Place arr[j+1] = key
5. Return sorted array
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Sorting Basics

- **Bubble Sort: Off-by-one** — Using j < n-i instead of j < n-i-1 causes overflow
- **Bubble Sort: No early exit** — Missing swapped flag always runs O(n²)
- **Selection Sort: Wrong minIdx update** — Forgetting to update when finding smaller element
- **Selection Sort: Self-swap** — Swapping when minIdx == i (wasted operation)
- **Insertion Sort: Condition order** — Check j >= 0 BEFORE accessing arr[j]
- **Insertion Sort: Losing key** — Using arr[i] after shifting instead of saved key
- **Generic arrays** — Cannot create T[] directly in Java
"@

wf "DEBUGGING.md" @"
# Debugging — Sorting Basics

## Print Debugging
```java
System.out.println(Arrays.toString(arr) + " after pass " + i);
```

## Assertions
```java
assert isSorted(arr) : "Array should be sorted";

public static <T extends Comparable<T>> boolean isSorted(T[] arr) {
    for (int i = 1; i < arr.length; i++)
        if (arr[i].compareTo(arr[i-1]) < 0) return false;
    return true;
}
```

## Visual Debugging
Use JVisualVM or Java Mission Control to profile sorting of 10,000+ elements.
"@

wf "REFACTORING.md" @"
# Refactoring — Sorting Basics

## Extract Comparator
```java
public static <T> void insertionSort(T[] arr, Comparator<T> cmp) {
    // use cmp.compare(arr[j], key) > 0
}
```

## Strategy Pattern
```java
interface SortStrategy<T> {
    void sort(T[] arr, Comparator<T> cmp);
}
```

## Generalize from int[] to generic
Convert from int[] to T extends Comparable<T> for reusability.
"@

wf "PERFORMANCE.md" @"
# Performance — Sorting Basics

## Empirical Performance

| Algorithm | n=10 | n=100 | n=1,000 | n=10,000 |
|-----------|------|-------|---------|----------|
| Bubble Sort | <1μs | 15μs | 1.5ms | 150ms |
| Selection Sort | <1μs | 10μs | 1.0ms | 100ms |
| Insertion Sort | <1μs | 8μs | 0.8ms | 80ms |

## When to Use

- **Bubble Sort**: Never in production
- **Selection Sort**: When minimizing swaps is critical (e.g., EEPROM)
- **Insertion Sort**: Small arrays (n < 50), nearly-sorted data
"@

wf "SECURITY.md" @"
# Security — Sorting Basics

- **Timing Attacks**: Constant-time comparison may be needed for sensitive data
- **Data Integrity**: Unstable sorts can reorder equal elements, affecting audit trails
- **Resource Exhaustion**: O(n²) on attacker-controlled large inputs can cause DoS
- **Comparator Injection**: Ensure user-controlled comparators are well-behaved
"@

wf "ARCHITECTURE.md" @"
# Architecture — Sorting Basics

## Java Standard Library Sorting
```
java.util.Arrays
  ├── sort(int[]) — Dual-Pivot QuickSort
  ├── sort(Object[]) — TimSort
  └── parallelSort(int[]) — ForkJoin-based

java.util.Collections
  └── sort(List<T>) — TimSort
```

## When to Build Custom
- Sorting custom objects with specific comparison rules
- External sorting (data too large for memory)
- Embedded systems with constrained resources
"@

wf "EXERCISES.md" @"
# Exercises — Sorting Basics

## Beginner
1. Implement Bubble Sort on int[]
2. Implement Selection Sort on String[]
3. Implement Insertion Sort on double[]
4. Count comparisons for [5,2,7,1,9,3]

## Intermediate
5. Track number of swaps in Bubble Sort
6. Hybrid sort: Insertion Sort for subarrays < 10
7. Sort Person objects by age using each algorithm
8. Descending Selection Sort

## Advanced
9. SortBenchmark class timing all three sorts
10. Cocktail Shaker Sort (bidirectional bubble)
11. Prove Insertion Sort is O(n + inversions)
12. Insertion Sort on a linked list
"@

wf "QUIZ.md" @"
# Quiz — Sorting Basics

1. Which sort has best best-case time complexity?
2. Is Bubble Sort stable?
3. Which minimizes number of swaps?
4. Worst-case time complexity of Insertion Sort?
5. Why choose Insertion Sort over other O(n²) sorts?
6. What optimization gives Bubble Sort O(n) on sorted data?
7. How many comparisons does Selection Sort always make for n elements?
8. What is an inversion?
"@

wf "FLASHCARDS.md" @"
# Flashcards — Sorting Basics

- Q: Bubble Sort time? → A: O(n²) avg/worst, O(n) best optimized
- Q: Selection Sort stable? → A: No
- Q: Insertion Sort best case? → A: O(n) already sorted
- Q: Selection Sort swaps? → A: O(n) — at most n-1
- Q: Adaptive sort? → A: Insertion Sort
- Q: In-place sorts? → A: All three are in-place
- Q: When use Bubble Sort? → A: Never in production
"@

wf "INTERVIEW.md" @"
# Interview Questions — Sorting Basics

1. "Implement Insertion Sort." — Standard coding question
2. "When would you use Bubble Sort?" — Trap: never
3. "Explain stability in sorting." — Equal elements maintain order
4. "Best algorithm for nearly-sorted data?" — Insertion Sort O(n)
5. "What sort does Java's Arrays.sort() use?" — Dual-Pivot QuickSort (primitives), TimSort (objects)
6. "Define in-place sorting." — O(1) extra space
"@

wf "REFLECTION.md" @"
# Reflection — Sorting Basics

- What patterns do you see across these three algorithms?
- How does the concept of an invariant apply to each?
- Why might you never use these in production despite learning them?
- How does understanding O(n²) help you write better code?
- What real-world scenarios involve nearly-sorted data?
"@

wf "REFERENCES.md" @"
# References — Sorting Basics

- Knuth, D. "The Art of Computer Programming, Vol. 3: Sorting and Searching"
- Sedgewick, R. "Algorithms"
- Cormen, T. et al. "Introduction to Algorithms" (CLRS)
- Java Arrays.sort() Documentation
- Visualgo Sorting: https://visualgo.net/en/sorting
"@

Write-Host "01-sorting-basics: All 24 files created"
