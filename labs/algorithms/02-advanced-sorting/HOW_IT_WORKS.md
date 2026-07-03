# How Advanced Sorting Works

## Merge Sort Trace
[38,27,43,3,9,82,10]
Divide: [38,27,43,3] | [9,82,10]
        [38,27] | [43,3]    [9,82] | [10]
Merge:  [27,38] | [3,43]    [9,82] | [10]
        [3,27,38,43]      [9,10,82]
        [3,9,10,27,38,43,82]

## Quick Sort Trace (pivot = last)
[10,7,8,9,1,5] pivot=5 → [1,5,8,9,10,7]
Recurse: [1] | [8,9,10,7] → [7,8,9,10] → ...

## Heap Sort Trace
Build: [5,3,8,4,2] → [8,4,5,3,2]
Extract 8 → [5,4,2,3|8]
Extract 5 → [4,3,2|5,8]
Extract 4 → [3,2|4,5,8]
Extract 3 → [2|3,4,5,8]
