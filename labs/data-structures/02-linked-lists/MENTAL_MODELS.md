# Mental Models for Linked Lists

## The Treasure Hunt (Singly Linked)

Each clue (node) tells you where to find the next clue. You start at the first clue and follow references. To find clue #5, you must visit clues 1, 2, 3, 4 first. There is no way to jump ahead.

## The Train (Doubly Linked)

Each car knows the car before it and the car after it. You can walk forward from the engine or backward from the caboose. Adding a car in the middle means coupling it to both neighbors. To remove a car, you decouple it from both adjacent cars and connect them directly.

## The Round Table (Circular)

Knights sitting at a round table — each looks to the right for the next knight. There is no head or tail; every position is equivalent. To go around the table, you keep following the next pointer until you return where you started.

## The Janitor's Keys (Sentinel Nodes)

A janitor has a master key ring (sentinel) that always exists, even when no real keys are on it. The master ring is always there, so you never have to check "is the ring null?" — you always start from the master ring and check if it points to anything.

## Java Reference Model

```
LinkedList<String>
  ┌─────────────────┐
  │ first (Node)──┐ │
  │ last  (Node)──│─┤
  │ size = 3      │ │
  └────────────────┘ │
                     ▼
        ┌──────────────────────┐
        │ Node<String>         │
        │ prev = null          │
        │ data = "A"           │
        │ next = ──────────┐   │
        └──────────────────┘   │
                               ▼
                    ┌──────────────────────┐
                    │ Node<String>         │
                    │ prev = ────────────┐ │
                    │ data = "B"          │ │
                    │ next = ──────────┐ │ │
                    └──────────────────┘ │ │
                                         ▼ ▼
                              ┌──────────────────────┐
                              │ Node<String>         │
                              │ prev = ────────────  │
                              │ data = "C"           │
                              │ next = null          │
                              └──────────────────────┘
```
