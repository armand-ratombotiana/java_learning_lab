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
