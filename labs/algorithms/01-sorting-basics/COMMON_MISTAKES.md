# Common Mistakes — Sorting Basics

- **Bubble Sort: Off-by-one** — Using j < n-i instead of j < n-i-1 causes overflow
- **Bubble Sort: No early exit** — Missing swapped flag always runs O(n²)
- **Selection Sort: Wrong minIdx update** — Forgetting to update when finding smaller element
- **Selection Sort: Self-swap** — Swapping when minIdx == i (wasted operation)
- **Insertion Sort: Condition order** — Check j >= 0 BEFORE accessing arr[j]
- **Insertion Sort: Losing key** — Using arr[i] after shifting instead of saved key
- **Generic arrays** — Cannot create T[] directly in Java
