# Collections Deep - Micro-Labs

## Overview
This module contains 10 atomic deep-dive micro-labs.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [HashMap Internals](./01-hashmap-internals/) | Bucket array, hash function, put/get/resize logic, load factor, treeification, keySet/values/entrySet views |
| 02 | [ConcurrentHashMap](./02-concurrent-hashmap/) | Segmented locking (pre-8), CAS-based design (8+), red-black tree bin, transfer queue, size calculation |
| 03 | [LinkedList Deep Dive](./03-linked-list-deep/) | Node structure, doubly-linked, insertion/deletion cost, random access O(n), ArrayList vs LinkedList benchmarks |
| 04 | [ArrayList Deep Dive](./04-arraylist-deep/) | Dynamic array, grow strategy (1.5x), System.arraycopy, trimToSize, subList view, fail-fast vs fail-safe |
| 05 | [TreeMap & TreeSet](./05-treemap-treeset/) | Red-black tree properties, rotations, self-balancing, comparator, subMap/headMap/tailMap views |
| 06 | [PriorityQueue](./06-priority-queue/) | Binary heap, siftUp/siftDown, heapify O(n), comparator-based ordering, priority inversion |
| 07 | [Build Your Own HashMap](./07-hashmap-design-build/) | Build your own HashMap from scratch with open addressing, load factor tuning, benchmark vs java.util |
| 08 | [ConcurrentLinkedQueue](./08-concurrent-linked-queue/) | Michael-Scott non-blocking queue, CAS head/tail, ABA problem, dummy node |
| 09 | [CopyOnWriteArrayList](./09-copyonwrite-arraylist/) | Snapshot iterator, copy-on-write semantics, read vs write throughput, memory overhead |
| 10 | [Bloom Filter](./10-bloom-filter/) | Bit array, hash functions, false positive probability, optimal size calculator, counting Bloom filter |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code,
JUnit 5 tests, and 7 subdirectories for hands-on work.
Start from lab 01 and progress sequentially.