# How Randomized Algorithms Work

## Randomized Quickselect Example

Array: [7, 3, 9, 2, 8, 5, 1, 4], find 3rd smallest (k=2, 0-indexed)

Pick random pivot, say index 3 (value 2):
  Partition: [1, 2, 7, 3, 9, 8, 5, 4], pivot at position 1
  Pivot position 1 < k=2: recurse right partition [7, 3, 9, 8, 5, 4]
    Pick random pivot, say 8: [7, 3, 5, 4, 8, 9], pivot at position 4
    Pivot position > k: recurse left [7, 3, 5, 4]
      Pick random pivot 3: [3, 7, 5, 4], pivot at position 0
      k' = 2 - 0 - 1 = 1: recurse right [7, 5, 4]
        Pick random pivot 5: [4, 5, 7], pivot at position 1
        Pivot position == k': return 5

## Fisher-Yates Shuffle Example

Array: [A, B, C, D]
i=3: pick random 0-3, say 1: swap -> [A, D, C, B]
i=2: pick random 0-2, say 0: swap -> [C, D, A, B]
i=1: pick random 0-1, say 0: swap -> [D, C, A, B]
Done. Final: [D, C, A, B]
