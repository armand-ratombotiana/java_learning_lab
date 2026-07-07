# Bit Manipulation — Common Mistakes

1. Forgetting to handle x <= 0 in isPowerOfTwo: (x & (x-1)) == 0 is true for x=0 and x=-2147483648 (Integer.MIN_VALUE). Always check x > 0 first.

2. Using signed right shift (>>) instead of unsigned (>>>) when treating integers as bit arrays. Signed shift preserves the sign bit, which corrupts bit manipulation routines.

3. Assuming 1 << i works for i >= 31: 1 << 31 is Integer.MIN_VALUE (negative), and 1 << 32 wraps to 1. Use 1L << i for long arithmetic or mask with (1L << i) & 0xffffffffL.

4. Infinite loop in population count when x is negative: while (x > 0) { x &= x - 1; } would never execute for negative x. Use while (x != 0).

5. Off-by-one in Gray code conversion: the binary-to-Gray formula is correct, but Gray-to-binary requires enough iterations for all bits.

6. XOR basis not handling zero input: inserting 0 should be a no-op, as 0 does not change the basis. The empty set can represent zero.

7. TSP DP using Integer.MAX_VALUE as INF leads to overflow when adding edge weights. Use a large but safe sentinel like Integer.MAX_VALUE / 2.

8. Bit reversal on 32-bit: forgetting to handle all 5 swapping steps. Missing any step results in incomplete reversal.