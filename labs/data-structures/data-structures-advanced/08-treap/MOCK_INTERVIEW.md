# Mock Interview: Treap

## Setting

- **Round**: Onsite data structures coding
- **Duration**: 45 minutes
- **Focus**: Treap, implicit treap, range operations

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** What's a treap and what operations does it support that a regular BST does not?

**Candidate:** A treap = BST (by key) + heap (by random priority). The random priority keeps it balanced with high probability. Unique operations are split and merge, which let us:
- Insert/delete by splitting and merging
- Support order statistics (with subtree sizing)
- Support range operations in implicit mode (reverse, range sum)
- Create persistent versions easily

---

### Part 2: Core Problem — Implicit Treap for Text Editor (25 min)

**Interviewer:** Implement a text editor data structure supporting insert at position, delete range, reverse range, and get character. All in O(log n).

**Candidate:** I'll use an implicit treap. Each node stores one character, has a random priority, subtree size, and a lazy reverse flag.

Let me implement the core:

```java
class ImplicitTreap {
    class Node {
        char val;
        int prio, size;
        boolean rev;
        Node left, right;
        Node(char v) { val = v; prio = nextInt(); size = 1; }
    }

    Node root;

    int size(Node n) { return n == null ? 0 : n.size; }

    void push(Node n) {
        if (n != null && n.rev) {
            Node t = n.left; n.left = n.right; n.right = t;
            if (n.left != null) n.left.rev ^= true;
            if (n.right != null) n.right.rev ^= true;
            n.rev = false;
        }
    }

    void update(Node n) {
        if (n != null) n.size = size(n.left) + 1 + size(n.right);
    }

    Node[] split(Node n, int k) { // [0..k-1], [k..]
        if (n == null) return new Node[]{null, null};
        push(n);
        if (k <= size(n.left)) {
            Node[] p = split(n.left, k);
            n.left = p[1]; update(n);
            return new Node[]{p[0], n};
        } else {
            Node[] p = split(n.right, k - size(n.left) - 1);
            n.right = p[0]; update(n);
            return new Node[]{n, p[1]};
        }
    }

    Node merge(Node l, Node r) {
        if (l == null || r == null) return l == null ? r : l;
        push(l); push(r);
        if (l.prio > r.prio) {
            l.right = merge(l.right, r); update(l); return l;
        } else {
            r.left = merge(l, r.left); update(r); return r;
        }
    }

    void insert(int pos, char v) {
        Node[] p = split(root, pos);
        root = merge(merge(p[0], new Node(v)), p[1]);
    }

    void delete(int l, int r) {
        Node[] p1 = split(root, l);
        Node[] p2 = split(p1[1], r - l + 1);
        root = merge(p1[0], p2[1]);
    }

    void reverse(int l, int r) {
        Node[] p1 = split(root, l);
        Node[] p2 = split(p1[1], r - l + 1);
        if (p2[0] != null) p2[0].rev ^= true;
        root = merge(p1[0], merge(p2[0], p2[1]));
    }

    char get(int pos) {
        Node n = root;
        while (true) {
            push(n);
            int ls = size(n.left);
            if (pos < ls) n = n.left;
            else if (pos == ls) return n.val;
            else { pos -= ls + 1; n = n.right; }
        }
    }
}
```

**Interviewer:** Walk me through insert(pos=3, 'X') on string "hello".

**Candidate:**
1. `split(root, 3)`: Split tree so left has first 3 chars ("hel"), right has rest ("lo")
2. Create new Node('X')
3. `merge(left, newNode)` → "helX"
4. `merge("helX", "lo")` → "helXlo"

Total: O(log n) for two splits + two merges.

**Interviewer:** And reverse(1, 4) on "hello"?

**Candidate:**
1. `split(root, 1)`: left = "h", right = "ello"
2. `split(right, 4)`: left = "ello", right = "" (exhausted)
3. Set `ello.rev = true` (lazy)
4. Merge "h" + reversed "ello" + empty = "holle"

The lazy flag means we just mark the node — actual swap happens during push when split/merge traverses.

---

### Part 3: Follow-up (10 min)

**Interviewer:** How would you support undo/redo?

**Candidate:**
**Persistent treap**: Each operation (insert/delete/reverse) creates a new root. Old roots remain valid. Maintain a stack of roots — undo pops to previous root, redo pushes forward.

Since treap operations only create O(log n) new nodes per modification (path copying), the total space for 10⁵ operations is O(n + q log n).

```java
class PersistentRope {
    List<Node> versions = new ArrayList<>();
    int currentVersion = 0;

    void insert(int pos, char v) {
        Node newRoot = ... // functional insert (path copy)
        versions.add(++currentVersion, newRoot);
    }

    void undo() { currentVersion = Math.max(0, currentVersion - 1); }
}
```

**Interviewer:** How would you handle very long strings (10⁸ chars)?

**Candidate:** Don't store one character per node. Use chunk-based treap:
- Each node stores a char[] chunk (e.g., 4KB)
- `split` on a chunk boundary returns two nodes
- `split` inside a chunk splits the chunk into two nodes
- `update` propagates total length
- Reverse just sets lazy flag (the actual reversal swaps whole chunks)

This reduces node count from O(n) to O(n / chunkSize).

---

### Part 4: System Design (5 min)

**Interviewer:** Design Google Docs' real-time collaborative editing.

**Candidate:**
- **Core**: Implicit treap per document for O(log n) operations
- **OT/CRDT**: Each operation has a position + length. Transform operations against concurrent edits
- **Server authoritative**: Server holds the canonical treap, clients send ops
- **Persistence**: Persistent treap for version history + undo
- **Wire protocol**: (opType, position, length, content) tuples
- **Concurrency**: Operational transformation over treap positions; when concurrent insert at same position, use site ID to order

---

## Debrief

### What Went Well
- Implicit treap implementation from scratch, correct
- Lazy reverse flag explained well
- Efficiency shown with split/merge reasoning
- Good follow-up on persistence and chunk-based treap

### Areas for Growth
- Could mention array-based treap for small strings
- Concurrency handling could be deeper

### Score
| Category | Score (1-5) |
|----------|-------------|
| Implicit Treap Knowledge | 5 |
| Code Quality | 5 |
| Complexity Analysis | 5 |
| Persistence Understanding | 4 |
| System Design | 4 |
| **Overall** | **4.6 / 5** |