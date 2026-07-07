# How Bit Manipulation Works

## Building a Bit Tricks Class

The BitTricks class provides static utility methods for common bit operations. The key insight is that each operation exploits properties of two's complement representation.

To check if x is a power of two: x > 0 && (x & (x-1)) == 0. When x is a power of two, it has exactly one bit set. Subtracting 1 flips that bit and all lower bits to 1. The AND of x and x-1 is zero because they have no 1 bits in common.

To count bits (population count), Brian Kernighan's algorithm repeatedly clears the lowest set bit: for (count = 0; x != 0; count++) x &= x - 1. Each iteration removes one set bit. This runs in O(number of set bits) time.

To reverse bits, use divide-and-conquer masks: swap adjacent bits, swap adjacent pairs, swap nibbles, etc. Each step doubles the block size of the swap.

To find the next power of two: decrement, shift right and OR repeatedly to fill all lower bits with 1, then increment. For example, 5 (0101) -> 0100 -> 0111 -> 1000 = 8.

## Gray Code Generation

Binary to Gray: gray = binary ^ (binary >> 1). Gray to binary: for (mask = gray >> 1; mask != 0; mask >>= 1) gray ^= mask. The nth Gray code can be generated directly: n ^ (n >> 1).

## XOR Basis Construction

Initialize an array basis[32] (or [64]) to zeros. For each vector x, iterate from highest bit (31 down to 0). If the i-th bit of x is set and basis[i] is zero, set basis[i] = x and break. Otherwise, XOR x with basis[i] (x ^= basis[i]). This builds an upper-triangular basis.

## TSP Bit DP

Initialize dp[1<<start][start] = 0, others = INF. For each mask from 1 to (1<<n)-1, for each v in mask, for each u not in mask, update dp[mask|1<<u][u] = min(dp[mask|1<<u][u], dp[mask][v] + dist[v][u]). Answer is min over v of dp[(1<<n)-1][v] + dist[v][start] for Hamiltonian cycle.