# Bit Manipulation — Theoretical Foundation

## Bitwise Operations and Binary Representation

Every integer in a computer is stored as a sequence of bits (0 or 1). Bitwise operations operate on these bits in parallel. The fundamental operations are AND (&), OR (|), XOR (^), NOT (~), left shift (&lt;&lt;), and right shift (&gt;&gt;). These execute in a single clock cycle on modern processors, making them extremely efficient.

## Bit Tricks and Common Patterns

The expression (x &amp; (x - 1)) clears the lowest set bit; it is used to check if x is a power of two: (x &amp; (x - 1)) == 0. Population count (number of set bits) can be computed using Brian Kernighan's algorithm: repeatedly clear the lowest set bit until x becomes zero. Trailing zeros are found with (x &amp; -x), which isolates the lowest set bit. Reversing bits can be done with a divide-and-conquer approach using masks.

## Gray Code

Gray code, also known as reflected binary code, is a sequence where successive values differ by exactly one bit. It is constructed recursively: the n-bit Gray code is the (n-1)-bit code prefixed with 0 followed by the reverse of the (n-1)-bit code prefixed with 1. The conversion from binary to Gray is G = B ^ (B &gt;&gt; 1); the reverse conversion uses successive XOR operations.

## XOR Basis (Linear Basis)

A set of binary vectors can be represented by a basis under XOR operations. The XOR basis is a minimal set of vectors such that every vector in the original set can be expressed as a linear combination. Building the basis uses Gaussian elimination over GF(2): for each vector, if its highest set bit is not present in the basis, add it; otherwise, XOR it with the existing basis vector to eliminate that bit.

## Subset Enumeration via Bitmasks

A bitmask of length n represents a subset of an n-element universe. Iterating over all subsets is equivalent to iterating from 0 to 2^n - 1. Enumerating submasks of a given mask can be done efficiently using the trick: for (int sub = mask; sub &gt; 0; sub = (sub - 1) &amp; mask). This iterates only over submasks, skipping non-subset values.

## Bit DP (Traveling Salesman)

The Traveling Salesman Problem is solved with DP over subsets: dp[mask][v] = minimum cost to visit all cities in mask and end at v. The recurrence is dp[mask][v] = min(dp[mask without v][u] + dist[u][v]) for all u in mask. Complexity is O(n^2 * 2^n), which is feasible for n up to about 20. This is the fastest known exact algorithm for TSP.