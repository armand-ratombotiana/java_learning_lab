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
