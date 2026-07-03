# Why Control Flow Matters

## Algorithm Expressiveness

Control flow is how algorithms are expressed. Every sorting algorithm, every search, every data structure traversal is built from loops and conditionals. Without mastering control flow, you cannot implement any non-trivial algorithm.

## Code Readability

Well-structured control flow communicates intent:
```java
// Clear: for-each expresses "process each element"
for (Item item : items) { process(item); }

// Less clear: traditional for with index
for (int i = 0; i < items.size(); i++) { process(items.get(i)); }
```

Choosing the right loop construct makes code self-documenting.

## Error Prevention

Control flow misunderstandings cause real bugs:
- Off-by-one errors (`<=` vs `<`)
- Infinite loops (missing update, wrong condition)
- Switch fall-through (missing `break`)
- Empty loop bodies (accidental `;` after `for`/`while`)

## Performance Implications

- For loops with `length` call recomputed each iteration (call out of loop)
- Enhanced for-loop on `LinkedList` uses efficient iterator (vs indexed loop which is O(n²))
- Switch with dense values compiles to `tableswitch` (O(1)) vs `lookupswitch` (O(log n))

## Industry Impact

Control flow is universal across languages. Mastering Java's control flow constructs transfers directly to C#, C++, JavaScript, and Kotlin. The concepts (selection, iteration, short-circuiting) are language-independent.
