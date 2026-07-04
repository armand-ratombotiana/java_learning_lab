# Math Foundation: Linked Lists

## Node Count

For a linked list of length n:
- **Singly**: n nodes, n next references
- **Doubly**: n nodes, n prev references, n next references
- **Memory per node**: object header (~12-16 bytes) + data reference (4-8 bytes) + pointer(s)

## Pointer Manipulation

When reversing a singly linked list iteratively:

```
prev = null
current = head
while current != null:
    next = current.next    // save forward reference
    current.next = prev    // reverse pointer
    prev = current         // advance prev
    current = next         // advance current
```

This is O(n) time, O(1) space — a constant number of references regardless of list size.

## Cycle Detection (Floyd's Algorithm)

For a cycle starting at distance `a` from head, with cycle length `c`:
- Slow pointer moves 1 step, fast moves 2 steps
- They meet after `a + b` steps where `b ≡ a + b (mod c)` → `c | a`
- Distance from meeting point to cycle start = `a`

Proof: Let distance from head to cycle start = a, from cycle start to meeting point = b.
Slow: a + b. Fast: 2(a + b). Since fast = slow + c·k, 2(a+b) = a+b + c·k → a+b = c·k → b = c·k - a.

## Skip List Expected Height

For a skip list with probability p = 1/2:
- Expected number of levels: O(log n)
- Expected nodes per level: n/2, n/4, n/8, ...
- Expected search time: O(log n)

## Complexity Summary

| Operation | Singly | Doubly | Circular |
|-----------|--------|--------|----------|
| Access | O(n) | O(n) | O(n) |
| Search | O(n) | O(n) | O(n) |
| Head insert | O(1) | O(1) | O(1) |
| Tail insert* | O(1) | O(1) | O(1) |
| Middle insert | O(n) | O(n) | O(n) |
| Head delete | O(1) | O(1) | O(1) |
| Known node delete | O(n) | O(1) | O(1)** |

*With tail pointer
**If node reference known
