# Bit Manipulation — Visual Guide

## Binary Representation

Decimal 42: 32 + 8 + 2 = 00101010 (8 bits). Bits from MSB to LSB: position 7 (32), position 3 (8), position 1 (2). Visual: [0][0][1][0][1][0][1][0].

## Lowest Set Bit Isolation

x = 40 (00101000), -x = 11011000 (two's complement), x & -x = 00001000 (only the lowest set bit). This isolates bit 3 (value 8).

## Power of Two Check

x = 16 (00010000), x-1 = 00001111, x & (x-1) = 00000000. Since there is exactly one set bit, subtracting 1 converts it to a string of 1s below that position, making the AND zero.

## Brian Kernighan's Count

x = 42 (00101010). Iterations: (1) x = 42 & 41 = 00101010 & 00101001 = 00101000 = 40, count=1. (2) x = 40 & 39 = 00101000 & 00100111 = 00100000 = 32, count=2. (3) x = 32 & 31 = 00100000 & 00011111 = 0, count=3. Final: 3 set bits.

## Gray Code Sequence (3-bit)

Binary: 000, 001, 010, 011, 100, 101, 110, 111. Gray: 000, 001, 011, 010, 110, 111, 101, 100. Each transition flips exactly one bit. Binary-to-Gray: G = B ^ (B >> 1). Example: 101 -> 101 ^ 010 = 111.

## Subset Enumeration

For a set {a, b, c}, masks: 000={}, 001={c}, 010={b}, 011={b,c}, 100={a}, 101={a,c}, 110={a,b}, 111={a,b,c}. Submask enumeration of mask 101: sub=101, then sub=(101-1)&101=100, then 011&101=001, then 000. Iterates: {a,c}, {a}, {c}, {}.

## TSP Bit DP Table

2D array dp[mask][v]: rows = 2^n masks, cols = n cities. dp[1][0]=0 (start at city 0). For mask=3 (011, visited {0,1}), dp[3][1]=dist[0][1]. Final answer: min over v of dp[2^n-1][v] + dist[v][0].