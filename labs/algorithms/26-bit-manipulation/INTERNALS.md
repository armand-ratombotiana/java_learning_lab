# Bit Manipulation — Internal Implementation Details

The BitTricks class is implemented as a utility with private constructor, following the Java pattern for static method classes. All methods operate on 32-bit integers (int) and 64-bit (long) where noted.

Key internal details:
- isPowerOfTwo: uses (x & (x-1)) == 0. This works because powers of two have exactly one set bit; subtracting 1 flips all lower bits, making the AND zero. The special case x <= 0 returns false since 0 and negative numbers are not powers of two.
- countBits (Kernighan): while (x != 0) { x &= (x - 1); count++; }. This clears the lowest set bit by subtracting 1 (which flips the trailing zeros to 1 and the lowest 1 to 0) then ANDing. Each iteration removes one set bit.
- reverseBits: uses divide-and-conquer with predefined masks. Step 1: swap adjacent bits (mask 0x55555555). Step 2: swap adjacent pairs (0x33333333). Step 3: swap nibbles (0x0f0f0f0f). Step 4: swap bytes (0x00ff00ff). Step 5: swap 16-bit halves (0x0000ffff). Each mask isolates the bits to be swapped.
- nextPowerOfTwo: if (x <= 1) return 1; x--; propagate highest set bit down: x |= x >> 1; x |= x >> 2; x |= x >> 4; x |= x >> 8; x |= x >> 16; return x + 1. The decrement handles exact powers of two. The right shifts fill all lower bits with 1, so adding 1 wraps to the next power of two.
- The GrayCode class holds static methods for binary-to-Gray and Gray-to-binary conversion using XOR operations. The TSP bit DP uses a 2D array of size [1<<n][n], which for n=20 requires about 80 MB (20 * 2^20 * 4 bytes).