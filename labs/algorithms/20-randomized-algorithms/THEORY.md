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
