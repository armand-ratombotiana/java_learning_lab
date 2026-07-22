# Interview Questions: XOR & Unrolled Linked Lists

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 136 Single Number](https://leetcode.com/problems/single-number/) | Easy | Google, Amazon, Microsoft, Apple, Meta | XOR property |
| [LC 268 Missing Number](https://leetcode.com/problems/missing-number/) | Easy | Amazon, Meta, Google, Microsoft, Apple | XOR / arithmetic |
| [LC 260 Single Number III](https://leetcode.com/problems/single-number-iii/) | Medium | Google, Amazon, Meta | XOR to separate groups |
| [LC 1720 Decode XORed Array](https://leetcode.com/problems/decode-xored-array/) | Easy | Google, Amazon, Meta | XOR encoding/decoding |
| [LC 389 Find the Difference](https://leetcode.com/problems/find-the-difference/) | Easy | Amazon, Google, Meta | XOR character finding |
| (System design focus for unrolled lists) | — | Google, Meta, Microsoft, Apple | Cache-efficient data layout |

## NeetCode Reference
NeetCode 150: XOR problems are in the Bit Manipulation category (Single Number, Missing Number).

## Company-Specific Questions

### Google
- Explain how XOR linked lists work — how does XOR compression of prev + next pointers reduce memory?
- Implement an XOR linked list with insert, delete, and traverse (forward/backward) operations
- What are the limitations of XOR linked lists? (Requires pointer to previous node to start traversal, pointer arithmetic, not garbage-collected)
- Design an unrolled linked list — how does block size affect cache performance and insertion/deletion cost?

### Microsoft
- Compare XOR linked list vs doubly linked list — memory savings vs usability trade-offs
- Implement an unrolled linked list with insert, delete, and get operations — what block size is optimal?
- How would you implement a memory allocator using XOR linked lists for free block tracking?

### Meta
- Single Number II (LC 137) — solve using bit manipulation (XOR-based)
- Design a data structure for a text editor buffer that supports O(√n) insert/delete and O(1) character access (unrolled linked list / rope)
- How would you implement a cursor-based traversal over an XOR linked list?

### Amazon
- Design a cache-friendly list for frequently accessed items in a recommendation engine
- How would you implement a block list that stores items in sorted order within each block?
- Compare unrolled linked list vs B-tree for block-based storage systems

### Apple
- How would you design a memory pool for small objects using XOR linked list free lists?
- Implement a text editing buffer using a gap buffer (related to unrolled linked lists for text)
- How does using memory pools avoid fragmentation in unrolled linked lists?

### Oracle
- What are the garbage collection implications of XOR linked lists in Java? (Objects can't be relocated without computing the wrong "pointer")
- How would you implement an unrolled linked list for a database cache (block-aware sizing)?
- Compare the memory locality of unrolled linked list vs ArrayList vs LinkedList

## Real Production Scenarios

- **Scenario 1: Memory-Constrained Embedded System** — An embedded device uses XOR linked lists for its memory allocator free list. Each free block header stores `prev ^ next` in a single pointer field, saving one pointer per block (8 bytes per block on 64-bit). For 10,000 free blocks, this saves 80KB — significant on devices with 512KB total RAM.

- **Scenario 2: Text Editor Buffer** — A text editor uses an unrolled linked list (gap buffer variant) for the document buffer. Each block stores ~4KB of text. Insertion and deletion within a block are O(1) amortized (memmove within the block). Splitting and merging blocks are O(block_size). Character access is O(√n) average.

- **Scenario 3: Database Cache** — A database buffer pool uses an unrolled linked list for its page replacement structure. Each block holds 32-64 page references. The unrolled layout improves cache locality during FIFO/LRU scanning by reducing pointer chasing compared to a standard linked list.

## Interview Tips

- Time: O(n) for traversal (XOR list requires full traversal for random access), O(1) for insert/delete at known position; O(√n) average for unrolled list operations
- Space: XOR list saves ≈1 pointer per node vs doubly linked list; unrolled list reduces pointer overhead by storing multiple elements per node
- Common edge cases: XOR of two pointers may produce platform-specific results in Java (references not integers); unrolled list split/merge edge cases
- XOR property: `a ^ b ^ b = a` and `a ^ a = 0` — useful for finding unique elements
- XOR list traversal: `prev ^ current.ptr` gives next node; need to track both current and prev while traversing
- Unrolled list block size: typically 16-512 elements; too small → too many pointers; too large → expensive splits/merges

## Java-Specific Considerations

- XOR linked list is impractical in Java — no way to safely XOR references (int[] addresses not accessible from Java code)
- In C/C++: XOR list uses `uintptr_t` for pointer XOR; in Java, System.identityHashCode is not the memory address and XOR gives wrong results
- Unrolled linked list can be implemented: `class Block { Object[] items; int size; Block next; }` with `ArrayList<Block>` for tracking
- For block size: typically `int BLOCK_SIZE = (int)Math.sqrt(n)` adjusted dynamically
- `System.arraycopy()` for shifting elements within a block during insert/delete
- `Arrays.copyOfRange()` for splitting blocks
- Block merging: combine adjacent blocks when their total element count < BLOCK_SIZE / 2
- XOR problems in Java: `int x ^ y` for integer XOR operations; `long a ^ b` for long XOR
- `java.util.BitSet` for XOR-based set operations (xor(BitSet set) modifies the set)
- For XOR-related bitwise problems: `(x - 1) ^ x` to find bits that changed; `x & (-x)` to isolate lowest set bit
