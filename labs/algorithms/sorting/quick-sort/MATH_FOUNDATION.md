# Mathematical Foundation of Quick Sort

## 📐 Average Case Analysis: $O(n \log n)$
The performance of Quick Sort depends on the balance of the partitions. In the best case, the pivot always splits the array into two equal halves.

The recurrence relation for the best case is:
$$ T(n) = 2T(n/2) + \Theta(n) $$
Using the Master Theorem, this solves to **$O(n \log n)$**.

In the **Average Case**, even if the split is not perfectly equal (e.g., a 10:90 split), the height of the recursion tree still remains logarithmic. The average number of comparisons for Quick Sort is proved to be:
$$ C(n) \approx 2n \ln n \approx 1.39n \log_2 n $$

## 📉 Worst Case Analysis: $O(n^2)$
The worst case occurs when the pivot is always the smallest or largest element in the array. This happens if the input is **already sorted** and we pick the first or last element as the pivot.

The recurrence relation becomes:
$$ T(n) = T(n-1) + T(0) + \Theta(n) $$
$$ T(n) = T(n-1) + \Theta(n) = \Theta(n^2) $$

This turns the recursion tree into a linked list of depth $n$, leading to $O(n^2)$ time and potentially a `StackOverflowError`.

## 🛡️ Mitigation: Pivot Selection
To avoid the $O(n^2)$ trap, we use better pivot selection strategies:
1. **Randomized Pivot**: Pick a random index as the pivot. This makes the $O(n^2)$ case statistically impossible for any fixed input.
2. **Median-of-Three**: Look at the first, middle, and last elements, and pick the median of those three as the pivot. This is the standard strategy in many production libraries.

## 📏 Space Complexity: $O(\log n)$
While Quick Sort is an "In-Place" algorithm (it doesn't use extra arrays), it is not $O(1)$ space. It requires space on the **Call Stack** for recursion. With a good pivot strategy, the stack depth is $O(\log n)$.