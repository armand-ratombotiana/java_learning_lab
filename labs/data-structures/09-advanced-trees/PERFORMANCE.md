# Performance: Advanced Trees

## Time Complexity Comparison

| Operation | BST* | AVL | Red-Black | B-Tree | Fenwick | Segment |
|-----------|------|-----|-----------|--------|---------|---------|
| Search | O(log n) | O(log n) | O(log n) | O(log n) | O(1)** | O(log n) |
| Insert | O(log n) | O(log n) | O(log n) | O(log n) | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) | O(log n) | O(log n) | O(log n) | O(log n) |
| Range query | O(k+log n) | O(k+log n) | O(k+log n) | O(k+log n) | O(log n) | O(log n) |
| Range update | O(k+log n) | O(k+log n) | O(k+log n) | O(k+log n) | O(n) | O(log n) |

\*Average case; worst O(n)
\*\*Point query (prefix sum)

## Rotation Cost

| Tree | Rotations per Insert | Rotations per Delete |
|------|---------------------|---------------------|
| AVL | 0-2 (O(1)) | 0-2 (O(log n) worst) |
| Red-Black | 0-2 (O(1)) | 0-3 (O(1)) |
| B-Tree | Split (O(t)) | Merge (O(t)) |

## Space Complexity

| Tree | Memory per Element | Notes |
|------|-------------------|-------|
| AVL | ~40 bytes | key + left + right + height |
| Red-Black | ~44 bytes | key + left + right + parent + color |
| B-Tree | ~key_size × (2t-1) + pointer × 2t | Amortized over many keys |
| Fenwick | 4n bytes | Single array |
| Segment | 16n bytes | 4 × array size |

## Practical Performance (1M operations)

| Structure | Inserts | Searches | Range Queries |
|-----------|---------|----------|--------------|
| TreeMap | ~0.8s | ~0.6s | ~2s (iterate) |
| HashMap | ~0.3s | ~0.2s | N/A |
| Fenwick | ~0.2s | ~0.1s (prefix) | ~0.2s |
| Segment | ~0.5s | ~0.4s | ~0.5s |

## When to Use What

- **AVL**: Read-heavy workloads where search speed is critical
- **Red-Black**: Write-heavy workloads (TreeMap/Set)
- **B-Tree**: Disk-based storage (databases, file systems)
- **Fenwick**: Frequent prefix sum queries with point updates
- **Segment**: Complex range queries (sum, min, max) with range updates
