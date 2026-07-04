# Mental Models for Hash Tables

## The Dictionary

A dictionary maps words (keys) to definitions (values). To find a word, you could search sequentially (O(n)) or use an **index** (the hash function) that tells you which page to look at. If multiple words map to the same page, you scan down the page (chaining).

## The Phone Book

Each person's name is hashed to a page number and a position on the page. If two people (e.g., "Smith" and "Smyth") hash to the same slot, one goes into the next available slot (open addressing).

## The Library Filing System

Books are assigned a call number (hash) based on their subject. Books with the same subject are shelved together in the same aisle (bucket). If too many books for one aisle, start a second shelf (rehashing) or use overflow shelves (chaining).

## The Locker Room

Each student gets a locker number computed from their student ID. If two students get the same number, they share a locker by putting their belongings one after another (chaining). If the locker room is too full (high load factor), expand the room and reassign lockers (rehash).

## The Coffee Shop Order

Each order has an order number (hash of customer name). All orders for the same number are pinned to the same board. When searching, find the board by number, then scan the pinned orders.

## Java HashMap Mental Model

```
HashMap<String, String>
┌──────────────┐
│ table[]      │   ← array of buckets (Node<K,V>[])
├──────┬───────┤
│ 0    │ null  │
│ 1    │ null  │
│ 2    │ Node─→Node─→Node  (chained list or tree)
│ 3    │ Node  │
│ 4    │ null  │
└──────┴───────┘
Each Node: hash | key | value | next
```

To `get("hello")`:
1. Compute `hash = "hello".hashCode()` → e.g., 99162322
2. Compute index: `(n - 1) & hash` → bucket 2
3. Scan chain at bucket 2, compare keys with `equals()`
4. Return value or null
