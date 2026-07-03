# Collections — Visual Guide

## Collection Hierarchy

```
                        Iterable
                           │
                     Collection
                  ┌────────┼──────┐
                  │       │      │
                List     Set   Queue
               ┌─┴─┐   ┌─┼─┐    │
           ArrL LnkL HS LHS TS   PQ
           iSt iSt        │      ├──
                         LS     DQ
                                ├──
                              ArrD LnkL
                              eQ  ist

 Map
 ┌──┼──┐
HM LHM TM
```

## ArrayList Memory Layout

```
elementData: [ "a" | "b" | "c" | "d" | null | null | null | null | null | null ]
               ↑                                         ↑
             index 0                                  size = 4
 capacity = 10

After grow (size == capacity):
elementData: [ "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | null | ... ]
               ↑                                                ↑
             index 0                                         size = 15 (after 50% growth)
```

## HashMap Structure

```
Table (Node[]):
[0]   → Node("a", 1) → Node("i", 9)       [linked list]
[1]   null
[2]   → Node("b", 2)
[3]   null
...
[15]  → Node("p", 16) → TreeNode (after 8+)

Each node:
┌─────────────────────────┐
│ hash: 97                │
│ key: "a"                │
│ value: 1                │
│ next: → (next node)     │
└─────────────────────────┘
```

## LinkedList (Doubly-Linked)

```
null ←─── [prev|"a"|next] ←──→ [prev|"b"|next] ←──→ [prev|"c"|next] ───→ null
            ↑ first                                      ↑ last
```

## TreeSet / Red-Black Tree

```
        "d" (black)
       /          \
  "b" (red)    "g" (black)
   /    \       /    \
 "a"   "c"    "f"    "h"

(null leaves omitted)
```

## Queue vs Deque

```
Queue (FIFO):
  addLast → [ a | b | c | d ] → removeFirst

Deque (double-ended):
  addFirst → [ a | b | c | d ] → addLast
  removeFirst ← [ a | b | c | d ] → removeLast
```

## Selection Decision Tree

```
Need ordered collection?
  ├─ Yes → Need duplicates?
  │        ├─ Yes → Use List
  │        │        ├─ Need fast random access? → ArrayList
  │        │        └─ Need fast add/remove at ends? → LinkedList
  │        └─ No  → Use Set
  │                 ├─ Need sorted order? → TreeSet
  │                 └─ Need insertion order? → LinkedHashSet
  └─ No  → Need key-value mapping?
           ├─ Yes → Use Map
           │        ├─ Need sorted keys? → TreeMap
           │        ├─ Need insertion order? → LinkedHashMap
           │        └─ General purpose → HashMap
           └─ No  → Use Set (no duplicates) → HashSet
                    Or List (with duplicates) → ArrayList
```
