# Math Foundation — Comparison Sorts

## 1. Inversions and Sort Complexity

An **inversion** is a pair of indices (i, j) such that i < j and a[i] > a[j]. The number of inversions in an array measures how far it is from being sorted.

- Sorted array: 0 inversions
- Reverse-sorted array of size n: n(n-1)/2 inversions

### Insertion Sort and Inversions
Insertion sort performs exactly one swap per inversion eliminated. If I is the number of inversions, insertion sort runs in O(n + I) time.

- Best case (sorted): I = 0 → O(n)
- Worst case (reverse): I = n(n-1)/2 → O(n²)
- Average case: I = n(n-1)/4 → Θ(n²)

**Proof of expected inversions**: For random permutation, each pair (i,j) has 50% probability of being inverted. There are C(n,2) = n(n-1)/2 pairs, so expected inversions = n(n-1)/4.

## 2. Comparison Lower Bound

Any comparison-based sorting algorithm requires at least ⌈log₂(n!)⌉ comparisons in the worst case.

### Derivation
- There are n! possible permutations of n elements
- Each comparison has 2 outcomes, so a decision tree with k comparisons can distinguish at most 2^k leaves
- To distinguish all n! permutations: 2^k ≥ n!
- k ≥ log₂(n!) = n log₂ n − n log₂ e + O(log n) (Stirling's approximation)

Stirling's approximation: n! ≈ √(2πn) · (n/e)^n
log₂(n!) = n log₂ n − n log₂ e + O(log n) ≈ n log₂ n − 1.44n + O(log n)

Thus lower bound is Ω(n log n).

## 3. Summation Analysis

### Selection Sort Comparisons
```
Total = Σ(i=0 to n-2) Σ(j=i+1 to n-1) 1
      = Σ(i=0 to n-2) (n-1-i)
      = (n-1) + (n-2) + ... + 1
      = n(n-1)/2
      = Θ(n²)
```

### Bubble Sort Comparisons
For standard bubble sort (without early termination):
```
Total = Σ(i=0 to n-2) (n-1-i) = n(n-1)/2 = Θ(n²)
```

With early termination, best case needs only n-1 comparisons.

### Insertion Sort Comparisons
```
Best:  n-1 comparisons
Worst: Σ(i=1 to n-1) (i) = n(n-1)/2 comparisons
Avg:   Σ(i=1 to n-1) (i/2 + 1) ≈ n²/4 comparisons
```

## 4. Shell Sort Gap Analysis

The performance of shell sort depends critically on the gap sequence.

### Theorem (Poonen, 1993)
Shell sort with gap sequence g_k, g_{k-1}, ..., g_1 = 1 has worst-case running time O(n · Σ g_k) if the sequence satisfies certain divisibility conditions.

### Sedgewick Gap Performance
The sequence 1, 5, 19, 41, 109, ... (4^k + 3·2^{k-1} + 1) gives O(n^(4/3)) worst case.

### Pratt's Gap Performance
Using all numbers of the form 2^p · 3^q less than n gives O(n log² n) worst case.

**Proof sketch**: For each pair of powers (p,q), the algorithm performs O(n) work, and there are O(log² n) such pairs.

## 5. Stability and Sorting Networks

A sorting algorithm is **stable** if equal elements retain their relative order. This is important when sorting by multiple keys.

For comparison sorts:
- Stable: Insertion, Bubble, Merge
- Unstable: Selection, Shell, Quick, Heap

## 6. Average Case via Probability

For random permutation of distinct elements:
- Insertion sort makes ~n²/4 comparisons and ~n²/4 moves on average
- Selection sort always makes n(n-1)/2 comparisons
- Bubble sort makes ~n²/2 comparisons on average

## 7. Space Complexity

All algorithms in this lab are in-place (O(1) extra space) except for:
- Binary insertion sort: O(log n) extra (binary search on sorted portion)
- Shell sort: O(1) plus gap array O(g) where g is the number of gaps
