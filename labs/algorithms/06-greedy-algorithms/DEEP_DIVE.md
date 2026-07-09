# Deep Dive: Greedy Algorithms

## 1. Exchange Argument Proofs

Exchange arguments are the primary technique for proving greedy algorithm optimality.

### Exchange Argument Framework

To prove greedy gives the optimal solution:

1. **Define a solution format**: Let OPT = any optimal solution
2. **Compare greedy vs OPT**: Find the first position where greedy and OPT differ
3. **Exchange**: Show we can modify OPT by exchanging its choice with greedy's choice without making the solution worse
4. **Iterate**: Repeat until OPT becomes identical to greedy solution
5. **Conclusion**: Greedy is at least as good as OPT, hence optimal

```java
public class ExchangeArgumentProofs {
    
    // Problem 1: Activity Selection
    // Greedy: pick the activity with earliest finish time
    // 
    // PROOF:
    // Let OPT be an optimal set of non-overlapping activities.
    // Let G be the greedy solution.
    // Let a_i be first activity where G and OPT differ:
    //   G picks g = earliest-finishing activity
    //   OPT picks o (some later-finishing activity)
    // 
    // Exchange: Replace o with g in OPT.
    //   - g finishes before o (by greedy choice)
    //   - So g doesn't overlap with other activities in OPT (they start after o ends)
    //   - New solution OPT' has same size as OPT
    //   - By iterating, OPT can be transformed into G
    // Therefore |G| = |OPT|, greedy is optimal.
    
    public static int activitySelection(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1])); // sort by finish time
        int count = 0, lastEnd = 0;
        for (int[] act : intervals) {
            if (act[0] >= lastEnd) {
                count++;
                lastEnd = act[1];
            }
        }
        return count;
    }
    
    // Problem 2: Minimum Spanning Tree (Kruskal's)
    // Greedy: pick the smallest weight edge that doesn't create a cycle
    //
    // PROOF (Cut property):
    // Let F be the set of edges chosen so far (a forest).
    // Let S be a connected component of F.
    // Let e = (u,v) be the smallest edge crossing the cut (S, V\S).
    // If e is not in MST, add e → creates cycle.
    // Remove the heaviest edge in cycle crossing the cut (≥ weight(e)).
    // Result is another MST containing e.
    // By induction, greedy picks only MST edges.
    
    // Problem 3: Huffman Coding
    // Greedy: repeatedly merge two smallest frequency nodes
    //
    // PROOF:
    // For the two smallest probability symbols a, b:
    // There exists an optimal prefix code where a and b are siblings
    // at the deepest level.
    //
    // Exchange argument:
    // If a and b are not at deepest level, exchange them with deeper nodes.
    // The total cost doesn't increase (because a,b have smallest frequencies).
    // Merging a,b reduces to (n-1)-symbol problem.
    // By induction, greedy is optimal.
}
```

### Formal Exchange Lemma

```java
// Formal framework for exchange arguments:
// 
// Lemma: If a greedy choice g and an optimal choice o differ at the first 
// decision point, then exchanging o for g in the optimal solution yields 
// a solution that is at least as good as the original optimal solution.
//
// Proof structure:
// 1. Let G be greedy's first choice, O be optimal's first choice, G ≠ O.
// 2. Let O' = (O \ {o}) ∪ {g} (exchange).
// 3. Show O' is feasible (doesn't violate constraints).
// 4. Show O' is at least as good as O.
// 5. By induction, the greedy solution is optimal.

public class ExchangeLemma {
    // Problem: Schedule jobs with deadlines and profits.
    // Each job takes 1 unit time, has deadline d and profit p.
    // Greedy: sort by profit descending, schedule job at latest available slot.
    //
    // Exchange proof:
    // If greedy picks job j with profit p, and optimal picks job k with p_k > p_j,
    // swap them in optimal solution.
    // Since j has higher profit, the swapped solution is at least as good.
    
    static class Job {
        int deadline, profit;
        Job(int d, int p) { this.deadline = d; this.profit = p; }
    }
    
    public static int jobScheduling(Job[] jobs) {
        Arrays.sort(jobs, (a, b) -> b.profit - a.profit);
        int maxDeadline = Arrays.stream(jobs).mapToInt(j -> j.deadline).max().orElse(0);
        int[] slots = new int[maxDeadline + 1]; // -1 = empty
        
        int totalProfit = 0;
        for (Job job : jobs) {
            // Find latest available slot before deadline
            for (int t = job.deadline; t > 0; t--) {
                if (slots[t] == 0) {
                    slots[t] = job.profit;
                    totalProfit += job.profit;
                    break;
                }
            }
        }
        return totalProfit;
    }
}
```

## 2. Matroid Theory

Matroids provide a rigorous mathematical foundation for when greedy is optimal.

### Definition

A **matroid** M = (S, I) consists of:
- A finite set S (ground set)
- A collection I of subsets of S (independent sets) satisfying:
  1. **Empty set property**: ∅ ∈ I
  2. **Hereditary property**: If A ∈ I and B ⊆ A, then B ∈ I
  3. **Exchange property**: If A, B ∈ I and |A| < |B|, then ∃x ∈ B\A such that A ∪ {x} ∈ I

```java
public class MatroidTheory {
    // Weighted Matroid Greedy Algorithm:
    // Given matroid (S, I) with weight function w: S → ℝ⁺:
    // 1. Sort S by weight descending
    // 2. Initialize A = ∅
    // 3. For each x in sorted order:
    //      if A ∪ {x} ∈ I, add x to A
    // 4. Result A is maximum-weight independent set
    
    // THEOREM: The greedy algorithm finds the maximum-weight independent 
    // set in any matroid. Moreover, if the greedy algorithm is optimal 
    // for all weight functions, the structure is a matroid.
    
    // Examples of matroids:
    
    // 1. Graphic Matroid (spanning trees)
    //    S = edges of graph G
    //    I = acyclic edge sets (forests)
    //    Max-weight independent set = Maximum Spanning Tree
    //    Kruskal's algorithm = greedy on graphic matroid
    //
    // 2. Uniform Matroid
    //    S = any set
    //    I = subsets of size ≤ k
    //    Greedy: pick k largest-weight elements (trivial)
    //
    // 3. Transversal Matroid (matching in bipartite graph)
    //    S = vertices of a bipartite graph side A
    //    I = subsets of A that can be matched to distinct vertices in B
    //
    // 4. Partition Matroid
    //    S partitioned into disjoint groups S₁, S₂, ..., Sₖ
    //    Each group has capacity cᵢ
    //    I = subsets with ≤ cᵢ elements from group i
    //
    // 5. Linear Matroid (vector matroid)
    //    S = vectors in a vector space
    //    I = linearly independent subsets

    // Example: Uniform matroid with capacity k
    // Pick k largest-weight elements
    public static int uniformMatroid(int[] weights, int k) {
        Arrays.sort(weights);
        int sum = 0;
        for (int i = weights.length - 1; i >= weights.length - k; i--) {
            sum += weights[i];
        }
        return sum;
    }
}
```

### The Matroid Intersection Problem

```java
// When a problem is the intersection of two matroids:
// Find max-weight set I ∈ I₁ ∩ I₂
// This is solvable in polynomial time (not greedy, but matroid intersection algorithm)
//
// Example: Bipartite Matching
// Let G = (U ∪ V, E). Matroid M₁: independence in U (partition matroid)
// Matroid M₂: independence in V (partition matroid)
// Intersection = matchings in bipartite graph
//
// Example: Arborescence (directed spanning tree)
// Matroid M₁: graphic matroid on underlying undirected graph
// Matroid M₂: each node (except root) has exactly one incoming edge (partition matroid)
// Intersection = spanning arborescence
}
```

## 3. Greedy vs DP Classification

### How to decide if a problem is Greedy or DP:

```java
public class GreedyVsDP {
    // Decision framework:
    //
    // Does the problem have optimal substructure?
    //   └── No → Not DP or greedy
    //   └── Yes → Does greedy choice lead to optimal?
    //        ├── Yes → Greedy (exchange argument works)
    //        └── No → DP (need to explore subproblem combinations)
    //
    // Key indicators for greedy:
    // - All decisions are "final" (never need to undo)
    // - Local optimal → global optimal (no future consequences)
    // - Exchange argument is straightforward
    // - Matroid structure
    //
    // Key indicators for DP:
    // - Decision today affects future options
    // - Need to try multiple choices at each step
    // - Overlapping subproblems
    // - Optimal substructure but no greedy property
    
    // Examples:
    // Activity Selection → Greedy (earliest finish)
    // Fractional Knapsack → Greedy (value/weight ratio)
    // 0/1 Knapsack → DP (greedy fails)
    // Minimum Spanning Tree → Greedy (cut property)
    // Shortest Path (non-negative) → Greedy (Dijkstra)
    // Shortest Path (negative edges) → DP (Bellman-Ford)
    // Change Making (canonical) → Greedy
    // Change Making (non-canonical) → DP
    // Huffman Coding → Greedy
    // Edit Distance → DP
}
```

### Counterexamples: When Greedy Fails

```java
public class GreedyFails {
    // 1. 0/1 Knapsack
    // Items: (weight=10, value=60), (20, 100), (30, 120)
    // Capacity: 50
    // Greedy by value/weight ratio: item1(6) → item2(5) → item3(4)
    // Greedy picks: (10,60) + (20,100) = value 160 (capacity 30)
    // Optimal: (20,100) + (30,120) = value 220 (capacity 50)
    // 
    // Why greedy fails: can't take fractional items, and best ratio item
    // may leave unusable capacity
    
    // 2. Change Making (non-canonical coin system)
    // Coins: {1, 3, 4}
    // Amount: 6
    // Greedy (largest first): 4 + 1 + 1 = 3 coins
    // Optimal: 3 + 3 = 2 coins
    //
    // Coins are "canonical" if greedy works for all amounts:
    // {1, 5, 10, 25} is canonical (standard US coins)
    // Testing: amount = 2*max_coin works as a sufficient check
    
    // 3. Maximum Bipartite Matching
    // Greedy: pick edge incident to unmatched vertices with smallest degree
    // Can get stuck with suboptimal matching
    // Need augmenting paths (Kuhn-Munkres / Hungarian)
    
    // 4. Longest Path (general graph)
    // Greedy: at each node, pick highest-weight outgoing edge
    // May miss longer paths that start with a low-weight choice
    // NP-hard in general graphs
    
    // 5. Vertex Cover
    // Greedy: pick vertex with highest degree
    // Approximation ratio: 2× optimal in general
    // Not optimal for all cases
}
```

## 4. Fractional Knapsack: Greedy Canonical Example

The fractional knapsack is the textbook example of a solvable greedy problem.

```java
public class FractionalKnapsack {
    static class Item {
        double weight, value;
        double ratio() { return value / weight; }
        Item(double w, double v) { weight = w; value = v; }
    }
    
    public static double fractionalKnapsack(Item[] items, double capacity) {
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio(), a.ratio()));
        
        double totalValue = 0;
        for (Item item : items) {
            if (capacity >= item.weight) {
                totalValue += item.value;
                capacity -= item.weight;
            } else {
                totalValue += item.ratio() * capacity;
                break;
            }
        }
        return totalValue;
    }
    
    // Optimality Proof (Exchange Argument):
    // 
    // Let OPT be an optimal solution with items sorted by ratio.
    // Let i be first index where greedy and OPT differ.
    // If OPT[i] < greedy[i] (took less fractional amount):
    //   - Replace OPT[i] with greedy[i]'s amount (more value per weight)
    //   - The replaced weight goes to lower-ratio items in OPT
    //   - Total value increases → contradiction with optimality
    // 
    // More formally:
    // Greedy: fill with highest ratio items first.
    // Let α = weight of item i taken by greedy, β = weight taken by OPT.
    // If β < α, replace β of item i with β of item i (waste).
    // Actually: exchange a small amount ε from lower-ratio item j 
    // in OPT with ε from higher-ratio item i.
    // New value = old value + ε(ratio(i) - ratio(j)) > old value.
    // Therefore optimal must match greedy.
}
```

### Extended Greedy Analysis

```java
// 0/1 Knapsack vs Fractional:
// 
// Fractional: continuous, greedy works
// Proof: Exchange argument with ε-trades
//
// 0/1 Knapsack: discrete, greedy fails
// Counterexample: 3 items with capacity 50
// Item A: (w=10, v=60, r=6)
// Item B: (w=20, v=100, r=5)  
// Item C: (w=30, v=120, r=4)
// Greedy by ratio: A + B = 160 vs optimal B + C = 220
//
// But greedy by ratio gives 2-approximation:
// Let OPT = optimal, GREEDY = greedy
// Best item value: max(v_i) ≤ GREEDY
// Ratio upper bound: value of fractional solution V* ≥ OPT
// Greedy gets at least V*/2 (since removing first item leaves ≤ V*/2)
// Actually, greedy gets > OPT/2 for 0/1 knapsack
```

## 5. Huffman Coding Optimality Proof

Huffman coding is the optimal prefix code for a known symbol distribution.

### Problem Statement

Given symbols a₁,...,aₙ with frequencies f₁,...,fₙ, find a prefix code (binary tree) minimizing:
∑ fᵢ × depth(aᵢ) (total weighted path length)

### Greedy Algorithm

```java
public class HuffmanCodingProof {
    // Algorithm: Repeatedly merge two smallest frequency nodes.
    // 
    // PROOF OF OPTIMALITY:
    //
    // Lemma 1 (Smallest nodes at deepest level):
    // There exists an optimal prefix code where the two smallest-frequency
    // symbols are siblings at the deepest level.
    //
    // Proof: Let a, b be the smallest symbols. Let x, y be siblings at 
    // deepest level. Exchange a↔x and b↔y. The total cost:
    //   Δ = f(x)·depth(a) + f(a)·depth(x) + f(y)·depth(b) + f(b)·depth(y)
    //      - f(a)·depth(a) - f(b)·depth(b) - f(x)·depth(x) - f(y)·depth(y)
    // Since f(a) ≤ f(x) and depth(x) ≥ depth(a), the exchange doesn't
    // increase cost. Same for b↔y. So new tree is at least as good.
    //
    // Lemma 2 (Merging preserves optimality):
    // Given Lemma 1, replace a,b with a new symbol c where f(c) = f(a)+f(b).
    // An optimal tree for the merged problem gives an optimal tree for original.
    //
    // By induction on n, the greedy algorithm (merge smallest) is optimal.
    
    static class HuffmanNode implements Comparable<HuffmanNode> {
        int frequency;
        HuffmanNode left, right;
        
        HuffmanNode(int f) { frequency = f; }
        
        @Override
        public int compareTo(HuffmanNode o) {
            return Integer.compare(this.frequency, o.frequency);
        }
    }
    
    public static HuffmanNode buildHuffmanTree(int[] frequencies) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (int f : frequencies) {
            pq.add(new HuffmanNode(f));
        }
        
        while (pq.size() > 1) {
            HuffmanNode a = pq.poll();
            HuffmanNode b = pq.poll();
            HuffmanNode parent = new HuffmanNode(a.frequency + b.frequency);
            parent.left = a;
            parent.right = b;
            pq.add(parent);
        }
        
        return pq.poll();
    }
    
    // Calculate weighted path length (cost)
    public static int totalCost(HuffmanNode root) {
        return dfsCost(root, 0);
    }
    
    private static int dfsCost(HuffmanNode node, int depth) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) {
            return node.frequency * depth; // leaf
        }
        return dfsCost(node.left, depth + 1) + dfsCost(node.right, depth + 1);
    }
}
```

### Entropy and Huffman Coding

```java
public class EntropyBound {
    // Shannon's source coding theorem:
    // The optimal prefix code has average length H ≤ L < H + 1
    // where H = -∑ pᵢ·log₂(pᵢ) is the entropy of the distribution.
    //
    // Huffman coding achieves L < H + 1 for any distribution.
    //
    // For a block of n symbols, Huffman can achieve L → H as n → ∞
    // (but practically, n=1 block is usually used)
    
    public static double entropy(double[] probabilities) {
        double h = 0;
        for (double p : probabilities) {
            if (p > 0) h -= p * (Math.log(p) / Math.log(2));
        }
        return h;
    }
    
    public static double averageCodeLength(HuffmanNode root) {
        int[] sum = new int[2]; // [total cost, leaf count]
        computeAverage(root, 0, sum);
        return (double) sum[0] / sum[1];
    }
    
    private static void computeAverage(HuffmanNode node, int depth, int[] sum) {
        if (node.left == null && node.right == null) {
            sum[0] += depth;
            sum[1]++;
            return;
        }
        computeAverage(node.left, depth + 1, sum);
        computeAverage(node.right, depth + 1, sum);
    }
}
```

## 6. Greedy Approximation Guarantees

When greedy doesn't give optimal, it often guarantees an approximation ratio:

```java
public class GreedyApproximation {
    // 1. Set Cover: Greedy gives O(log n) approximation
    // Pick set covering most uncovered elements
    // Theorem: No polynomial algorithm achieves better than O(log n)
    // unless P = NP
    
    // 2. Vertex Cover: Greedy gives 2-approximation
    // Pick edge, add both endpoints, remove incident edges
    // But greedy by degree: O(log n) approximation
    
    // 3. k-Center: Greedy gives 2-approximation
    // Pick furthest point from current centers repeatedly
    
    // 4. Maximum Cut: Greedy gives 1/2-approximation
    // Random assignment gives at least 50% of edges crossing
    
    // 5. Knapsack: Greedy by ratio gives 1/2-approximation
    // Max of: greedy result, or best single item ∈ OPT
    
    public static double knapsackApproximation(int[] weights, int[] values, int capacity) {
        // Greedy by ratio
        int n = weights.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(
            (double) values[b] / weights[b],
            (double) values[a] / weights[a]));
        
        int greedyValue = 0, greedyWeight = 0;
        for (int i : idx) {
            if (greedyWeight + weights[i] <= capacity) {
                greedyWeight += weights[i];
                greedyValue += values[i];
            }
        }
        
        // Best single item
        int bestSingle = 0;
        for (int i = 0; i < n; i++) {
            if (weights[i] <= capacity) {
                bestSingle = Math.max(bestSingle, values[i]);
            }
        }
        
        return Math.max(greedyValue, bestSingle);
    }
    // This gives 1/2-approximation
}
```

## 7. Testing Greedy for Correctness

```java
public class GreedyCorrectnessTest {
    // Systematic approach:
    // 1. Check matroid structure
    // 2. Attempt exchange argument
    // 3. Test with brute force on small cases
    // 4. Check for counterexamples
    
    public static boolean testGreedy(int[] testData, 
                                       Function<int[], Integer> greedy,
                                       Function<int[], Integer> optimal) {
        for (int n = 1; n <= 10; n++) {
            // Generate all possible inputs of size n
            List<int[]> inputs = generateInputs(n);
            for (int[] input : inputs) {
                int g = greedy.apply(input);
                int o = optimal.apply(input);
                if (g != o) {
                    System.out.println("GREEDY FAILS: " + Arrays.toString(input));
                    System.out.println("  Greedy: " + g + ", Optimal: " + o);
                    return false;
                }
            }
        }
        return true;
    }
    
    private static List<int[]> generateInputs(int n) {
        // Generate test cases of size n with values 1..5
        List<int[]> result = new ArrayList<>();
        generateRecursive(result, new int[n], 0);
        return result;
    }
    
    private static void generateRecursive(List<int[]> result, int[] curr, int pos) {
        if (pos == curr.length) {
            result.add(curr.clone());
            return;
        }
        for (int v = 1; v <= 5; v++) {
            curr[pos] = v;
            generateRecursive(result, curr, pos + 1);
        }
    }
}
```

The exchange argument method is the cornerstone of proving greedy correctness. When you can't construct an exchange argument, the problem likely requires DP instead.
