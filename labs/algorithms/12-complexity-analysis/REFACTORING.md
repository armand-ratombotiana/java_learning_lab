# Refactoring for Complexity

## Reduce Quadratic to Linear
`java
// O(n²): nested loop search
for (int a : listA)
    for (int b : listB)
        if (a == b) result.add(a);

// O(n): HashSet lookup
Set<Integer> setB = new HashSet<>(listB);
for (int a : listA)
    if (setB.contains(a)) result.add(a);
`

## Reduce O(n) to O(log n)
Use binary search, balanced BST, or binary heap.
