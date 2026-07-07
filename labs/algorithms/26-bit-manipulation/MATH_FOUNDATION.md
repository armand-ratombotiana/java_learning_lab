# Bit Manipulation — Mathematical Foundation

## Binary Number System

A binary number is a sequence of bits b_{k-1} ... b_0 where value = sum_{i=0}^{k-1} b_i * 2^i. Two's complement represents negative numbers: -x = ~x + 1. The most significant bit is the sign bit. Range for n-bit signed: -2^{n-1} to 2^{n-1} - 1.

## Properties of Bitwise Operations

- x & 0 = 0, x | 0 = x, x ^ 0 = x
- x & (-x) = lowest set bit (trapping)
- x & (x - 1) clears the lowest set bit
- x | (x + 1) sets the lowest zero bit
- x ^ (x - 1) produces a mask of trailing zeros plus the lowest set bit
- x ^ (x + 1) produces a mask of trailing ones plus the lowest zero bit
- ~x = -x - 1 in two's complement

## Gray Code Mathematics

Gray code of n: G(n) = n ^ (n >> 1). This gives a sequence where adjacent values differ by exactly one bit. Proof: G(n) and G(n+1) differ by the position of the rightmost set bit in n+1.

## XOR Basis over GF(2)

A set of vectors over GF(2) forms a vector space. The XOR basis is a set of linearly independent vectors that span the space. The dimension d determines that there are 2^d distinct XOR combinations. Gaussian elimination over GF(2) computes the basis in O(n * log MAX). The maximum XOR subset sum problem reduces to finding the maximum value representable by the basis: for each basis vector from highest bit, if XORing increases the current value, do it.

## Bit DP Complexity

For TSP: number of states = n * 2^n. Each state transition is O(1). Total: O(n^2 * 2^n). For n = 20: 20^2 * 2^20 = 400 * 1,048,576 ≈ 420 million operations, feasible in Java with optimization. For n = 25: 25^2 * 2^25 = 625 * 33,554,432 ≈ 21 billion, requiring pruning or better algorithms.