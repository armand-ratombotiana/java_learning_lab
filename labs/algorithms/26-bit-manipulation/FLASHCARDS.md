# Flashcards — Bit Manipulation

Q: How to check if x is a power of 2?
A: x > 0 && (x & (x-1)) == 0

Q: Kernighan's population count method?
A: while (x != 0) { x &= x - 1; count++; }

Q: Gray code of binary B?
A: G = B ^ (B >> 1)

Q: Lowest set bit isolation?
A: x & (-x)

Q: Bit DP for TSP complexity?
A: O(n^2 * 2^n)

Q: XOR basis uses which linear algebra?
A: Gaussian elimination over GF(2)

Q: Submask enumeration pattern?
A: for (int sub = mask; sub > 0; sub = (sub-1) & mask)

Q: Next power of two?
A: Decrement, fill right with 1s, increment