# Interview Questions: Rope (Balanced Binary Tree for Strings)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 612 (not a standard LC — implement from scratch)](https://leetcode.com/problems/) | — | Google, Meta, Microsoft, Apple | Text editor / string operations |
| [LC 108 Convert Sorted Array to BST](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/) | Easy | Amazon, Google, Meta, Microsoft | Balanced tree construction |
| [LC 114 Flatten Binary Tree to Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/) | Medium | Amazon, Google, Microsoft, Meta | Tree-to-string traversal |
| (System design focus for text editors) | — | Google, Microsoft, Apple | Document representation |

## NeetCode Reference
Not in NeetCode 150. Rope data structure is relevant for text editor and string processing interviews.

## Company-Specific Questions

### Google
- Design a data structure for Google Docs that supports efficient insert, delete, and substring operations on a large document
- Implement a Rope with split, concat, insert, delete, and charAt operations — analyze time complexity
- Compare Rope vs StringBuilder vs gap buffer for a text editor — trade-offs for different workloads
- How would you implement a persistent Rope for collaborative editing (multi-user with versioning)?

### Microsoft
- Design a Rope that supports substring extraction in O(log n) time (balanced binary tree + lazy concatenation)
- How would you implement search-and-replace on a Rope? What is the time complexity?
- Compare Rope vs piece table (used in Visual Studio Code) for text buffer representation

### Meta
- Design a real-time collaborative document editor (Operational Transformation on Ropes)
- How would you implement find-and-replace on a large document using a Rope?
- Compare cursor movement and selection handling with a Rope vs array-based text buffer

### Amazon
- How would you implement a code editor for AWS Cloud9 using Rope data structures?
- Design a document diff engine that uses Rope-based trees for efficient comparison
- Compare Rope vs zipper data structure for navigating and editing hierarchical documents

### Apple
- How does NSTextStorage (Cocoa text system) use efficient string representation?
- Design a Rope-based buffer for Swift Playgrounds (interactive code editing)
- How would you implement syntax highlighting on a Rope (token spans that adjust with edits)?

### Oracle
- How would you implement a Rope for storing and querying XML/JSON documents as text?
- Compare Rope vs CLOB (Character Large Object) for database string storage
- How does Oracle Text's index use tree-based document representation?

## Real Production Scenarios

- **Scenario 1: Text Editor Buffer** — A modern text editor (like VSCode or Atom) uses a piece table or Rope for the document buffer. The Rope stores the document as a balanced binary tree where each leaf holds a short string (e.g., 4KB). Inserting or deleting characters at any position is O(log n). Undo/redo creates persistent versions by copying the affected path.

- **Scenario 2: Collaborative Document Editing** — Google Docs uses a data structure resembling a Rope with operational transformation (OT). Each edit operation (insert, delete) is represented as a position + delta. The Rope supports applying transformations efficiently. Character positions are tracked via balanced tree indices that handle concurrent edits.

- **Scenario 3: Versioned Document Storage** — A document management system stores historical versions of documents using persistent Ropes. Each version shares unchanged subtrees (structural sharing). Computing the diff between two versions only traverses the diverging paths — O(log n) for identifying changed regions.

## Interview Tips

- Time: O(log n) for index-based operations (charAt, insert, delete, split, concat) with a balanced tree; O(n) for toString (traversal)
- Space: O(n) — leaf nodes store string chunks; internal nodes store total length (and optionally hash)
- Common edge cases: empty rope (null root), concatenating with empty rope, split at boundaries, split beyond range
- Rope node stores: total length (sum of children's lengths), left child, right child, optionally leaf string data
- Balancing: use tree rotations or treap-based rope to maintain O(log n) height
- Leaf size: typically 4KB-16KB — small enough for efficient splits, large enough to avoid too many nodes
- Concatenation: create a new internal node with left and right children (rebalance if height difference exceeds threshold)
- Split: recursively split at the target position, returning (leftRope, rightRope)

## Java-Specific Considerations

- No standard Rope class in Java — implement from scratch
- `interface Rope { char charAt(int index); Rope insert(int pos, String s); Rope delete(int start, int end); Rope concat(Rope other); String toString(); }`
- Leaf node: `class Leaf implements Rope { String value; int length() { return value.length(); } }`
- Internal node: `class Branch implements Rope { Rope left, right; int length; }` — length = left.length() + right.length()
- Balanced implementation: use `class RopeNode { RopeNode left, right; String leaf; int length; int height; }` — AVL balancing
- Or use Treap (Cartesian tree): random priority + split/merge for O(log n) expected operations
- `StringBuffer` / `StringBuilder` for building toString() from traversal
- `charAt(index)`: traverse the tree: if index < left.length(), go left; else go right with index -= left.length()
- `split(index)`: traverse, splitting each node that spans the boundary
- `insert(pos, str)`: split at pos → left + leaf(str) + right → concat(concat(left, leaf(str)), right)
- `delete(start, end)`: split at start → (a, rest); split rest at (end-start) → (b, c); return concat(a, c)
- Substring: `substring(start, end) = delete(end, length).delete(0, start)`
- Rebalancing: monitor height difference; perform tree rotations when |h_left - h_right| > 1
- For persistent ropes: copy nodes along the path; share unchanged subtrees
- `java.io.RandomAccessFile` for file-backed rope (when rope is too large for memory)
- `java.nio.MappedByteBuffer` for memory-mapped file-based leaf storage
- Hash computation at internal nodes enables O(1) substring comparison (compare hash of substrings)
