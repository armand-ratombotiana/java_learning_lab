# Visual Guide to Linked Lists

## Singly Linked List

```
Head ──→ ┌─────┬────┐    ┌─────┬────┐    ┌─────┬────┐
         │ 10  │ ───┼──→ │ 20  │ ───┼──→ │ 30  │ ╱  │
         └─────┴────┘    └─────┴────┘    └─────┴────┘
```

## Doubly Linked List

```
         ┌────┬─────┬────┐    ┌────┬─────┬────┐    ┌────┬─────┬────┐
Head ──→ │ ╱  │ 10  │ ───┼──→ │ ───│ 20  │ ───┼──→ │ ───│ 30  │ ╱  │
         └────┴─────┴────┘    └────┴─────┴────┘    └────┴─────┴────┘
               ↑                    ↕                    ↕
         ┌──────┘              ┌─────────┘         ┌──────┘
         │ prev pointers       │                   │
         └─────────────────────┴───────────────────┘
                                                     Tail ←── ┘
```

## Insert at Head — Singly

```
Before:
Head → [20] → [30] → null

Insert(10) at head:
1. newNode(10), newNode.next = Head (→ [20])
2. Head = newNode

After:
Head → [10] → [20] → [30] → null
```

## Insert at Middle — Singly

```
Before:
Head → [10] → [30] → null

Insert(20) after [10]:
1. newNode(20)
2. newNode.next = [10].next (→ [30])
3. [10].next = newNode

After:
Head → [10] → [20] → [30] → null
```

## Delete Head — Singly

```
Before:
Head → [10] → [20] → [30] → null

Delete head:
1. Head = Head.next (→ [20])

After:
Head → [20] → [30] → null
```

## Delete Middle — Singly

```
Before:
Head → [10] → [20] → [30] → null

Delete [20]:
1. Find node before [20] → [10]
2. [10].next = [20].next (→ [30])

After:
Head → [10] → [30] → null
```

## Circular Singly Linked List

```
      ┌──────────────────────────────┐
      │                              │
      ▼                              │
┌─────┬────┐    ┌─────┬────┐    ┌─────┬────┐
│ 10  │ ───┼──→ │ 20  │ ───┼──→ │ 30  │ ───┘
└─────┴────┘    └─────┴────┘    └─────┴────┘
```

## Reverse Singly Linked List

```
Before:
Head → [10] → [20] → [30] → null

Step 1: prev=null, curr=[10]
  [10].next = null
  prev = [10], curr = [20]

Step 2: [20].next = [10]
  prev = [20], curr = [30]

Step 3: [30].next = [20]
  prev = [30], curr = null

After:
Head → [30] → [20] → [10] → null
```
