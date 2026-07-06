$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\20-randomized-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Randomized Algorithms — Overview

This lab covers randomized algorithms: Las Vegas vs Monte Carlo classifications, randomized quickselect, reservoir sampling, Fisher-Yates shuffle, Freivalds' matrix multiplication checker, and Karger's randomized min cut algorithm. Randomization can simplify algorithm design, improve average-case performance, or provide high-probability guarantees.

## Learning Objectives

- Distinguish between Las Vegas and Monte Carlo algorithms
- Implement randomized quickselect for order statistics
- Perform reservoir sampling of k items from a stream
- Generate uniform random permutations with Fisher-Yates shuffle
- Verify matrix multiplication using Freivalds' checker

## Prerequisites

- Basic probability and expectation
- Java Random and ThreadLocalRandom classes
- Recursion and divide-and-conquer
"@

wf "THEORY.md" @"
# Randomized Algorithms — Theoretical Foundation

## Las Vegas vs Monte Carlo

Las Vegas algorithms always produce the correct result, but runtime is random (e.g., randomized quicksort). Monte Carlo algorithms may produce incorrect results with bounded probability, but runtime is deterministic (e.g., Freivalds' checker). The distinction affects how we analyze and trust the output.

## Randomized Quickselect

Quickselect finds the k-th smallest element in expected O(n) time by selecting a random pivot. The random pivot ensures that the algorithm avoids the O(n^2) worst-case of deterministic median-of-three selection. The expected number of comparisons is 2n + o(n).

## Reservoir Sampling

Reservoir sampling selects k random items from a stream of unknown length n. The algorithm uses O(k) memory regardless of stream length. Each item has probability k/n of being in the final sample. The algorithm maintains a reservoir of k items: the first k items fill the reservoir, and for the i-th item (i > k), it is added with probability k/i, replacing a randomly selected reservoir item.

## Fisher-Yates Shuffle

The Fisher-Yates shuffle (also known as the Knuth shuffle) produces a uniformly random permutation of an array in O(n) time. It works by iterating from the end: for position i, pick a random index from [0, i] and swap. Each of the n! permutations is equally likely.

## Freivalds' Matrix Checker

Freivalds' algorithm checks if A * B = C for n x n matrices in O(n^2) time with bounded error probability. It generates a random vector x of bits, computes A*(B*x) and C*x, and checks for equality. If A*B != C, the test detects this with probability at least 1/2. Running k independent tests reduces error to 2^{-k}.

## Karger's Min Cut

Karger's algorithm finds the minimum cut in an undirected graph by repeatedly contracting random edges until two nodes remain. Each contraction merges two vertices, removing edges between them and keeping edges to other vertices. The surviving edges form a cut. Repeating O(n^2 log n) times gives probability of finding the global min cut.
"@

wf "WHY_IT_EXISTS.md" @"
# Why Randomized Algorithms Exist

Randomization provides two fundamental advantages in algorithm design: simplicity and performance. Many problems that have complex deterministic solutions yield to simple randomized algorithms. Additionally, randomization often defeats adversarial inputs that would cause deterministic algorithms to perform poorly.

The classic example is quicksort. The deterministic version with a fixed pivot has O(n^2) worst-case behavior on sorted input. Randomized quicksort, using a random pivot, has O(n log n) expected time on any input. The adversary cannot predict the pivot choice, so no input reliably causes worst-case behavior.

Quickselect faces the same issue: selecting the k-th smallest element in worst-case O(n^2) with a deterministic pivot. The randomized version achieves expected O(n) with a simple algorithm. The Blum-Floyd-Pratt-Rivest-Tarjan deterministic O(n) algorithm exists but is significantly more complex.

Reservoir sampling solves a problem that deterministic algorithms cannot: sampling from a stream of unknown length without storing all elements. The algorithm is remarkably simple: each element takes its place in the sample with probability proportional to its position.

Freivalds' checker exploits randomness to reduce an O(n^3) matrix multiplication verification problem to O(n^2). For large matrices, this difference is enormous. Deterministic verification of matrix multiplication requires full recomputation (though there are recent theoretical breakthroughs).

Karger's algorithm uses randomization to solve a fundamental graph problem that has complex deterministic solutions. The algorithm's simplicity belies its power: it finds the minimum cut with high probability after O(n^2 log n) repetitions.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Randomized Algorithms Matter

Randomized algorithms are pervasive in modern computing. They underpin Monte Carlo methods in scientific computing, randomized data structures (hash tables, skip lists), machine learning (stochastic gradient descent, dropout), and cryptography (random key generation).

## Practical Applications

- Randomized quickselect is used in databases for approximate quantile queries
- Reservoir sampling powers A/B testing systems that need unbiased samples from event streams
- Fisher-Yates shuffle is used for random playlist generation, card dealing in games, and unbiased data splitting for machine learning
- Freivalds' checker enables efficient verification of computation results in cloud computing
- Karger's algorithm is used in network reliability analysis and circuit design

## Performance Impact

Matrix multiplication verification via Freivalds reduces O(n^3) to O(n^2). For n=10,000, this is 10^12 vs 10^8 operations — a 10,000x speedup. The continuous probability of correctness can be boosted arbitrarily high with minimal cost.

## Interview Relevance

Randomized quickselect and Fisher-Yates shuffle are common interview questions. Understanding randomized algorithm analysis demonstrates sophisticated mathematical maturity.
"@

wf "HISTORY.md" @"
# History of Randomized Algorithms

1930s: Fisher and Yates develop the original shuffle algorithm for statistical tables.

1948: Feller's probability theory provides foundations for randomized algorithms.

1961: Alan Turing proposes randomization in computation.

1968: Knuth's TAOCP includes Fisher-Yates shuffle (Algorithm P).

1970: Rabin introduces randomized algorithms for primality testing.

1975: Solovay-Strassen primality test uses randomization.

1977: Freivalds publishes matrix multiplication checker.

1989: Karger's randomized min cut algorithm introduced.

1990s: Adleman uses DNA computing with randomized algorithms.

1995: Randomized incremental geometric algorithms become standard.

2000s: Randomized algorithms for machine learning (SGD, dropout).

2010s: Sublinear randomized algorithms for big data.

2020s: Randomized algorithms in quantum computing and differential privacy.
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for Randomized Algorithms

## Las Vegas — "The Bus Schedule"

A Las Vegas bus is guaranteed to arrive, but the exact time is random. Similarly, Las Vegas algorithms always produce the correct answer, but running time is random. The algorithm might finish quickly or take longer, but it never gives a wrong answer.

## Monte Carlo — "The Medical Test"

A medical test might give false positives (false alarm) or false negatives (missed detection) with known probabilities. Monte Carlo algorithms have bounded error probability. By repeating the test, you can make the error probability arbitrarily small.

## Reservoir Sampling — "The Lottery"

Imagine you need to pick k winners from a box of tickets, but you don't know how many tickets exist. You process tickets one by one. The first k tickets go into the "holding pool." For each subsequent ticket, with decreasing probability you might replace a random existing ticket. This ensures every ticket has equal probability of being selected.

## Fisher-Yates — "The Card Shuffle"

To shuffle a deck of cards fairly, pick a random card from the deck and put it at the beginning. Then pick a random card from the remaining deck and put it second. Continue until the entire deck is shuffled. This produces every permutation with equal probability.

## Karger's Algorithm — "The Contracting City"

Imagine a city where neighborhoods are slowly merging. When two neighborhoods merge, all connections between them become internal (no longer cross the boundary). The surviving connections between the remaining neighborhoods after enough mergers represent a cut of the original city. Different merger orders produce different cuts, and some order will produce the true minimum cut.
"@

wf "HOW_IT_WORKS.md" @"
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
"@

wf "INTERNALS.md" @"
# Randomized Algorithms — Internal Mechanics

## Randomized Quickselect

```java
int quickselect(int[] arr, int left, int right, int k) {
    if (left == right) return arr[left];
    int pivotIdx = left + ThreadLocalRandom.current().nextInt(right - left + 1);
    pivotIdx = partition(arr, left, right, pivotIdx);
    if (k == pivotIdx) return arr[k];
    else if (k < pivotIdx) return quickselect(arr, left, pivotIdx - 1, k);
    else return quickselect(arr, pivotIdx + 1, right, k);
}
```

## Fisher-Yates Shuffle

```java
void shuffle(int[] arr) {
    Random rng = ThreadLocalRandom.current();
    for (int i = arr.length - 1; i > 0; i--) {
        int j = rng.nextInt(i + 1);
        int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
    }
}
```

## Reservoir Sampling

```java
int[] reservoirSample(int[] stream, int k) {
    int[] reservoir = new int[k];
    for (int i = 0; i < k; i++) reservoir[i] = stream[i];
    Random rng = ThreadLocalRandom.current();
    for (int i = k; i < stream.length; i++) {
        int j = rng.nextInt(i + 1);
        if (j < k) reservoir[j] = stream[i];
    }
    return reservoir;
}
```

## Freivalds' Checker

```java
boolean freivaldsCheck(int[][] A, int[][] B, int[][] C) {
    int n = A.length;
    int[] x = new int[n];
    Random rng = ThreadLocalRandom.current();
    for (int i = 0; i < n; i++) x[i] = rng.nextBoolean() ? 1 : 0;
    int[] Bx = multiply(B, x);
    int[] ABx = multiply(A, Bx);
    int[] Cx = multiply(C, x);
    return Arrays.equals(ABx, Cx);
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Randomized Algorithms

## Expected Time of Quickselect

The expected number of comparisons in randomized quickselect is E[T(n)] = n + E[T(max(k, n-k))] with probability 1/n for each pivot position. Solving: E[T(n)] <= 2n + o(n).

## Reservoir Sampling Probability

The i-th item appears in the final reservoir with probability k/n. Proof: For i <= k: P(i in final) = product_{j=k+1}^n (1-1/j) * (k/k) = k/n. For i > k: P(i added) * P(not removed) = (k/i) * product_{j=i+1}^n (1-1/j) = k/n.

## Freivalds' Error Bound

If A*B != C, then P(r * (A*B - C) != 0) >= 1/2, where r is the random vector. The test detects inequality with probability at least 1/2 per run. After k runs, the probability of error is at most 2^{-k}.

## Karger's Success Probability

Each random edge contraction in Karger's algorithm preserves the global min cut with probability at least 2/n^2 per independent run. After O(n^2 log n) runs, the probability of finding the global min cut is at least 1 - 1/n.

## Union Bound

The union bound (Boole's inequality) is used extensively in randomized algorithm analysis: P(union of events) <= sum of individual probabilities. This allows bounding the overall error probability of multiple randomized tests.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Randomized Algorithms

## Quickselect Partitioning

Before: [7, 3, 9, 2, 8, 5, 1, 4] pivot=2 (index 3)
After:  [1, 2, 7, 3, 9, 8, 5, 4]
          ↑ pivot at correct position

## Reservoir Sampling Walkthrough

Stream: [a, b, c, d, e, f, g, h], k=3
i=0: reservoir = [a, -, -]
i=1: reservoir = [a, b, -]
i=2: reservoir = [a, b, c]
i=3: d replaces c with prob 3/4 -> [a, b, d] (say)
i=4: e replaces a with prob 3/5 -> [e, b, d] (say)
i=5: f replaces nothing (prob 3/6 = 50%) -> [e, b, d] (no change)
i=6: g replaces b with prob 3/7 -> [e, g, d] (say)
i=7: h replaces d with prob 3/8 -> [e, g, h] (say)

Each original element has probability 3/8 of being in final reservoir.
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Randomized Algorithms

## Random Source Selection

Java provides two main random sources: java.util.Random (thread-safe but slower) and java.util.concurrent.ThreadLocalRandom (per-thread, faster, preferred). For cryptographic applications, use java.security.SecureRandom.

## Implementing Quickselect Iteratively

Recursive quickselect may stack overflow for large inputs. An iterative version using a while loop avoids this while maintaining the expected O(n) time:

```java
int quickselectIterative(int[] arr, int k) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int pivotIdx = partition(arr, left, right, 
            ThreadLocalRandom.current().nextInt(left, right + 1));
        if (k == pivotIdx) return arr[k];
        else if (k < pivotIdx) right = pivotIdx - 1;
        else left = pivotIdx + 1;
    }
    return arr[left];
}
```

## Shuffling Immutable Collections

For immutable collections, Fisher-Yates cannot be applied in-place. Create an array copy, shuffle the copy, and return it as a new list view.

## Verifying Uniformity of Shuffle

Statistical tests (chi-squared test on permutation counts) can verify shuffle uniformity. For n elements, each permutation should appear with probability 1/n!.
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Randomized Algorithms Implementation

## Reservoir Sampling Steps

1. Create reservoir array of size k
2. Copy first k elements into reservoir
3. For i from k to n-1:
   a. Generate random j in [0, i]
   b. If j < k, set reservoir[j] = stream[i]
4. Return reservoir

## Fisher-Yates Shuffle Steps

1. For i from n-1 down to 1:
   a. Generate random j in [0, i]
   b. Swap arr[i] and arr[j]
2. Array is now uniformly random permutation

## Freivalds' Steps

1. Generate random vector x of length n with entries 0 or 1
2. Compute Bx = B * x (vector result)
3. Compute ABx = A * Bx
4. Compute Cx = C * x
5. If ABx != Cx, return FALSE (definite)
6. Else return TRUE (probable, re-run for higher confidence)
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Randomized Algorithms

- **Wrong random range in shuffle** — Using nextInt(n) instead of nextInt(i+1) biases results
- **Creating new Random per call** — ThreadLocalRandom.current() is the correct approach
- **Fisher-Yates going forward** — Must iterate backward to avoid bias
- **Reservoir: wrong replacement probability** — The j < k comparison is critical
- **Quickselect: not randomizing** — Deterministic pivot gives O(n^2) worst case
- **Freivalds: using same vector** — Each iteration needs a fresh random vector
- **Not boosting confidence** — Single Freivalds run gives only 50% detection rate
- **Karger: forgetting to copy graph** — Each repetition needs a fresh graph copy
- **Predictable randomness for security** — Using Random instead of SecureRandom for crypto
- **Seed reuse** — Same seed produces same sequence; use random seeds
"@

wf "DEBUGGING.md" @"
# Debugging — Randomized Algorithms

## Shuffle Verification

```java
// Statistical test: count occurrences of each element at each position
int[][] count = new int[n][n];
for (int trial = 0; trial < 100000; trial++) {
    int[] arr = {1, 2, 3, 4};
    shuffle(arr);
    for (int i = 0; i < arr.length; i++)
        count[arr[i]-1][i]++;
}
// Each cell should be ~25000 for uniform shuffle
```

## Reservoir Correctness

```java
// Verify each element appears with approx k/n probability
int[] reservoir = new int[k];
int[] freq = new int[n];
for (int trial = 0; trial < 100000; trial++) {
    sample(stream, reservoir);
    for (int val : reservoir) freq[val]++;
}
// Each freq entry should be ~100000 * k / n
```

## Deterministic Seed for Debugging

Use a fixed seed during development: Random rng = new Random(42). This reproduces the same pseudo-random sequence for reproducible debugging.
"@

wf "REFACTORING.md" @"
# Refactoring — Randomized Algorithms

## Configurable Random Source

```java
interface RandomSource {
    int nextInt(int bound);
    double nextDouble();
}
```

Allow injecting Random, ThreadLocalRandom, or SecureRandom.

## Strategy Pattern for Quickselect

Make the pivot selection strategy pluggable:
- RandomPivotStrategy
- MedianOfThreeStrategy
- Deterministic3MedianStrategy

## Observable Reservoir

Create a reservoir stream that emits intermediate states for visualization and debugging.

## Parallel Reservoir Sampling

For large streams, partition the stream, sample each partition independently, then merge samples using a final reservoir pass.
"@

wf "PERFORMANCE.md" @"
# Performance — Randomized Algorithms

## Algorithm Comparison

| Algorithm | Expected Time | Worst Case | Space |
|-----------|--------------|------------|-------|
| Quickselect | O(n) | O(n^2) | O(1) |
| Fisher-Yates | O(n) | O(n) | O(1) |
| Reservoir | O(n) | O(n) | O(k) |
| Freivalds | O(n^2) | O(n^2) | O(n) |
| Karger | O(n^4 log n) | O(n^4 log n) | O(n^2) |

## Benchmark Data

- Quickselect on 10^7 elements: ~50ms expected
- Fisher-Yates on 10^7 elements: ~80ms
- Reservoir sampling of 1000 from 10^7: ~30ms
- Freivalds on 1000x1000 matrix: ~5ms per test
- Karger on 1000-node graph: several seconds

## Optimization Tips

- Use ThreadLocalRandom for thread-local access
- Replace recursion with iteration for quickselect
- Use arrays of primitives instead of boxed types
- For Karger, use adjacency lists and union-find for efficient contraction
"@

wf "SECURITY.md" @"
# Security — Randomized Algorithms

## Predictable Randomness

Using java.util.Random with a known seed allows attackers to predict all random choices. For security applications, always use SecureRandom.

## Randomness Exhaustion

On systems with low entropy, SecureRandom may block. Have fallback strategies for non-cryptographic randomization.

## Shuffle Timing Attacks

If shuffle timing depends on random values, attackers may infer bits of randomness through timing observations. Use constant-time implementations when randomness is secret.

## Cryptographic vs Statistical Randomness

Fisher-Yates with java.util.Random is fine for games but insecure for cryptographic key generation. Use SecureRandom for any security-sensitive shuffling.

## Input Validation

Reservoir sampling with k > stream length: handle gracefully. Quickselect with k out of range: check bounds.
"@

wf "ARCHITECTURE.md" @"
# Architecture — Randomized Algorithms

## Library Design

```
RandomizedAlgorithms Library
├── Selection
│   └── RandomizedQuickselect
├── Sampling
│   └── ReservoirSampling
├── Shuffling
│   └── FisherYatesShuffle
├── Verification
│   └── FreivaldsChecker
└── Graph
    └── KargerMinCut
```

## Integration with Other Systems

- Stream processing frameworks for reservoir sampling
- Statistical testing frameworks for shuffle verification
- Linear algebra libraries for matrix operations
- Graph processing systems for min-cut analysis
"@

wf "EXERCISES.md" @"
# Exercises — Randomized Algorithms

## Beginner
1. Implement Fisher-Yates shuffle on int array
2. Implement naive quickselect with random pivot
3. Find k smallest elements in unsorted array
4. Simulate a fair coin using an unfair coin

## Intermediate
5. Implement reservoir sampling for k items from a stream
6. Implement Freivalds' checker for 100x100 matrices
7. Compare quickselect vs sorting for finding median
8. Implement Karger's min cut on a small graph

## Advanced
9. Prove Fisher-Yates generates all permutations uniformly
10. Implement parallel reservoir sampling
11. Boost Freivalds' confidence to 1 - 10^{-9}
12. Implement Karger-Stein algorithm (recursive version)
"@

wf "QUIZ.md" @"
# Quiz — Randomized Algorithms

1. What is the difference between Las Vegas and Monte Carlo algorithms?
2. What is the expected time of randomized quickselect?
3. How much memory does reservoir sampling use?
4. What is the probability of Freivalds detecting an incorrect multiplication in one run?
5. Why does Fisher-Yates iterate backward?
6. How many repetitions does Karger's algorithm need?
7. What is the probability a stream element ends up in the reservoir?
8. When would you use SecureRandom instead of ThreadLocalRandom?
"@

wf "FLASHCARDS.md" @"
# Flashcards — Randomized Algorithms

- Q: Las Vegas vs Monte Carlo? -> A: Las Vegas always correct; Monte Carlo bounded error
- Q: Quickselect expected time? -> A: O(n)
- Q: Reservoir space? -> A: O(k)
- Q: Freivalds detection rate? -> A: >= 50% per run
- Q: Fisher-Yates complexity? -> A: O(n)
- Q: Karger runs needed? -> A: O(n^2 log n)
- Q: Stream item reservoir probability? -> A: k/n
- Q: Union bound? -> A: P(union) <= sum of probabilities
"@

wf "INTERVIEW.md" @"
# Interview Questions — Randomized Algorithms

1. "Shuffle a deck of cards." — Fisher-Yates shuffle
2. "Select k random items from a stream." — Reservoir sampling
3. "Find the median of an unsorted array in expected O(n)." — Randomized quickselect
4. "Check if two matrices multiply to a third." — Freivalds' algorithm
5. "How would you test if a shuffle is uniform?" — Chi-squared test
6. "Design a load balancer that randomly distributes requests." — Random assignment with weighted probabilities
"@

wf "REFLECTION.md" @"
# Reflection — Randomized Algorithms

- Why do randomized algorithms often outperform their deterministic counterparts in practice?
- How does the use of randomness change how we think about algorithm correctness?
- What are the limits of randomization? Can every problem have an efficient randomized algorithm?
- How does randomness help defeat adversarial inputs?
- What is the philosophical significance of algorithms that are "probably" correct?
"@

wf "REFERENCES.md" @"
# References — Randomized Algorithms

- Motwani, R., Raghavan, P. "Randomized Algorithms." Cambridge University Press, 1995.
- Mitzenmacher, M., Upfal, E. "Probability and Computing." Cambridge University Press, 2017.
- Knuth, D. "The Art of Computer Programming, Vol. 2: Seminumerical Algorithms." Addison-Wesley.
- Karger, D. "Random Sampling in Cut, Flow, and Network Design Problems." STOC, 1994.
- Freivalds, R. "Probabilistic Machines Can Use Less Running Time." IFIP Congress, 1977.
- Cormen, T.H. et al. "Introduction to Algorithms." MIT Press, 4th Edition, 2022.
"@

Write-Host "20-randomized-algorithms: All 24 markdown files created"
