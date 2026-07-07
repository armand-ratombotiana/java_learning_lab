# Bit Manipulation — Code Deep Dive

The BitTricks.java file is organized as a utility class with a private constructor and static methods. Each method targets a specific bit operation. The code avoids branching where possible, using only bitwise expressions.

The isPowerOfTwo method: return x > 0 && (x & (x - 1)) == 0. The early check for x <= 0 handles the edge case where x=0 (0 & -1 = 0, incorrectly returning true) and negative numbers (which don't count in unsigned context).

The countBits method uses Brian Kernighan's technique. The loop while (x != 0) { x &= x - 1; count++; }. Each iteration clears one set bit. The worst case (x = -1) requires 32 iterations. Java's Integer.bitCount uses a more efficient divide-and-conquer approach with 5 operations regardless of input.

The reverseBits method uses the standard swapping technique with masks. For 32 bits: swap 1-bit pairs, swap 2-bit pairs, swap 4-bit nibbles, swap 8-bit bytes, swap 16-bit halves. Each step uses shifting and masking.

The nextPowerOfTwo method: if (x <= 1) return 1; x--; x |= x >> 1; x |= x >> 2; x |= x >> 4; x |= x >> 8; x |= x >> 16; return x + 1. The decrement first reduces x to the next lower power of two, then right-shifts propagate the highest set bit down, and incrementing gives the next power of two.

The XorBasis class maintains an array basis[31] (for 32-bit integers). The insert method: for (int i = 31; i >= 0; i--) if ((x & 1<<i) != 0) { if (basis[i] == 0) { basis[i] = x; return; } x ^= basis[i]; }. The queryMax method: compute max XOR of any subset by iterating basis from high to low and checking if XORing increases value.

The BitDpTsp class uses arrays of size [1<<n][n]. The outer loop iterates masks from 1 to (1<<n)-1. Inner loops iterate over possible last and next cities. Java 2D array access is fast enough for n up to 20.