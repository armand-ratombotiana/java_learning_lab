# Deep Dive: Dynamic Programming

## 1. DP as DAG (Directed Acyclic Graph)

Every dynamic programming problem can be expressed as finding the shortest/longest path in a DAG where nodes represent subproblems and edges represent dependencies.

### Formal Definition

A DP problem is defined by:
- **State space S**: all possible subproblem identifiers
- **Dependency edges E**: (s → t) if subproblem s depends on t
- **Base cases**: states with no outgoing edges
- **Transition function**: value(s) = f(value(t₁), value(t₂), ..., value(tₖ))

The condition for DP: the dependency graph must be a DAG.

```java
// Generic DP-as-DAG framework:
import java.util.*;

public class DPAsDAG<T> {
    private final Map<String, T> memo = new HashMap<>();
    private final Map<String, List<String>> deps = new HashMap<>();
    private final Function<String, List<String>> dependencyFn;
    private final Function<String, T> baseCaseFn;
    private final BiFunction<T, List<T>, T> combineFn;
    
    public DPAsDAG(Function<String, List<String>> deps,
                    Function<String, T> baseCase,
                    BiFunction<T, List<T>, T> combine) {
        this.dependencyFn = deps;
        this.baseCaseFn = baseCase;
        this.combineFn = combine;
    }
    
    public T solve(String state) {
        if (memo.containsKey(state)) return memo.get(state);
        
        List<String> dependencies = depFn(state);
        if (dependencies.isEmpty()) {
            memo.put(state, baseCaseFn.apply(state));
            return memo.get(state);
        }
        
        List<T> results = new ArrayList<>();
        for (String dep : dependencies) {
            results.add(solve(dep));
        }
        
        T result = combineFn.apply(state, results);
        memo.put(state, result);
        return result;
    }
    
    // Example: Fibonacci as DAG
    // fib(n) depends on fib(n-1) and fib(n-2)
    // Base: fib(0) = 0, fib(1) = 1
    // Transition: fib(n) = fib(n-1) + fib(n-2)
    // DAG: 5 → 4 → 3 → 2 → 1 → 0
    //                         ↓
    //                         0
    public static void main(String[] args) {
        DPAsDAG<Long> fib = new DPAsDAG<>(
            n -> {
                int i = Integer.parseInt(n);
                if (i <= 1) return List.of();
                return List.of(String.valueOf(i - 1), String.valueOf(i - 2));
            },
            n -> {
                int i = Integer.parseInt(n);
                return i <= 1 ? (long) i : 0L;
            },
            (state, vals) -> vals.isEmpty() ? 0L : vals.stream().mapToLong(Long::longValue).sum()
        );
        System.out.println("fib(10) = " + fib.solve("10")); // 55
    }
}

// Key insight: topological ordering of the DAG = bottom-up DP order
```

### Topological Order Proof

For DP correctness, we need a topological order of the subproblem DAG. This is always possible because:

**Theorem**: If every subproblem depends only on "smaller" subproblems (according to some well-founded measure), then the dependency graph is a DAG.

Proof: Define a ranking function r: S → ℕ where r(s) is the "size" of the subproblem. If r(t) < r(s) for every dependency t of state s, then any cycle would create a strictly decreasing sequence of ranks, which is impossible in ℕ.

```java
// Common ranking functions:
// - r(i, j) = i + j (grid problems like unique paths)
// - r(i) = i (sequence problems like LIS)
// - r(i, j, k) = k (interval DP, increasing length)
// - r(mask) = popcount(mask) (bitmask DP)
```

## 2. Optimal Substructure Proofs

### Formal Definition

A problem has **optimal substructure** if an optimal solution can be constructed from optimal solutions of its subproblems.

### Proof Technique

To prove optimal substructure:
1. Assume you have an optimal solution OPT for the problem
2. Show that it contains solutions to subproblems
3. Prove those subproblem solutions must be optimal (by contradiction)

```java
// Example: Longest Increasing Subsequence (LIS)
// 
// Problem: Find longest subsequence of arr that is strictly increasing
// 
// Optimal substructure: 
// For position i, let LIS_len(i) = length of LIS ending at arr[i]
// LIS_len(i) = 1 + max{ LIS_len(j) | j < i and arr[j] < arr[i] }
//
// PROOF:
// Let OPT be the LIS ending at position i, with length L*.
// OPT has a last element arr[i] and second-to-last element arr[j].
// The subsequence ending at arr[j] (call it SUB) must be optimal for position j.
//
// Suppose SUB is not optimal. Then there exists a longer subsequence 
// ending at j, call it BETTER. But then BETTER + [arr[i]] would be a 
// longer subsequence ending at i than OPT, contradicting optimality.
// Therefore SUB must be optimal, proving optimal substructure.

public class OptimalSubstructureProofs {
    // Coin Change optimal substructure:
    // Let minCoins(amount) = min coins to make amount
    // For coin c: minCoins(amount) = 1 + minCoins(amount - c)
    // Proof: In optimal solution, remove one coin → optimal for remainder
    
    // Knapsack optimal substructure:
    // Let dp[i][w] = max value with first i items, capacity w
    // dp[i][w] = max(dp[i-1][w], dp[i-1][w-wi] + vi)
    // Proof: Item i is either included (optimal for i-1, w-wi) or not (optimal for i-1, w)
    
    // Edit Distance optimal substructure:
    // Let ed(i,j) = edit distance between s[0..i] and t[0..j]
    // ed(i,j) = min(ed(i-1,j)+1, ed(i,j-1)+1, ed(i-1,j-1) + cost(s[i],t[j]))
    // Proof: Last operation is insert, delete, or replace/substitute
}
```

## 3. State Compression Techniques

State compression reduces the memory footprint of DP tables, often from O(n²) to O(n) or O(1).

### 2D → 1D Compression

```java
public class StateCompression {
    
    // Example 1: 0/1 Knapsack (compress from O(nW) to O(W))
    public static int knapsackCompressed(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            // Traverse backwards to avoid reusing same item
            for (int w = capacity; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }
    // Original: dp[i][w] depends only on dp[i-1][*]
    // After compression: dp[w] = old dp[w] (skip) or old dp[w-wi] + vi
    
    // Example 2: Unbounded Knapsack (traverse forward!)
    public static int unboundedKnapsack(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int w = 0; w <= capacity; w++) {
            for (int i = 0; i < weights.length; i++) {
                if (w >= weights[i]) {
                    dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
                }
            }
        }
        return dp[capacity];
    }
    // Forward traversal allows reusing same item multiple times
    
    // Example 3: Longest Common Subsequence (2 rows)
    public static int lcsCompressed(String s, String t) {
        int m = s.length(), n = t.length();
        if (n > m) { String tmp = s; s = t; t = tmp; m = s.length(); n = t.length(); }
        
        int[] prev = new int[n + 1];
        for (int i = 1; i <= m; i++) {
            int[] curr = new int[n + 1];
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            prev = curr;
        }
        return prev[n];
    }
    // Space: O(min(m,n)) instead of O(mn)
    
    // Example 4: Fibonacci (O(1) space)
    public static long fibCompressed(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long c = a + b;
            a = b;
            b = c;
        }
        return b;
    }
}
```

### Rolling Array Patterns

```java
// General pattern for rolling arrays:
// If dp[i][j] depends only on dp[i-1][*]:
//   Use 1 array, update in place (correct order matters)
//
// If dp[i][j] depends on dp[i-1][j] and dp[i][j-1] (grid):
//   Use 1 array, update in forward order
//
// If dp[i][j] depends on dp[i-1][j] and dp[i-1][j-wi]:
//   Use 1 array, update in reverse order (knapsack)
//
// If dp[i][j] depends on dp[i-1][j-1], dp[i][j-1], dp[i-1][j]:
//   Use 1 array + one temp variable (edit distance)
//
// If dp[i][j] depends on dp[i-2][*]:
//   Use 2 rolling arrays (swap at each iteration)
```

## 4. DP on Bitmasks (Held-Karp / DP over Subsets)

DP on bitmasks solves problems where the state includes a subset of available items.

```java
public class BitmaskDP {
    // Classic: Traveling Salesman Problem (TSP)
    // dp[mask][last] = min cost to visit set {mask}, ending at city last
    // mask: bit i = 1 if city i visited
    // Transition: dp[mask | (1 << k)][k] = 
    //   min(dp[mask][last] + dist[last][k]) for k not in mask
    
    private static final int INF = Integer.MAX_VALUE / 2;
    
    public static int tsp(int[][] dist) {
        int n = dist.length;
        int[][] dp = new int[1 << n][n];
        for (int[] row : dp) Arrays.fill(row, INF);
        
        // Base: start at city 0
        dp[1][0] = 0;
        
        // Iterate over all masks
        for (int mask = 1; mask < (1 << n); mask++) {
            if ((mask & 1) == 0) continue; // Must include city 0
            
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0) continue;
                if (dp[mask][last] == INF) continue;
                
                // Try going to next city
                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) continue;
                    int newMask = mask | (1 << next);
                    dp[newMask][next] = Math.min(dp[newMask][next], 
                        dp[mask][last] + dist[last][next]);
                }
            }
        }
        
        // Return to start
        int full = (1 << n) - 1;
        int min = INF;
        for (int i = 1; i < n; i++) {
            min = Math.min(min, dp[full][i] + dist[i][0]);
        }
        return min;
    }
    // Time: O(n² · 2ⁿ), Space: O(n · 2ⁿ)
    
    // Example: Partition Equal Subset Sum
    // Can we partition array into two equal-sum subsets?
    public static boolean canPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0) return false;
        int target = sum / 2;
        
        // dp[mask] = sum of subset {mask}
        // Can also use boolean[] dp = new boolean[target + 1] (simpler)
        // But bitmask version shows the DP structure:
        int n = nums.length;
        int[] dp = new int[1 << n];
        
        for (int mask = 1; mask < (1 << n); mask++) {
            int lsb = mask & -mask; // lowest set bit
            int i = Integer.numberOfTrailingZeros(lsb);
            int prev = mask ^ lsb;
            dp[mask] = dp[prev] + nums[i];
            if (dp[mask] == target) return true;
        }
        return false;
    }
    
    // Optimization: Branch and bound
    // - Precompute prefix sums for early cutoff
    // - Prune: if dp[mask] > target, skip (sum already too large)
    // - Heuristic: sort descending to find solution faster
}
```

### Subset Enumeration Technique

```java
// Iterating over submasks of a mask:
public class SubsetEnumeration {
    // For each mask, iterate over its submasks:
    public static void iterateSubmasks(int mask) {
        for (int sub = mask; sub > 0; sub = (sub - 1) & mask) {
            // Process sub (non-empty subset of mask)
        }
        // Also include sub = 0 if needed
    }
    // Complexity: 3ⁿ total over all masks (each element: not in mask, in mask and sub, in mask not sub)
    
    // Example: Minimum Set Cover
    public static int minSetCover(List<Set<Integer>> sets, int universeSize) {
        int n = sets.size();
        int[] coverage = new int[n];
        for (int i = 0; i < n; i++) {
            for (int e : sets.get(i)) {
                coverage[i] |= (1 << e);
            }
        }
        
        int fullMask = (1 << universeSize) - 1;
        int[] dp = new int[1 << universeSize];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;
        
        for (int mask = 0; mask <= fullMask; mask++) {
            if (dp[mask] == Integer.MAX_VALUE / 2) continue;
            for (int i = 0; i < n; i++) {
                int newMask = mask | coverage[i];
                dp[newMask] = Math.min(dp[newMask], dp[mask] + 1);
            }
        }
        return dp[fullMask];
    }
}
```

## 5. DP with Convex Hull Trick (CHT)

CHT optimizes DP transitions of the form:
dp[i] = min/max(dp[j] + a[i]·b[j] + c[j]) where a[i] is monotonic.

### Li Chao Segment Tree (for lines)

```java
// Li Chao Segment Tree — add lines and query min/max at point x
// Lines: y = m*x + b
// Query: min(m_i * x + b_i) for all inserted lines

class LiChaoTree {
    private static class Line {
        long m, b;
        Line(long m, long b) { this.m = m; this.b = b; }
        long eval(long x) { return m * x + b; }
    }
    
    private final long[] tree;
    private final long[] xs;
    private final int n;
    private static final long INF = Long.MAX_VALUE / 2;
    
    public LiChaoTree(long[] xs) {
        this.xs = xs;
        this.n = xs.length;
        int size = 1;
        while (size < n) size <<= 1;
        tree = new long[2 * size];
        Arrays.fill(tree, Integer.MAX_VALUE / 2);
        // Each node stores a line index
    }
    
    // Insert line y = m*x + b
    public void addLine(long m, long b) {
        addLine(m, b, 1, 0, n - 1);
    }
    
    private void addLine(long m, long b, int node, int l, int r) {
        if (l > r) return;
        int mid = (l + r) / 2;
        long xl = xs[l], xm = xs[mid], xr = xs[r];
        
        long newL = m * xl + b;
        long newM = m * xm + b;
        long newR = m * xr + b;
        
        long curL = ...; // current line evaluation at xl
        long curM = ...; // current line evaluation at xm
        long curR = ...; // current line evaluation at xr
        
        // Standard Li Chao segment tree logic
        // If new line is better at all points, replace
        // If worse at all points, discard
        // Otherwise, recurse to child
    }
    
    public long query(long x) {
        int idx = Arrays.binarySearch(xs, x);
        if (idx < 0) idx = -idx - 1;
        return query(idx, 1, 0, n - 1);
    }
}
```

### Classic CHT Optimization

```java
// Problem: dp[i] = min(dp[j] + (prefix[j] - prefix[i])²) where i < j
// = min(dp[j] + prefix[i]² - 2*prefix[i]*prefix[j] + prefix[j]²)
// = prefix[i]² + min(-2*prefix[j]*prefix[i] + dp[j] + prefix[j]²)
// Which is: query for min with m = -2*prefix[j], b = dp[j] + prefix[j]²
// 
// m is monotonic decreasing if prefix is increasing
// → can use deque-based CHT (Convex Hull Trick with monotonic slopes)
//
// Deque-based CHT:
// Store lines in deque, maintain convex hull
// Pop front when query point passes intersection
// Pop back when adding new line makes previous obsolete

public class ConvexHullTrick {
    private final Deque<Line> hull = new ArrayDeque<>();
    
    static class Line {
        long m, b;
        Line(long m, long b) { this.m = m; this.b = b; }
        
        // Intersection x-coordinate with another line
        double intersect(Line other) {
            return (double)(other.b - b) / (m - other.m);
        }
    }
    
    public void add(long m, long b) {
        Line newLine = new Line(m, b);
        // Remove last line if new line makes it obsolete
        while (hull.size() >= 2) {
            Line last = hull.removeLast();
            Line second = hull.peekLast();
            if (last.intersect(newLine) > last.intersect(second)) {
                hull.addLast(last);
                break;
            }
        }
        hull.addLast(newLine);
    }
    
    public long query(long x) {
        // Pop front if next line is better at x
        while (hull.size() >= 2) {
            Line first = hull.peekFirst();
            Line second = hull.getFirst();
            if (first.eval(x) <= second.eval(x)) break;
            hull.removeFirst();
        }
        return hull.peekFirst().eval(x);
    }
}
```

## 6. DP with Divide-and-Conquer Optimization

Condition: dp[i][j] = min(dp[k][j-1] + cost(k+1, i)) where opt[i][j] ≤ opt[i+1][j] (monotonicity of decision points)

```java
public class DnCOptimization {
    // Classic: dp[k][i] = min over j<k of dp[j][i-1] + C(j, k)
    // where C(j, k) is the cost of grouping j..k
    // And opt[i][k] (the best j for dp[i][k]) is monotone in k
    
    // Quadrangle Inequality (QI): C(a, c) + C(b, d) ≤ C(a, d) + C(b, c) for a ≤ b ≤ c ≤ d
    // Monotonicity: C(b, c) ≤ C(a, d) for a ≤ b ≤ c ≤ d
    // When QI holds, opt[i][j] is monotone → D&C optimization
    
    public static void solve(int n, int K) {
        int[][] dp = new int[K+1][n+1];
        int[][] opt = new int[K+1][n+1];
        
        // Initialize for k=1 (first group)
        for (int i = 0; i <= n; i++) {
            dp[1][i] = cost(0, i);
            opt[1][i] = 0;
        }
        
        // For each additional group
        for (int k = 2; k <= K; k++) {
            compute(k, 0, n, 0, n, dp, opt);
        }
    }
    
    private static void compute(int k, int l, int r, int optL, int optR, 
                                  int[][] dp, int[][] opt) {
        if (l > r) return;
        int mid = (l + r) / 2;
        
        dp[k][mid] = Integer.MAX_VALUE;
        int best = -1;
        for (int j = optL; j <= Math.min(mid, optR); j++) {
            int val = dp[k-1][j] + cost(j, mid);
            if (val < dp[k][mid]) {
                dp[k][mid] = val;
                best = j;
            }
        }
        opt[k][mid] = best;
        
        // Recurse — opt bounds are monotonic
        compute(k, l, mid-1, optL, best, dp, opt);
        compute(k, mid+1, r, best, optR, dp, opt);
    }
    
    // Example costs
    private static int cost(int i, int j) {
        // Example: number of inversions in arr[i..j]
        // Precompute for O(1) cost queries
        return (j - i + 1) * (j - i + 1); // placeholder
    }
}
```

## 7. DP with Monotone Queue

Condition: dp[i] = max/min(dp[j] + f(j, i)) where f can be decomposed as g(i) + h(j) and j is in a sliding window.

```java
public class MonotoneQueueDP {
    // Classic: Sliding Window Maximum
    // dp[i] = max(dp[j] + arr[i]) for j ∈ [i-k, i-1]
    // = arr[i] + max(dp[j]) for j in window
    
    public static int maxSumSlidingWindow(int[] arr, int k) {
        int n = arr.length;
        int[] dp = new int[n];
        Deque<Integer> dq = new ArrayDeque<>(); // stores indices, decreasing dp values
        
        dp[0] = arr[0];
        dq.addLast(0);
        
        for (int i = 1; i < n; i++) {
            // Remove out-of-window elements
            while (!dq.isEmpty() && dq.peekFirst() < i - k) {
                dq.removeFirst();
            }
            
            // dp[i] = arr[i] + max(dp[j]) in window
            dp[i] = arr[i] + (dq.isEmpty() ? 0 : dp[dq.peekFirst()]);
            
            // Maintain monotone decreasing queue
            while (!dq.isEmpty() && dp[dq.peekLast()] <= dp[i]) {
                dq.removeLast();
            }
            dq.addLast(i);
        }
        
        return Arrays.stream(dp).max().orElseThrow();
    }
    
    // Another classic: DP[i] = max(dp[j] + score(j,i)) 
    // where score(j,i) depends on sum of arr[j..i]
    // Kadane's algorithm is a special case
    
    public static int maxSubarraySum(int[] arr) {
        // dp[i] = max subarray ending at i
        // dp[i] = max(arr[i], dp[i-1] + arr[i])
        // Monotone queue with window size = ∞
        int maxSoFar = arr[0], maxEndingHere = arr[0];
        for (int i = 1; i < arr.length; i++) {
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }
}
```

## 8. Summary: DP Optimization Decision Tree

```java
// Is DP needed here?
// ├── No → Use greedy/math
// ├── Simple DP O(n²) acceptable? → Implement basic DP
// └── Need optimization?
//     ├── Transition uses window [j-k, j] → Monotone Queue
//     ├── Transition depends on prefix min/max → Prefix optimization
//     ├── Transition has form dp[i] = min(dp[j] + a[i]*b[j]) → CHT (Li Chao)
//     ├── Decision point is monotone → Divide & Conquer DP
//     ├── State includes subsets → Bitmask DP
//     ├── State space is 2D, one dim large → State compression (rolling array)
//     └── State space is large → Consider greedy, A*, or approximation

public class DPOptimizationSelector {
    // Optimization | Time before | Time after | Condition
    // Monotone Queue | O(n²) | O(n) | Sliding window dependency
    // Convex Hull Trick | O(n²) | O(n log n) | Line dependency
    // Divide & Conquer | O(kn²) | O(kn log n) | Quadrangle inequality
    // Bitmask | O(n!) | O(n²·2ⁿ) | Subset states
    // State compression | O(n²) | O(n) | Only needs prev row
}
```

The key insight is that DP optimization is about exploiting structure in the recurrence relation. Every optimization technique above leverages a specific structural property (monotonicity, convexity, bounded dependence) to avoid redundant computation.
