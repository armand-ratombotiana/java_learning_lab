# Lab 06: Hash Tables

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Intermediate-yellow?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-3_4_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Key-value storage with near-constant-time operations**

</div>

---

## Learning Objectives

- Understand hash functions and their properties
- Implement hash tables with separate chaining and open addressing
- Master collision resolution strategies
- Understand load factor and dynamic resizing (rehashing)
- Analyze amortized time complexity of hash table operations
- Understand Java's `HashMap` and `HashSet` internals

## Prerequisites

- Lab 01: Arrays
- Understanding of `hashCode()` and `equals()` in Java

## Topics Covered

- Hash functions: properties, good hash distribution
- Modulo compression and bit-shifting tricks
- Collision resolution: separate chaining (linked lists, trees)
- Collision resolution: open addressing (linear probing, quadratic probing, double hashing)
- Load factor and rehashing (capacity doubling)
- Amortized analysis: O(1) average for put/get/remove
- Worst case: O(n) with poor hash distribution
- Java `HashMap` internals: capacity, threshold, treeify threshold
- `hashCode()` contract with `equals()`
- Cryptographic hashing (brief intro)
- Common pitfalls: mutable keys, weak hashCode, excessive collisions

## Exercises

1. Implement a hash table with separate chaining
2. Implement linear probing hash table
3. Implement a custom `hashCode()` for a composite object
4. Find the first non-repeating character in a string
5. Implement a LRU cache using HashMap + doubly linked list

## Estimated Time: 3-4 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*
