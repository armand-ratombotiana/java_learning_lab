# History: Trees

## Early Tree Structures (1950s–1960s)

- **1956**: Decision trees used in early AI (Arthur Samuel's checkers program)
- **1960**: Binary search trees described by **Hibbard** (ACM 60)
- **1962**: **Adelson-Velsky and Landis** invented the **AVL tree** — first self-balancing binary search tree
- **1964**: **Binary heaps** and **heapsort** by J.W.J. Williams

## B-Trees (1970s)

- **1970**: **Rudolf Bayer and Edward McCreight** invented **B-trees** at Boeing for database indexing
- **1979**: **B+ trees** variation for range queries and sequential access
- **1980s**: B-trees became the standard index structure in relational databases (Oracle, DB2)

## Red-Black Trees (1970s–1980s)

- **1972**: **Rudolf Bayer** invented "symmetric binary B-trees" — the precursor to red-black trees
- **1978**: **Leonidas Guibas and Robert Sedgewick** formalized red-black trees
- **1990s**: Red-black trees chosen for `std::map` (C++) and `java.util.TreeMap`

## Modern Era (1990s–present)

- **1990**: AVL trees used extensively in memory-constrained environments
- **1993**: B-trees remain dominant in databases; LSM-trees gain popularity (Bigtable, LevelDB)
- **2000s**: Segment trees for range queries in competitive programming
- **2010s**: Fenwick trees (binary indexed trees) widely adopted
- **Java 8** (2014): HashMap chaining switches to balanced trees at threshold 8

Trees remain an area of active research: cache-oblivious B-trees, succinct tree representations, and learned index structures (B-Tree alternatives using ML).
