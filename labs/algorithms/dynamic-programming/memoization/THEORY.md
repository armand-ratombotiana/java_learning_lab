# Dynamic Programming Theory & Intuition

## 💡 The Core Philosophy
"Those who cannot remember the past are condemned to repeat it." — George Santayana.

This quote perfectly describes naive recursive algorithms. 
Dynamic Programming (DP) is an algorithmic paradigm that solves a complex problem by breaking it down into simpler subproblems. 

A problem must have two key attributes to be solvable by DP:
1. **Optimal Substructure**: The optimal solution to the main problem can be constructed from optimal solutions to its subproblems. (e.g., The shortest path from A to C via B is the shortest path from A to B + the shortest path from B to C).
2. **Overlapping Subproblems**: The algorithm solves the exact same subproblem multiple times.

## 🔄 The Fibonacci Example
The classic example is the Fibonacci sequence: $F(n) = F(n-1) + F(n-2)$.
If we want to calculate $F(5)$, we must calculate $F(4)$ and $F(3)$.
But to calculate $F(4)$, we must calculate $F(3)$ and $F(2)$.

Notice that $F(3)$ is being calculated twice! As $n$ grows, the number of redundant calculations explodes exponentially. 
To calculate $F(50)$, the algorithm will calculate $F(2)$ billions of times.

## 🧠 The Two DP Approaches

### 1. Top-Down (Memoization)
This approach keeps the natural, elegant recursive structure of the algorithm, but adds a "memo pad" (a cache, usually a HashMap or an Array).
- Before computing $F(n)$, check the cache. If it's there, return it instantly $O(1)$.
- If it's not there, compute it, save the result in the cache, and then return it.
- **Benefit**: Easy to write. Only computes subproblems that are strictly necessary.

### 2. Bottom-Up (Tabulation)
This approach abandons recursion entirely. It builds a table (an array) starting from the smallest subproblems and working its way up to the main problem.
- To calculate $F(50)$, it creates an array of size 51.
- It fills `arr[0] = 0`, `arr[1] = 1`.
- It loops from 2 to 50, filling `arr[i] = arr[i-1] + arr[i-2]`.
- **Benefit**: No recursion means no call stack overhead. Avoids `StackOverflowError`. Usually slightly faster and uses less memory.