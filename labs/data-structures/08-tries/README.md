# Lab 08: Tries

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Intermediate-yellow?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-3_4_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Prefix trees for efficient string operations — autocomplete, spell check, IP routing**

</div>

---

## Learning Objectives

- Understand the trie (prefix tree) data structure
- Implement a trie with insert, search, startsWith, and delete
- Understand memory trade-offs (array vs hash map children)
- Apply tries to autocomplete and spell checking
- Implement a compressed trie (radix tree)
- Analyze time complexity independent of string length

## Prerequisites

- Lab 04: Trees (trie is an N-ary tree variant)
- Recursion fundamentals
- Understanding of string operations

## Topics Covered

- Trie structure: root with children per character
- Node representation: array of 26 (for letters) vs HashMap (general)
- Insert: traverse/create nodes, mark terminal, O(L) where L = string length
- Search: traverse nodes, check terminal, O(L)
- Prefix search (startsWith): traverse nodes, O(L)
- Deletion: remove terminal, prune unnecessary branches
- Memory analysis: O(total characters stored) vs O(L × branching factor)
- Compressed trie (radix tree): single-child path compression
- Use cases: autocomplete, dictionary, spell checker, IP routing (CIDR)
- Java implementations: no standard library trie
- Common pitfalls: case sensitivity, non-alphabetic characters, memory explosion

## Exercises

1. Implement a trie with insert, search, and startsWith
2. Implement autocomplete (return all words with a given prefix)
3. Find all words matching a pattern (with wildcard `.`)
4. Implement a compressed trie (radix tree)
5. Find the longest common prefix among an array of strings using a trie

## Estimated Time: 3-4 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*
