# How Complexity Analysis Works

## Analyzing Loop Complexity
`java
// O(n)
for (int i = 0; i < n; i++) { sum += arr[i]; }

// O(n²)
for (int i = 0; i < n; i++)
    for (int j = 0; j < n; j++)
        sum += arr[i][j];

// O(n log n)
for (int i = 0; i < n; i++)
    for (int j = i; j > 0; j /= 2)
        sum += j;
`

## Recurrence Analysis
Merge Sort: T(n) = 2T(n/2) + O(n)
- Level 0: 1 node, O(n) work
- Level 1: 2 nodes, O(n/2) each → O(n) total
- Level 2: 4 nodes, O(n/4) each → O(n) total
- Total: log₂(n) levels × O(n) = O(n log n)
