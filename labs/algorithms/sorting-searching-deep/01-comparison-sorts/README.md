# 01 — Comparison Sorts

<div align="center">

**Insertion Sort · Selection Sort · Bubble Sort Variants · Shell Sort**

</div>

---

## Learning Objectives

- Implement insertion sort and analyze its Ω(n) best-case behavior
- Understand selection sort's O(n²) quadratic nature
- Analyze bubble sort variants (cocktail shaker, comb sort)
- Implement shell sort with multiple gap sequences
- Compare performance characteristics across input distributions

## Prerequisites

- Java 21+ SDK
- Basic array manipulation
- Nested loop constructs
- Big-O complexity notation

## Estimated Time

- **Theory**: 90 minutes
- **Practice**: 120 minutes
- **Exercises**: 60 minutes
- **Total**: 4–5 hours

## Key Concepts

| Concept | Description |
|---------|-------------|
| In-Place Sorting | Sorting without significant extra memory |
| Stability | Equal elements retain relative order |
| Adaptive Sorting | Performance improves on partially sorted data |
| Gap Sequences | The choice of gaps determines shell sort's efficiency |

## Algorithms Covered

### Insertion Sort
- **Best**: Ω(n) — already sorted
- **Average**: Θ(n²)
- **Worst**: O(n²) — reverse sorted
- **Space**: O(1) in-place
- **Stable**: Yes
- **Adaptive**: Yes

### Selection Sort
- **Best**: Ω(n²)
- **Average**: Θ(n²)
- **Worst**: O(n²)
- **Space**: O(1) in-place
- **Stable**: No (by default)
- **Adaptive**: No

### Bubble Sort & Variants
- Standard bubble sort: O(n²) all cases
- Cocktail shaker: bidirectional passes
- Comb sort: shrink factor gap (1.3 typical)

### Shell Sort
- Gap sequences: Shell (n/2^k), Hibbard (2^k-1), Sedgewick (4^k+3·2^{k-1}+1), Pratt (2^p·3^q)
- Worst case depends on gap sequence: from O(n²) to O(n log² n)

## Files

| File | Purpose |
|------|---------|
| `src/main/java/com/alglab/01-comparison-sorts/` | Java implementations |
| `src/test/java/com/alglab/01-comparison-sorts/` | JUnit 5 tests |
| `MINI_PROJECT/` | Sort visualizer exercise |
| `REAL_WORLD_PROJECT/` | Production-grade sorting utility |
| `CHALLENGE/` | Advanced sorting challenges |
| `TESTS/` | Additional test cases |
| `BENCHMARK/` | Performance benchmark harness |
| `SOLUTION/` | Solutions to exercises |
| `DIAGRAMS/` | Visual aids |
