# Interview Questions: Treap (Randomized BST)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 295 Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | Hard | Google, Amazon, Meta, Microsoft | Two heaps / balanced BST |
| [LC 480 Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) | Hard | Google, Amazon, Meta | Balanced BST with lazy deletion |
| [LC 731 My Calendar II](https://leetcode.com/problems/my-calendar-ii/) | Medium | Google, Amazon, Meta, Microsoft | Interval management |
| [LC 732 My Calendar III](https://leetcode.com/problems/my-calendar-iii/) | Hard | Google, Amazon, Meta | TreeMap / segment tree |
| [LC 699 Falling Squares](https://leetcode.com/problems/falling-squares/) | Hard | Google, Amazon, Meta | Coordinate compression + segment tree |
| (Implicit treap) | — | Google, Amazon, Meta | Range operations on arrays |

## NeetCode Reference
Not in NeetCode 150. Treap is an advanced BST variant covered in NeetCode 250.

## Company-Specific Questions

### Google
- Implement a Treap (randomized BST) with insert, delete, and search operations
- How does the random priority guarantee O(log n) expected time? Explain the probabilistic analysis
- Compare Treap vs AVL vs Red-Black tree — which has simpler implementation and why?
- Implement an Implicit Treap (Cartesian tree) for array range operations (reverse, insert, delete)

### Microsoft
- Implement a split/merge-based Treap — how does split(root, key) and merge(left, right) work?
- How would you implement order statistics using a Treap (size-augmented)?
- Compare Treap vs Skip List — both use randomization, which is easier to implement?

### Meta
- Implement an implicit treap to support: range add, range sum, and range reverse on an array
- How would you use a Treap as a priority queue with O(log n) extract-max and O(log n) insert?
- Compare Treap vs Segment Tree for range operations on dynamic arrays

### Amazon
- Design a Treap for storing product inventory sorted by SKU with O(log n) insert/delete/range queries
- How would you implement a Treap that supports concurrent operations?
- Compare Treap vs B-tree for memory-constrained inventory management

### Apple
- Implement a Treap that supports the split and join operations for playlist management
- How would you implement an indexed sequence with O(log n) get/set at any position (implicit treap)?
- Design a Treap for real-time media annotations (ordered list with efficient splices)

### Oracle
- How does randomized balancing simplify Treap implementation compared to deterministic balancing?
- What is the expected height of a Treap? (O(log n)) What is the worst-case? (O(n), probability exponentially small)
- Compare Treap vs Patricia trie for lexicographically ordered key storage

## Real Production Scenarios

- **Scenario 1: Online Order Statistics** — A real-time trading dashboard maintains a Treap of order prices. The Treap supports insert (new order), delete (cancelled order), and O(log n) queries for "what is the kth smallest price?" and "what is the median price?" This is more flexible than a two-heap approach for median queries.

- **Scenario 2: Text Editor Buffer with Implicit Treap** — A code editor represents the document as an implicit treap. Each node stores a character or a substring. Operations: insert(")", O(log n)); delete range (O(log n)); reverse selection (O(log n) with lazy propagation). Compared to a rope, the implicit treap handles range reversals elegantly.

- **Scenario 3: Memory Manager** — A custom memory allocator uses a Treap of free memory blocks keyed by address (for coalescing) and a secondary priority queue by size (for best-fit). The Treap supports O(log n) insert (free a block), delete (allocate), and finding adjacent blocks.

## Interview Tips

- Time: O(log n) expected for all operations (insert, delete, search, split, merge)
- Space: O(n) — each node stores key, priority, left/right pointers, optionally size for order statistics
- Common edge cases: empty treap, duplicate keys (decide convention — left ≤ right or left < right), priority conflicts
- Treap is a combination of BST (key ordering) and heap (priority ordering) — hence the name
- Random priority: use a good random source (`ThreadLocalRandom.current().nextInt()`)
- Split(root, key): returns (left tree with keys < key, right tree with keys ≥ key)
- Merge(left, right): all keys in left < all keys in right; choose root by comparing priorities
- Insert(key): split at key, merge(left, newNode), then merge with right
- Delete(key): split at key, split right at key+1, return merge(left, right_of_right)
- Implicit treap: key is the index (implicitly defined by size of left subtree). No explicit key stored

## Java-Specific Considerations

- No standard Treap class in Java — implement from scratch
- Node structure: `class TreapNode { int key, priority; TreapNode left, right; int size; }`
- `Random random = new Random()` — or `ThreadLocalRandom.current()` for better concurrent performance
- Priority generation: `int priority = random.nextInt();` — 32-bit random, uniform distribution
- `updateSize(node): if (node != null) node.size = 1 + size(node.left) + size(node.right);`
- Return `TreapNode[]` from split: `TreapNode[] split(TreapNode root, int key)` — returns [left, right]
- Split implementation:
  ```java
  if (root == null) return new TreapNode[]{null, null};
  if (root.key < key) { TreapNode[] r = split(root.right, key); root.right = r[0]; updateSize(root); return new TreapNode[]{root, r[1]}; }
  else { TreapNode[] l = split(root.left, key); root.left = l[1]; updateSize(root); return new TreapNode[]{l[0], root}; }
  ```
- Merge implementation:
  ```java
  if (left == null) return right; if (right == null) return left;
  if (left.priority > right.priority) { left.right = merge(left.right, right); updateSize(left); return left; }
  else { right.left = merge(left, right.left); updateSize(right); return right; }
  ```
- Implicit treap: split by size (not key) — `split(TreapNode root, int index)` splits into [0, index) and [index, n)
- Lazy propagation in implicit treap: store reverse flag, push to children during split/merge
- `ThreadLocalRandom` for priority in multi-threaded contexts
- `java.util.Objects.hash()` for generating priorities if key hashing is desired
- Recursive implementation uses O(log n) stack (fine for balanced trees)
- Custom IntPriorityTreap for integer keys: specialized version to avoid boxing overhead
