# Mathematical Foundation of AVL Trees

## 📐 The Balance Factor
The core of the AVL tree is the **Balance Factor (BF)**. For any node $N$, the balance factor is defined as the difference between the heights of its left and right subtrees:

$$ BF(N) = Height(LeftSubtree(N)) - Height(RightSubtree(N)) $$

**The AVL Invariant**: A tree is an AVL tree if and only if for every node $N$:
$$ BF(N) \in \{-1, 0, 1\} $$

If $|BF(N)| > 1$, the tree is unbalanced and a rotation is required.

## 🔄 The 4 Rotation Cases
When an insertion or deletion causes a node to have a $BF$ of $2$ or $-2$, we perform one of four rotations based on the shape of the imbalance.

### 1. Left-Left (LL) Case
- *Condition*: Node $A$ has $BF=2$ and its left child $B$ has $BF \ge 0$.
- *Fix*: Single **Right Rotation** on $A$.

### 2. Right-Right (RR) Case
- *Condition*: Node $A$ has $BF=-2$ and its right child $B$ has $BF \le 0$.
- *Fix*: Single **Left Rotation** on $A$.

### 3. Left-Right (LR) Case
- *Condition*: Node $A$ has $BF=2$ and its left child $B$ has $BF=-1$.
- *Fix*: **Left Rotation** on $B$, then **Right Rotation** on $A$.

### 4. Right-Left (RL) Case
- *Condition*: Node $A$ has $BF=-2$ and its right child $B$ has $BF=1$.
- *Fix*: **Right Rotation** on $B$, then **Left Rotation** on $A$.

## 📏 Complexity Proof
Why is the height of an AVL tree with $n$ nodes guaranteed to be $O(\log n)$?

Let $M(h)$ be the minimum number of nodes in an AVL tree of height $h$.
- $M(0) = 1$
- $M(1) = 2$
- $M(h) = 1 + M(h-1) + M(h-2)$ (The root + one subtree of height $h-1$ + one subtree of height $h-2$).

This recurrence is very similar to the Fibonacci sequence. It can be shown that:
$$ M(h) > \left(\frac{1+\sqrt{5}}{2}\right)^h \approx 1.618^h $$
Solving for $h$ in terms of $n$:
$$ h < 1.44 \log_2(n) $$
This proves that the height is strictly logarithmic, ensuring that search, insert, and delete operations always take $O(\log n)$ time.