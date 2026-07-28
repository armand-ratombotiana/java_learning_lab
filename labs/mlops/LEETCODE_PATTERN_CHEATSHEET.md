# LeetCode Pattern Cheatsheet for MLOps Engineers

## Essential Data Structures & Algorithms

### 1. Arrays & Hashing
- **Prefix Sum**: Range sum queries (ML feature aggregation)
- **Sliding Window**: Time-series feature windows
- **HashMap Counter**: Feature distribution, categorical encoding

### 2. Two Pointers
- **Sorted arrays**: Merge sorted model predictions
- **In-place deduplication**: Remove redundant features

### 3. Sliding Window
- **Fixed/variable window**: Rolling statistics (mean, stddev)
- **Deque**: Sliding window max/min for anomaly detection

### 4. Binary Search
- **Threshold tuning**: Find optimal decision boundary
- **Hyperparameter search**: Learning rate, regularization strength

### 5. Trees / Graphs
- **DFS/BFS**: Dependency resolution in DAG pipelines
- **Topological Sort**: Task ordering in Airflow/Pipeline DAGs
- **Trie**: Auto-complete features, hierarchical feature groups

### 6. Heap / Priority Queue
- **Top-K**: Feature importance, nearest neighbors
- **Median maintenance**: Streaming data statistics

### 7. Dynamic Programming
- **Sequence alignment**: Time-series alignment
- **Knapsack**: Resource allocation in pipeline scheduling

### 8. Union-Find
- **Connected components**: Cluster evaluation, community detection

### 9. Math & Statistics
- **Probability**: Bayesian updates, MAB (Thompson sampling)
- **Linear Algebra**: Matrix multiplication, dot products
- **Statistics**: Mean, variance, covariance, p-value computation

## ML-Specific Algorithm Patterns

| LeetCode Problem | MLOps Application |
|-----------------|-------------------|
| Two Sum | Feature interaction detection |
| Merge Intervals | Time-series feature alignment |
| Top K Frequent | Feature importance ranking |
| LRU Cache | Feature cache eviction policy |
| Design HashMap | Feature store implementation |
| Minimum Window Substring | Optimal feature window selection |
| Number of Islands | Cluster detection in embeddings |
| Task Scheduler | DAG task scheduling optimization |
| Find Median from Data Stream | Online statistics computation |
| Time-Based Key-Value Store | Feature timestamp retrieval |

## Time Complexity Reference
- O(1): Hash lookups, array indexing
- O(log n): Binary search, balanced tree operations
- O(n): Linear scan, hash join
- O(n log n): Sorting, tree-based algorithms
- O(n²): Pairwise feature interactions (small n)
