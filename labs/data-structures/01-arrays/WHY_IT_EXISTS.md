# Why Arrays Exist

Arrays exist to solve the fundamental problem of **storing and accessing multiple values of the same type** using a single name and numeric indices.

## Problems Before Arrays

- **Individual variables**: `score1, score2, score3, ...` — unmanageable beyond a few items
- **No random access**: linked structures require traversal to reach position i
- **No cache efficiency**: pointer-based structures scatter data across memory

## The Gap Arrays Fill

| Need | Array Solution |
|------|---------------|
| Group related data | Contiguous block with shared name |
| Fast indexed access | O(1) via pointer arithmetic |
| Cache-friendly iteration | Sequential memory layout |
| Foundation for other structures | Used by heaps, hash tables, strings |
| Interop with hardware | Mirrors physical memory layout |

## Java-Specific Context

Java arrays are:
- **Objects** (stored on heap, have a `length` field)
- **Covariant** (`String[]` is subtype of `Object[]`)
- **Bounded** (index checking throws `ArrayIndexOutOfBoundsException`)
- **Type-reified at runtime** (int[] retains component type)

Arrays are the most basic building block of data structures — every complex structure ultimately rests on arrays or objects that reference them.
