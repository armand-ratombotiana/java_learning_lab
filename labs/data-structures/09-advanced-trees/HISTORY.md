# History: Advanced Trees

## BST (1960)
- **1960**: Binary search trees formalized by **Hibbard**
- **1962**: Algorithm for BST deletion published

## AVL Tree (1962)
- **1962**: **Georgy Adelson-Velsky** and **Evgenii Landis** published "An Algorithm for the Organization of Information"
- First self-balancing BST with height difference ≤ 1
- Named "AVL" after the authors' initials
- Still one of the most strictly balanced trees

## Red-Black Tree (1972–1978)
- **1972**: **Rudolf Bayer** invented "symmetric binary B-trees" — a way to represent 2-3-4 trees as binary trees
- **1978**: **Leonidas Guibas** and **Robert Sedgewick** refined them into "red-black trees" with red/black coloring
- **1990s**: Red-black trees chosen for C++ STL `std::map` and Java `TreeMap`

## B-Tree (1970)
- **1970**: **Rudolf Bayer** and **Edward McCreight** invented B-trees at Boeing
- The name "B-tree" has never been officially defined — possibly "Bayer," "Boeing," or "balanced"
- **1979**: B+ tree variant adds sequential access capability
- **1980s–present**: Standard index structure in relational databases

## Fenwick Tree (1989)
- **1989**: **Peter Fenwick** published "A New Data Structure for Cumulative Frequency Tables"
- Also known as **Binary Indexed Tree (BIT)**
- Elegant alternative to segment trees for prefix sum queries

## Segment Tree (1977–1990s)
- **1977**: **Jon Bentley** introduced segment trees in his paper on computational geometry
- **1990s**: Popularized in competitive programming
- Used for range minimum query, range sum, range max

## Java Implementation Timeline
- **Java 1.2** (1998): `TreeMap` (Red-Black), `TreeSet`
- **Java 8** (2014): HashMap treeification using Red-Black trees for collision chains
- **Java 21** (2023): Continued optimizations in TreeMap
