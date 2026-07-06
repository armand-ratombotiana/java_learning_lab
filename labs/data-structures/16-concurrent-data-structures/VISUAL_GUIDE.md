# Visual Guide to Concurrent Data Structures

## Treiber Stack Push

Thread 1: push(3)
`
top â†’ [2] â†’ [1] â†’ null
1. oldTop = top (2)
2. newTop.next = oldTop (2)
3. CAS(&top, 2, newTop) â†’ success
top â†’ [3] â†’ [2] â†’ [1] â†’ null
`

## Michael-Scott Queue Enqueue

`
head â†’ sentinel â†’ [A] â†’ [B] â†’ null
                          â†‘tail
1. Create new node [C]
2. CAS(tail.next, null, [C])
3. CAS(tail, [B], [C])
`
"@

System.Collections.Hashtable["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: LockFreeStack.java

The Treiber stack uses AtomicReference for the top pointer. The push operation loops until CAS succeeds. The pop operation checks for empty and uses CAS to update the top.

## Key Pattern

All lock-free operations follow the same pattern:
1. Read current state
2. Compute new state
3. CAS to publish new state
4. If CAS fails, retry from step 1
