# Theory — Comparison Sorts

## 1. Insertion Sort

### Algorithm
Insertion sort builds the final sorted array one element at a time. It iterates through the input, removing one element per iteration and inserting it into the correct position in the already-sorted prefix.

```
for i = 1 to n-1:
    key = a[i]
    j = i - 1
    while j >= 0 and a[j] > key:
        a[j+1] = a[j]
        j = j - 1
    a[j+1] = key
```

### Correctness Proof
**Loop Invariant**: At the start of each iteration of the outer loop (index i), the subarray a[0..i-1] contains the original elements of that subarray in sorted order.

- **Initialization**: Before the first iteration (i=1), the subarray a[0..0] is trivially sorted.
- **Maintenance**: The inner while loop shifts elements greater than key to the right, then inserts key in the gap. This preserves the sorted property of a[0..i].
- **Termination**: When i = n, the entire array is sorted.

## 2. Selection Sort

### Algorithm
Selection sort repeatedly finds the minimum element from the unsorted part and places it at the beginning.

```
for i = 0 to n-2:
    minIdx = i
    for j = i+1 to n-1:
        if a[j] < a[minIdx]:
            minIdx = j
    swap a[i], a[minIdx]
```

### Analysis
- Always performs exactly n(n-1)/2 comparisons regardless of input.
- Performs exactly n swaps (one per position).
- The quadratic nature stems from the fact that each minimum search is O(n).

## 3. Bubble Sort Variants

### Standard Bubble Sort
Repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order.

```
for i = 0 to n-2:
    for j = 0 to n-2-i:
        if a[j] > a[j+1]:
            swap a[j], a[j+1]
```

### Optimized Bubble Sort
Early termination when no swaps occur during a pass (already sorted detection).

### Cocktail Shaker Sort
A bidirectional variant that passes left-to-right then right-to-left each iteration. This handles turtles (small elements near the end) more efficiently.

### Comb Sort
Uses a shrinking gap factor. Initially the gap is n, then shrinks by a factor of 1.3 each pass. This eliminates turtles efficiently.

```
gap = n
while gap > 1 or swapped:
    gap = max(1, floor(gap / 1.3))
    swapped = false
    for i = 0 to n-gap-1:
        if a[i] > a[i+gap]:
            swap a[i], a[i+gap]
            swapped = true
```

## 4. Shell Sort

### Algorithm
Shell sort generalizes insertion sort by allowing the exchange of far-apart elements. It sorts h-subsequences using the insertion sort principle.

```
for each gap h in gapSequence:
    for i = h to n-1:
        key = a[i]
        j = i
        while j >= h and a[j-h] > key:
            a[j] = a[j-h]
            j = j - h
        a[j] = key
```

### Gap Sequences

| Sequence | Formula | Worst Case |
|----------|---------|-----------|
| Shell | ⌊n/2^k⌋ | O(n²) |
| Hibbard | 2^k − 1 | O(n^(3/2)) |
| Sedgewick | 4^k + 3·2^(k−1) + 1 | O(n^(4/3)) |
| Pratt | 2^p · 3^q | O(n log² n) |

## 5. Theoretical Lower Bound

Comparison-based sorting has a lower bound of Ω(n log n). All algorithms in this lab exceed this bound for worst-case inputs because they use only adjacent comparisons (insertion, bubble) or have suboptimal gap structures (shell).

## 6. Stability Analysis

- **Insertion sort**: Stable — elements are not moved past equal elements
- **Selection sort**: Unstable — a swap can move an equal element past another
- **Bubble sort**: Stable — only swaps adjacent elements when strictly greater
- **Shell sort**: Unstable — long-distance swaps destroy relative order

## 7. Complexity Summary

| Algorithm | Best | Average | Worst | Space | Stable | Adaptive |
|-----------|------|---------|-------|-------|--------|----------|
| Insertion | Ω(n) | Θ(n²) | O(n²) | O(1) | Yes | Yes |
| Selection | Ω(n²) | Θ(n²) | O(n²) | O(1) | No | No |
| Bubble | Ω(n) | Θ(n²) | O(n²) | O(1) | Yes | Yes |
| Cocktail | Ω(n) | Θ(n²) | O(n²) | O(1) | Yes | Yes |
| Comb | Ω(n log n) | Θ(n²) | O(n²) | O(1) | No | Yes |
| Shell (varies) | Ω(n log n) | — | O(n^(4/3)-n²) | O(1) | No | Yes |

## 8. Input Distribution Effects

- **Nearly sorted**: Insertion sort is optimal (linear time)
- **Reverse sorted**: Insertion sort is worst-case (quadratic)
- **Few unique values**: Selection sort is unaffected
- **Random**: All O(n²) algorithms perform similarly
- **Partially sorted with distant elements**: Shell sort with good gaps outperforms
