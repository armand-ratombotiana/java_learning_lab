# Interview Questions: Bit Manipulation

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 136 Single Number | Easy | Google, Meta, Amazon, Microsoft | XOR |
| LC 268 Missing Number | Easy | Google, Meta, Amazon, Microsoft | XOR / sum |
| LC 191 Number of 1 Bits | Easy | Google, Meta, Apple | Brian Kernighan's |
| LC 338 Counting Bits | Easy | Google, Meta, Amazon, Apple | DP + LSB |
| LC 190 Reverse Bits | Easy | Google, Meta, Apple | Bit by bit |
| LC 260 Single Number III | Medium | Google | XOR + mask |
| LC 137 Single Number II | Medium | Google | Bit counting / finite state |
| LC 371 Sum of Two Integers | Medium | Google, Meta, Amazon, Microsoft | Bitwise add |
| LC 421 Maximum XOR of Two Numbers | Medium | Google | Trie / bit prefix |

## NeetCode Reference
- LC 136 Single Number (NeetCode 150)
- LC 191 Number of 1 Bits (NeetCode 150)
- LC 268 Missing Number (NeetCode 150)
- LC 338 Counting Bits (NeetCode 150)
- LC 190 Reverse Bits (NeetCode 150)
- LC 371 Sum of Two Integers (NeetCode 150)
- LC 421 Maximum XOR of Two Numbers (NeetCode 150)

## Company-Specific Questions
### Google
- Maximum XOR of Two Numbers (Trie-based) is a Google signature problem
- Single Number series with follow-ups (II, III) testing bit pattern recognition
- Expect bit manipulation problems that require O(1) space and O(n) time

### Microsoft
- Reverse Bits and Number of 1 Bits are Microsoft favorites
- How would you implement a bit array for memory-efficient storage?
- Bit manipulation for low-level systems programming

### Meta
- Counting Bits with DP optimization is a Meta staple
- Sum of Two Integers without + or - operator
- Focus on using bit operations to replace arithmetic

### Amazon
- Single Number for detecting unique items in logs
- Missing Number for sequence integrity checks
- Bit manipulation for compression algorithms

### Apple
- Reverse Bits and Number of 1 Bits are Apple favorites
- How would you implement a Bloom filter with bit arrays?
- Memory-efficient algorithms using bit manipulation (Apple Watch constraints)

### Oracle
- How does Oracle use bitmap indexes for performance?
- Design a bit-level compression scheme for database storage
- Explain Oracle's bitmap join index implementation

## Real Production Scenarios
- Scenario 1: Low-latency logging - using bit packing to store 100 million event flags in a single 64-bit integer for real-time analytics with minimal memory overhead
- Scenario 2: Permission system - implementing a role-based access control using bitmask operations for O(1) permission checks across thousands of resource types
- Scenario 3: Network packet header parsing - debugging a bit-level protocol parser that fails due to endianness differences between network byte order and host byte order

## Interview Tips
- Know the common bit tricks: n & (n-1) clears LSB, n & -n isolates LSB, XOR is addition without carry
- For counting bits: Brian Kernighan's algorithm runs in O(number of set bits)
- Bit manipulation can often replace arithmetic: x/2 = x>>1, x%2 = x&1, x*2 = x<<1
- Common edge cases: negative numbers (>> vs >>>), Integer.MAX_VALUE, overflow

## Java-Specific Considerations
- Java uses two's complement for signed integers; `>>>` for unsigned right shift, `>>` for arithmetic
- `Integer.bitCount(i)` for population count; `Integer.highestOneBit(i)`, `Integer.lowestOneBit(i)`
- `Integer.numberOfLeadingZeros(i)` and `Integer.numberOfTrailingZeros(i)` for log-based operations
- `BitSet` class for arbitrary-length bit arrays with get/set/and/or/xor operations
- Pitfall: `1 << 31` overflows for int (use `1L << 31` or use unsigned)
- Pitfall: `int i = 0x80000000` is negative in Java; `Math.abs(Integer.MIN_VALUE)` = Integer.MIN_VALUE
- For XOR-based solutions: a ^ a = 0, a ^ 0 = a, XOR is commutative and associative
