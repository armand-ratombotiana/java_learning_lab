# Mathematical Foundation of Memoization

## 📐 The Recurrence Relation
A recurrence relation is an equation that recursively defines a sequence. 

For the Fibonacci sequence, the recurrence relation is:
$$ T(n) = T(n-1) + T(n-2) + O(1) $$
With base cases: $T(0) = O(1)$, $T(1) = O(1)$.

## 📉 Time Complexity Analysis: Naive Recursion
To solve $T(n)$, the algorithm splits into two branches at every step. This creates a binary tree of execution.
The height of this tree is $n$.
The number of nodes in a full binary tree of height $n$ is $2^n - 1$.

Therefore, the time complexity of naive recursion is **Exponential**: $O(2^n)$.
Actually, because the tree is not perfectly balanced (the right branch reaches the base case faster), the exact time complexity is bounded by the Golden Ratio $\phi \approx 1.618$:
$$ O(\phi^n) $$

## 🚀 Time Complexity Analysis: Memoization
When we apply Memoization, the execution tree is drastically pruned.

Because we cache the result of every subproblem $F(k)$ the very first time we compute it, we never compute the same subproblem twice.
- How many unique subproblems are there for $F(n)$? Exactly $n$ (i.e., $F(1), F(2), \dots, F(n)$).
- How much time does it take to compute a subproblem *if its dependencies are already cached*? Just one addition operation: $O(1)$.

Therefore, the total time complexity is the number of unique subproblems multiplied by the time per subproblem:
$$ n \times O(1) = O(n) $$

By adding a simple cache, we mathematically transformed an algorithm from **Exponential $O(2^n)$** to **Linear $O(n)$**. This is the power of Dynamic Programming.