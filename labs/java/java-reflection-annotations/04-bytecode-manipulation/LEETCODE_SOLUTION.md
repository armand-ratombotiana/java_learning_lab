# LEETCODE_SOLUTION — 706. Design HashMap

## Problem
Implement a HashMap without using any built‑in hash table libraries.

## Bytecode Context
A ByteBuddy‑generated class could serve as a benchmark stub for performance tests.

```java
class MyHashMap {
    private int[] map = new int[1_000_001];

    public MyHashMap() {
        Arrays.fill(map, -1);
    }

    public void put(int key, int value) { map[key] = value; }
    public int get(int key) { return map[key]; }
    public void remove(int key) { map[key] = -1; }
}
```

## Key Insight
Bytecode generation shines for creating thousands of similar classes at runtime — like entity proxies in Hibernate.

## Complexity
- Time: O(1) per operation
- Space: O(range of keys)
