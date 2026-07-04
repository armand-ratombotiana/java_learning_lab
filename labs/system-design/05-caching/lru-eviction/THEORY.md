# LRU Cache Eviction Theory

## 💡 The Caching Problem
A cache is a high-speed data storage layer (often in RAM) that stores a subset of data so that future requests for that data are served faster than accessing the primary storage location (like a database or disk).

Because RAM is expensive and finite, a cache cannot hold everything. When the cache reaches its maximum capacity, it must remove an existing item to make room for a new one. This is called **Cache Eviction**.

## ⚖️ Eviction Policies
How do you choose which item to evict? The goal is to maximize the **Cache Hit Rate** by keeping the data that is most likely to be requested again.

1. **FIFO (First In, First Out)**: Evicts the oldest item inserted. 
   - *Problem*: An item inserted a long time ago might be the most frequently accessed item (e.g., the homepage configuration).
2. **LFU (Least Frequently Used)**: Evicts the item with the lowest access count.
   - *Problem*: Requires keeping a counter for every item. An item that was heavily accessed yesterday but is useless today will remain in the cache, polluting it.
3. **LRU (Least Recently Used)**: Evicts the item that has not been accessed for the longest time.
   - *Benefit*: This perfectly aligns with the principle of **Temporal Locality**—if data was accessed recently, it is highly likely to be accessed again soon.

## 🏗️ LRU Architecture
To implement an LRU cache efficiently, we need two operations to be O(1) (constant time):
1. **Lookups**: Finding if a key exists in the cache.
2. **Updates**: Moving an accessed item to the "most recently used" position, or evicting the "least recently used" item.

A standard array or linked list cannot achieve O(1) for both. The industry standard architecture combines two data structures:
1. **HashMap**: Provides O(1) lookups. The keys are the cache keys, and the values are pointers to nodes in a linked list.
2. **Doubly-Linked List**: Provides O(1) insertions, deletions, and updates. It maintains the chronological order of access. The head represents the most recently used item, and the tail represents the least recently used item.