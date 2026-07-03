# Lab 10: Bloom Filters

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Advanced-orange?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-2_3_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Probabilistic data structures — space-efficient membership testing with tunable false positives**

</div>

---

## Learning Objectives

- Understand the Bloom filter probabilistic data structure
- Implement a Bloom filter with configurable size and hash count
- Analyze false positive probability and how to tune it
- Understand hash functions for Bloom filters (MurmurHash, FNV)
- Learn use cases: caching, spell check, spam filtering, databases
- Explore variants: counting Bloom filter, Cuckoo filter, Scalable Bloom filter

## Prerequisites

- Lab 06: Hash Tables (hash functions and collision)
- Basic probability and statistics understanding
- Bit-level operations in Java

## Topics Covered

- Bloom filter concept: m-bit array + k hash functions
- Insert: compute k hashes, set k bits to 1
- Query: compute k hashes, check all k bits set
- False positives vs false negatives: no false negatives, tunable false positives
- False positive probability: (1 - e^(-kn/m))^k
- Optimal k: (m/n) × ln(2)
- Choosing m and k based on n and desired FP rate
- Hash function quality: uniform distribution, speed
- Counting Bloom filter: 4-bit counters for delete support
- Cuckoo filter: higher density, supports deletion
- Big O: insert O(k), query O(k), space O(m)
- Java Guava `BloomFilter` class
- Common pitfalls: too few hash functions, overflow of bit index, poor hash distribution

## Exercises

1. Implement a Bloom filter with configurable size and k hash functions
2. Calculate false positive probability for given parameters
3. Implement a counting Bloom filter with delete support
4. Compare theoretical vs measured false positive rate
5. Use a Bloom filter to avoid cache misses (caching layer pattern)

## Estimated Time: 2-3 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*
