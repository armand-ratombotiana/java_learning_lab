# LeetCode Pattern Cheatsheet for Data Science Interviews

## Two Pointers
- **When**: Sorted arrays, sliding window, in-place partitioning
- **DS applications**: Comparing distributions, merging sorted data streams
- **Example**: Two-sum in sorted array, container with most water

## Sliding Window
- **When**: Subarray/substring problems, time-series rolling statistics
- **DS applications**: Moving averages, rolling variance, anomaly detection windows
- **Example**: Longest substring without repeating characters

## Binary Search
- **When**: Sorted data, searching for thresholds, finding quantiles
- **DS applications**: Finding optimal p-value thresholds, percentile computation
- **Example**: Search in rotated sorted array, find peak element

## BFS/DFS (Graph Traversal)
- **When**: Tree/graph problems, dependency resolution, causal graphs
- **DS applications**: DAG traversal for causal inference, decision tree paths
- **Example**: Number of islands, course schedule

## Dynamic Programming
- **When**: Optimal substructure, overlapping subproblems, sequence modeling
- **DS applications**: Hidden Markov models, dynamic time warping, optimal experiment design
- **Example**: Longest increasing subsequence, knapSack

## Backtracking
- **When**: Combinatorial search, constraint satisfaction, feature subset selection
- **DS applications**: Feature selection via subset search, hyperparameter grid search
- **Example**: N-queens, permutation generation

## Divide and Conquer
- **When**: Recursive partitioning, merge operations, ensemble methods
- **DS applications**: Decision trees, random forest construction, merge sort for streaming data
- **Example**: Merge sort, quickselect

## Heap / Priority Queue
- **When**: Top-k elements, median maintenance, stream processing
- **DS applications**: Top-k features, streaming quantiles, outlier detection
- **Example**: Find median from data stream, top k frequent elements

## Union-Find
- **When**: Disjoint sets, connectivity, clustering
- **DS applications**: Hierarchical clustering, connected component analysis
- **Example**: Number of connected components in graph

## Trie
- **When**: Prefix matching, string search, autocomplete
- **DS applications**: Feature namespace lookups, categorical encoding dictionaries
- **Example**: Implement trie, word search II

## Data Science-Specific Patterns

| Pattern | When to Use | Typical Problem |
|---------|-------------|-----------------|
| Bootstrap Resampling | Estimate uncertainty without parametric assumptions | Confidence interval for median |
| Cross-Validation Split | Evaluate model generalization | K-fold stratified split |
| Feature Hashing | High-cardinality categorical features | One-hot encoding at scale |
| Reservoir Sampling | Streaming data uniform sampling | Random sample from data stream |
| Bloom Filter | Set membership at scale | Dedup already-seen records |
| Count-Min Sketch | Frequency estimation in streams | Top-k frequent items |
