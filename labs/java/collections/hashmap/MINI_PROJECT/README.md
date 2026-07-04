# Mini Project: Build an LRU Cache

## 🎯 Objective
Combine your knowledge of `HashMap` with a Doubly-Linked List to build a highly efficient Least Recently Used (LRU) Cache.

## 📝 Requirements
1. **O(1) Get**: Retrieve a value by key in constant time.
2. **O(1) Put**: Insert a key-value pair in constant time.
3. **Capacity Limit**: The cache is initialized with a maximum capacity.
4. **Eviction**: When the cache reaches its capacity, inserting a new item must evict the *least recently used* item. (Accessing an item via `get` or updating it via `put` makes it the *most recently used*).

## 🚫 Constraints
- You may **not** use `java.util.LinkedHashMap`.
- You must build the Doubly-Linked List from scratch.

## 💡 Architecture Hint
- Use a `HashMap<K, Node>` to achieve O(1) lookups.
- Use the Doubly-Linked List to maintain the order of usage.
- When an item is accessed, remove its Node from the list and append it to the head.
- When capacity is reached, remove the Node at the tail of the list, and remove its corresponding key from the `HashMap`.