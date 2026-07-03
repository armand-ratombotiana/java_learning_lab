$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\06-greedy-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Greedy Algorithms — Overview

Covers greedy choice property, matroid theory, and classic greedy problems.

## Learning Objectives
- Understand greedy choice property and when greedy works
- Implement classic greedy algorithms in Java
- Prove optimality using exchange arguments
- Compare greedy vs DP approaches

## Prerequisites
- Basic algorithm analysis
- Understanding of DP helpful
- Graph basics for some problems

## Estimated Time
- **Total**: 3–4 hours
"@

wf "THEORY.md" @"
# Greedy Algorithms — Theoretical Foundation

## Greedy Choice Property
A globally optimal solution can be reached by making locally optimal choices.

## Optimal Substructure
Optimal solution contains optimal solutions to subproblems.

## Matroid Theory
Greedy works optimally on matroid-structured problems. A matroid (E, I):
- Empty set is independent
- Subsets of independent sets are independent
- Augmentation property

## Classic Greedy Algorithms
- Activity Selection: Earliest finish time
- Huffman Coding: Merge least frequent
- Dijkstra: Shortest path
- Prim/Kruskal: MST
- Fractional Knapsack: Highest value/weight ratio
"@

wf "WHY_IT_EXISTS.md" @"
# Why Greedy Algorithms Exist

Greedy algorithms provide simple, efficient solutions to optimization problems. While DP solves broader classes, greedy is often O(n log n) — significantly faster. Matroid theory (Whitney, 1935) characterizes when greedy approaches are optimal.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Greedy Matters

- Efficiency: Typically O(n log n) or O(n)
- Simplicity: Shorter and more intuitive than DP
- Network Protocols: Routing uses greedy shortest-path
- Data Compression: Huffman coding is greedy and optimal
- Approximation: Bounded approximations for NP-hard problems
- Interview favorite: Tests insight and proof ability
"@

wf "HISTORY.md" @"
# History of Greedy

- 1935: Whitney introduced matroids
- 1956: Dijkstra's algorithm
- 1956: Kruskal's MST
- 1957: Prim's MST
- 1952: Huffman coding (term paper by David Huffman)
- 1971: Edmonds proved greedy works on matroids
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Coin Changer
Make change with US coins: always take largest denomination that doesn't exceed remaining amount.

## Conference Room
Given activities, always pick the one that finishes earliest — leaves most room for remaining.

## Thief at Market
For fractional goods, take most expensive per gram first. For indivisible goods, greedy fails (0/1 knapsack needs DP).
"@

wf "HOW_IT_WORKS.md" @"
# How Greedy Works

## Activity Selection
```java
List<Activity> select(List<Activity> activities) {
    activities.sort(Comparator.comparing(Activity::end));
    List<Activity> selected = new ArrayList<>();
    int lastEnd = Integer.MIN_VALUE;
    for (Activity a : activities) {
        if (a.start >= lastEnd) {
            selected.add(a);
            lastEnd = a.end;
        }
    }
    return selected;
}
```

## Fractional Knapsack
```java
double fractionalKnapsack(Item[] items, int capacity) {
    Arrays.sort(items, (a, b) -> 
        Double.compare(b.value/b.weight, a.value/a.weight));
    double totalValue = 0;
    for (Item item : items) {
        if (capacity >= item.weight) {
            totalValue += item.value;
            capacity -= item.weight;
        } else {
            totalValue += item.value * ((double) capacity / item.weight);
            break;
        }
    }
    return totalValue;
}
```
"@

wf "INTERNALS.md" @"
# Greedy — Internal Mechanics

## Huffman Coding
```java
class HuffmanNode implements Comparable<HuffmanNode> {
    char ch; int freq; HuffmanNode left, right;
    public int compareTo(HuffmanNode o) { return this.freq - o.freq; }
}

HuffmanNode buildTree(Map<Character, Integer> freqMap) {
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
    freqMap.forEach((ch, f) -> pq.add(new HuffmanNode(ch, f)));
    while (pq.size() > 1) {
        HuffmanNode left = pq.poll(), right = pq.poll();
        HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq);
        parent.left = left; parent.right = right;
        pq.add(parent);
    }
    return pq.poll();
}

void buildCodes(HuffmanNode node, String code, Map<Character, String> codes) {
    if (node.left == null && node.right == null) {
        codes.put(node.ch, code);
        return;
    }
    buildCodes(node.left, code + "0", codes);
    buildCodes(node.right, code + "1", codes);
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Greedy

## Exchange Argument
Prove greedy optimality: Show any optimal solution can be transformed into greedy solution without decreasing optimality.

Example for Activity Selection:
1. Greedy picks A (earliest finish)
2. In optimal, let B be first activity
3. If B ≠ A, replace B with A (A finishes no later than B)
4. Result still has room for remaining activities
5. By induction, greedy is optimal

## Matroid Definition
M = (E, I): E is finite set, I ⊆ 2ᴱ (independent sets)
- Ø ∈ I
- Hereditary: A ∈ I, B ⊆ A → B ∈ I
- Augmentation: A,B ∈ I, |A| < |B| → ∃e ∈ B\A: A∪{e} ∈ I
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Greedy

## Activity Selection
```
A(1,4) B(3,5) C(0,6) D(5,7) E(3,8) F(5,9) G(6,10) H(8,11)
Sorted by finish:
A[1-4] → select
D[5-7] → select
H[8-11] → select
Result: {A, D, H}
```

## Huffman Tree
a=5, b=9, c=12, d=13, e=16, f=45
Step 1: a+b=14
Step 2: c+d=25
Step 3: 14+e=30
Step 4: 25+30=55
Step 5: f+55=100

Result: f:0, a:1000, b:1001, c:101, d:110, e:111
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Greedy

## Dijkstra's Algorithm
```java
public int[] dijkstra(Map<Integer, List<Edge>> graph, int n, int source) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;
    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.offer(new Node(source, 0));
    while (!pq.isEmpty()) {
        Node curr = pq.poll();
        if (curr.dist > dist[curr.id]) continue;
        for (Edge e : graph.getOrDefault(curr.id, List.of())) {
            int newDist = curr.dist + e.weight;
            if (newDist < dist[e.to]) {
                dist[e.to] = newDist;
                pq.offer(new Node(e.to, newDist));
            }
        }
    }
    return dist;
}
```

## Kruskal's MST
```java
int kruskal(int n, List<Edge> edges) {
    Collections.sort(edges);
    UnionFind uf = new UnionFind(n);
    int totalWeight = 0;
    for (Edge e : edges) {
        if (uf.union(e.u, e.v)) totalWeight += e.weight;
    }
    return totalWeight;
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Greedy

## Solving Greedy Problems
1. Identify greedy choice: What local decision leads to global optimum?
2. Define sorting/priority: What order yields the greedy choice?
3. Make the choice: Select current best option
4. Update state: Reduce to smaller instance
5. Repeat until solved

## Proving Greedy Works
1. Show greedy choice property: First greedy choice is part of some optimal solution
2. Show optimal substructure: After greedy choice, remaining problem is equivalent to original on smaller input
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Assuming greedy always works — many problems need DP
- Wrong sorting criteria — activity selection by start time (wrong!)
- Forgetting edge cases — tie-breaking affects correctness
- Not proving optimality
- Applying greedy to 0/1 knapsack
- Dijkstra with negative edges — use Bellman-Ford
- Coin change for non-canonical systems
"@

wf "DEBUGGING.md" @"
# Debugging — Greedy

## Counterexample Search
```java
public static <T> boolean verifyGreedy(List<T> input) {
    T greedyResult = greedySolve(input);
    T optimalResult = bruteForceSolve(input);
    return compare(greedyResult, optimalResult);
}
```

## Random Testing
```java
Random rand = new Random(42);
for (int trial = 0; trial < 1000; trial++) {
    List<Item> items = generateRandomItems(rand);
    double greedy = fractionalKnapsack(items, capacity);
    double optimal = dpKnapsack(items, capacity);
    assert Math.abs(greedy - optimal) < 1e-9;
}
```
"@

wf "REFACTORING.md" @"
# Refactoring — Greedy

## Configurable Greedy
```java
public class GreedySolver<T> {
    private final Comparator<T> greedyComparator;
    private final BiPredicate<T, State> canAdd;
    
    public List<T> solve(List<T> candidates) {
        candidates.sort(greedyComparator);
        List<T> result = new ArrayList<>();
        for (T item : candidates) {
            if (canAdd.test(item, currentState()))
                result.add(item);
        }
        return result;
    }
}
```
"@

wf "PERFORMANCE.md" @"
# Performance — Greedy

| Algorithm | Time | Space |
|-----------|------|-------|
| Activity Selection | O(n log n) | O(1) |
| Huffman Coding | O(n log n) | O(n) |
| Dijkstra | O((V+E) log V) | O(V) |
| Kruskal | O(E log E) | O(V) |
| Fractional Knapsack | O(n log n) | O(1) |

## Greedy vs DP
| Aspect | Greedy | DP |
|--------|--------|-----|
| Time | Usually O(n log n) | Often O(n²)+ |
| Memory | Minimal | Often O(n²) |
| Proof | Exchange argument | Induction |
| Applicability | Matroids | General |
"@

wf "SECURITY.md" @"
# Security — Greedy

- Manipulated sorting: Comparator could be subverted
- Resource allocation: Multi-tenant systems can be exploited
- Priority queue exhaustion: Infinite loops with comparator bugs
- Integer overflow: Weight/value comparisons using subtraction
"@

wf "ARCHITECTURE.md" @"
# Architecture — Greedy

## Java Standard Library
- PriorityQueue: Foundation for Dijkstra, Prim, Huffman
- Arrays.sort(): Key step in most greedy algorithms
- Comparator: Enables flexible greedy ordering

## Real-World Systems
- TCP congestion control: Greedy bandwidth estimation
- OS scheduling: Greedy heuristics
- Network routing: OSPF uses Dijkstra
- Data compression: ZIP uses Huffman
"@

wf "EXERCISES.md" @"
# Exercises — Greedy

## Beginner
1. Activity Selection — implement and count
2. Assign cookies to children
3. Lemonade change (bills: 5, 10, 20)
4. Maximum units on a truck

## Intermediate
5. Fractional Knapsack
6. Huffman Coding — build tree and codes
7. Minimum platforms for trains
8. Job sequencing with deadlines

## Advanced
9. Prove Activity Selection optimality
10. Implement Dijkstra and Prim
11. Kruskal with Union-Find
12. Greedy set cover (approximation)
"@

wf "QUIZ.md" @"
# Quiz — Greedy

1. What is the greedy choice property?
2. Example where greedy fails but DP works?
3. Why does Dijkstra fail with negative edges?
4. How does Huffman coding achieve optimal compression?
5. What is an exchange argument?
6. What is a matroid?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Activity selection sort? → A: Earliest finish time
- Q: Dijkstra requirement? → A: Non-negative edge weights
- Q: Huffman coding time? → A: O(n log n)
- Q: Greedy vs DP? → A: Single irreversible choice per step
- Q: Fractional knapsack sort? → A: Value/weight ratio
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Activity Selection" — Classic
2. "Jump Game II" — Minimum jumps
3. "Non-overlapping Intervals" — Greedy by end time
4. "Gas Station" — Can complete circuit?
5. "Partition Labels" — String partitioning
6. "Candy Distribution" — Two passes
"@

wf "REFLECTION.md" @"
# Reflection

- How to decide if a problem yields to greedy?
- Relationship between greedy and matroids?
- Why is proving optimality harder than implementation?
- When prefer greedy approximation over exact DP?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapter 16
- Kleinberg & Tardos, Chapter 4
- Dasgupta et al., Greedy chapter
- Oxley, J. "Matroid Theory"
"@

Write-Host "06-greedy-algorithms: All 24 files created"
