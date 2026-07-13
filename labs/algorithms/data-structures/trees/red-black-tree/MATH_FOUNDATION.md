# Mathematical Foundation of Red-Black Trees

## 📐 The 5 Invariants (Rules)
A Red-Black tree is a Binary Search Tree that satisfies the following 5 mathematical properties:

1. **Node Color**: Every node is either Red or Black.
2. **Root Property**: The root of the tree is always Black.
3. **Leaf Property**: Every leaf (NIL/NULL node) is Black.
4. **Red Property**: If a node is Red, then both its children must be Black. (Meaning: You can NEVER have two Red nodes in a row on a path).
5. **Black Depth Property**: For any given node, every simple path from that node to any of its descendant leaves contains the exact same number of Black nodes.

## 📏 Proving the $O(\log N)$ Height Guarantee
Why do these 5 rules guarantee that the tree remains balanced?

Let $bh(x)$ be the "Black Height" of a node $x$ (the number of black nodes on any path from $x$ down to a leaf, not counting $x$ itself).
By Rule 5, every path from $x$ to a leaf has exactly $bh(x)$ black nodes.

By Rule 4 (no two reds in a row), the maximum possible length of a path is achieved by alternating Red and Black nodes (Black -> Red -> Black -> Red -> Black).
Therefore, the longest possible path from the root to a leaf is at most **twice as long** as the shortest possible path (which would be all Black nodes).

**The Mathematical Proof**:
A subtree rooted at node $x$ contains at least $2^{bh(x)} - 1$ internal nodes.
Let $h$ be the total height of the tree.
Because at least half the nodes on any path must be Black (Rule 4), the black height of the root must be at least $h/2$.

Therefore, the total number of internal nodes $N$ is:
$$ N \ge 2^{h/2} - 1 $$
$$ N + 1 \ge 2^{h/2} $$
$$ \log_2(N + 1) \ge h/2 $$
$$ h \le 2 \log_2(N + 1) $$

This proves that the maximum height $h$ is strictly bounded by $2 \log_2(N + 1)$. Because the height is logarithmically bounded, all operations (Search, Insert, Delete) are mathematically guaranteed to take $O(\log N)$ time in the worst case.