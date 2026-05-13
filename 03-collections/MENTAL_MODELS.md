# Mental Models for Collections

## Collection Hierarchy

```
Iterable
    └─ Collection
        ├─ List (ordered, indexed)
        │   ├─ ArrayList (random access)
        │   ├─ LinkedList (insertion/deletion)
        │   └─ Vector (legacy, synchronized)
        ├─ Set (no duplicates)
        │   ├─ HashSet (fast, unordered)
        │   ├─ LinkedHashSet (insertion order)
        │   └─ TreeSet (sorted order)
        └─ Queue (FIFO)
            ├─ PriorityQueue
            └─ Deque
    └─ Map (key-value)
        ├─ HashMap (fast lookup)
        ├─ LinkedHashMap (insertion order)
        ├─ TreeMap (sorted keys)
        └─ Hashtable (legacy, synchronized)
```

## Operation Complexity

- **ArrayList**: O(1) get, O(n) add/remove
- **LinkedList**: O(n) get, O(1) add/remove
- **HashSet/HashMap**: O(1) average for add/get/remove
- **TreeSet/TreeMap**: O(log n) for all operations

## Memory Models

- ArrayList: Contiguous memory, grows by ~50%
- LinkedList: Nodes with pointers, more memory
- HashMap: Array of buckets with linked lists (or trees)