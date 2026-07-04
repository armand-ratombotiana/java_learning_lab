# Reflection: Advanced Trees

## What I Learned

- Self-balancing trees (AVL, Red-Black) guarantee O(log n) operations regardless of input
- AVL trees are stricter (balance factor ≤ 1), Red-Black trees allow more imbalance (height ≤ 2 log n)
- B-trees optimize for disk I/O with high branching factors
- Fenwick trees provide elegant prefix sum queries with minimal code
- Segment trees support arbitrary range operations with lazy propagation
- TreeMap in Java is a Red-Black tree implementation with NavigableMap features

## Questions to Consider

1. When would you choose an AVL tree over a Red-Black tree? (Read-heavy → AVL)
2. Why do databases use B-trees instead of AVL trees? (Disk block alignment, high branching factor)
3. How does lazy propagation improve segment tree performance? (Defers updates to query time)
4. Why does Java's TreeMap not allow null keys? (Comparable.compareTo throws NPE)
5. How would you implement a B+ tree with leaf-linked-list for range queries?

## Connections

Advanced trees connect to:
- **Basic trees** (BST is the foundation of all)
- **Databases** (B-tree, B+ tree indexing)
- **Competitive programming** (segment tree, Fenwick tree)
- **Java collections** (TreeMap, TreeSet)
- **File systems** (B-tree-based directory indexing)
- **Networking** (routing tables use binary tries)

## Self-Assessment

- [ ] Can implement BST with all operations
- [ ] Can implement AVL tree with rotations
- [ ] Understand Red-Black tree invariants
- [ ] Can implement B-tree insert with split
- [ ] Can implement Fenwick tree for prefix sums
- [ ] Can implement segment tree with query and update
- [ ] Know when to use each tree type
