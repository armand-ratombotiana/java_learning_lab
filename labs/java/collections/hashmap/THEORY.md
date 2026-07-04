# HashMap Theory & First Principles

## 💡 Intuition
Imagine a massive library with millions of books. If you want to find a specific book, searching shelf by shelf (O(n)) would take forever. Instead, what if you had a magic function that took the book's title and instantly told you exactly which shelf and position it was located at? 

That is the essence of a **Hash Map**. It provides O(1) average time complexity for lookups, insertions, and deletions by mapping a key to a specific memory location using a mathematical formula called a **Hash Function**.

## 🏗️ First Principles
A HashMap is fundamentally built on two core concepts:
1. **Array**: The underlying data structure that provides O(1) access by index.
2. **Hash Function**: A deterministic algorithm that converts an arbitrary key into a valid array index.

### The Core Mechanism
1. **Hash**: Calculate the hash code of the key.
2. **Index**: Map the hash code to an index in the underlying array (bucket).
3. **Store/Retrieve**: Place the key-value pair in that bucket, or retrieve it if it already exists.

## ⚠️ The Problem: Collisions
Because the array size is finite and the universe of possible keys is infinite, the Pigeonhole Principle dictates that eventually, two different keys will hash to the same index. This is called a **Collision**.

### Collision Resolution Strategies
1. **Chaining (Java's approach)**: Each bucket in the array holds a linked list (or tree) of entries. If a collision occurs, the new entry is simply appended to the list at that bucket.
2. **Open Addressing / Probing**: If a bucket is full, probe for the next available empty bucket (linear, quadratic, or double hashing).

## 🔄 Load Factor & Resizing
As the map fills up, the chains get longer, degrading performance from O(1) towards O(n). To prevent this, the HashMap monitors its **Load Factor** (Number of Elements / Total Buckets).

When the load factor exceeds a threshold (default 0.75 in Java), the map **resizes** (usually doubling the array size) and **rehashes** all existing elements into the new array. This is an expensive O(n) operation, which is why setting an appropriate initial capacity is crucial for performance.