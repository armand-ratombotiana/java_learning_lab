# ConcurrentHashMap Internals

## 🔬 Deep Dive into Java 8+ Architecture

### 1. Volatile Table & Nodes
In `ConcurrentHashMap`, the underlying array and the `val` and `next` fields of the `Node` are marked as `volatile`.
```java
transient volatile Node<K,V>[] table;

static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    volatile V val;
    volatile Node<K,V> next;
}
```
**Why?** `volatile` guarantees **Memory Visibility**. If Thread A updates a value, the `volatile` keyword ensures that the change is flushed to main memory immediately, and Thread B will read the freshest value, bypassing stale CPU caches. This allows `get()` operations to be entirely lock-free.

### 2. Compare-And-Swap (CAS)
When inserting a new node into an *empty* bucket, `ConcurrentHashMap` uses a CAS operation (via `Unsafe` or `VarHandle`).
```java
// Simplified pseudo-code
if (table[index] == null) {
    if (casTabAt(table, index, null, newNode)) {
        break; // Successfully inserted without locks!
    }
}
```
CAS is an atomic hardware instruction. It says: "Update this memory address to `newNode`, but ONLY if the current value is still `null`". If another thread beat us to it, CAS fails, and we retry.

### 3. Node-Level Locking
If a bucket is *not* empty (a collision occurred), CAS cannot be used safely for chaining. Instead, the thread `synchronize`s on the **head node** of the bucket.
```java
synchronized (headNode) {
    // Traverse the chain and append/update
}
```
This means if Thread A is writing to Bucket 5, and Thread B is writing to Bucket 12, they do not block each other at all.

### 4. Concurrent Resizing & ForwardingNodes
Resizing a `HashMap` is traditionally a massive stop-the-world operation. `ConcurrentHashMap` solves this brilliantly by allowing **multiple threads to resize the map collaboratively**.

When the map decides to resize, it creates a new, larger table. As it moves buckets from the old table to the new table, it replaces the old bucket's head with a special `ForwardingNode`.
```java
static final class ForwardingNode<K,V> extends Node<K,V> {
    final Node<K,V>[] nextTable;
}
```
If a thread attempts a `put()` and encounters a `ForwardingNode`, it realizes a resize is in progress. Instead of blocking and waiting, **it joins in and helps transfer other buckets** to the new table before completing its own `put()`.