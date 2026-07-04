# History: Hash Tables

## Early Concepts (1950s)

- **1953**: **Hans Peter Luhn** (IBM) invented hashing for information retrieval. He proposed using a hash function to locate data in a fixed-size table.
- **1954**: **Gene Amdahl, Elaine Boehme, and Robert O'Brien** at IBM developed hashing for the **IBM 701** — considered the first implementation of hash tables under the name "scatter storage."

## The Naming (1960s)

- **1960**: The term "hash" was first used by **Herbert Hellerman** at IBM in a programming manual. "Hashing" refers to "chopping up" the key to compute an address.
- **1962**: **Robert Morris** published "Scatter Storage Techniques" in the Communications of the ACM — the first major survey of hash table techniques.

## Collision Resolution (1960s–1970s)

- **1963**: **Separate chaining** described by **W.D. Maurer**
- **1964**: **Linear probing** introduced by **Gary D. Knott**
- **1968**: **Quadratic probing** by **F. Corbett**
- **1970**: **Double hashing** by **Gary D. Knott**
- **1977**: **Robin Hood hashing** by **Pedro Celis** — reduces variance in probe lengths

## Cryptography (1970s–1990s)

- **1976**: Cryptography hash functions by Diffie and Hellman
- **1992**: **MD5** (Rivest) — widely used, later broken
- **1995**: **SHA-1** (NSA)
- **2000s**: **SHA-2**, **SHA-3** family

## Java HashMap Evolution

- **Java 1.2** (1998): `java.util.HashMap` with separate chaining
- **Java 4** (2002): Performance improvements
- **Java 8** (2014): **Treeify threshold** = 8 — long chains become balanced trees (HashDoS mitigation)
- **Java 21** (2023): Further performance optimizations

## Modern Developments

- **Cuckoo hashing** (2001): multiple hash functions, guaranteed O(1) worst-case lookup
- **Concurrent hash maps**: lock-striping (Java 7), lock-free (Java 8+)
- **Consistent hashing**: distributed hash tables (DHTs) for scalable storage
