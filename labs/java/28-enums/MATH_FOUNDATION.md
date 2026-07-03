# Mathematical Foundation: Enums

## Set Theory
An enum defines a finite set of constants E = {e₀, e₁, ..., eₙ₋₁}. This set is:
- Complete: All possible values are known at compile time
- Finite: |E| = n, where n is the number of constants
- Immutable: No new constants can be added at runtime

## EnumSet Bit Operations
For an enum with n ≤ 64 constants, EnumSet stores constants as bits in a 64-bit long:
```
bitmask = Σ 2ᵒʳᵈⁱⁿᵃˡ⁽ᵉ⁾ for each constant e in the set
```

### Operations
| Operation | Implementation | Complexity |
|-----------|---------------|------------|
| add(e) | bitmask \|= (1L << ordinal(e)) | O(1) |
| remove(e) | bitmask &= ~(1L << ordinal(e)) | O(1) |
| contains(e) | (bitmask & (1L << ordinal(e))) != 0 | O(1) |
| size() | Long.bitCount(bitmask) | O(1) |
| union(a, b) | a.bitmask \| b.bitmask | O(1) |
| intersect(a, b) | a.bitmask & b.bitmask | O(1) |
| complement(s) | ~s.bitmask & fullMask | O(1) |

For enums with n > 64, EnumSet uses a long[] array, with operations scaling as O(n/64).

## EnumMap Array Indexing
EnumMap maps enum constants to values using an array indexed by ordinal:
```
values[ordinal(e)] = associated value
```
This gives O(1) get/put with no hashing overhead, purely array indexing.

## Probability
If you randomly select an enum constant from a set of n, the probability of any specific constant is 1/n (assuming uniform distribution). This is useful for switch statement optimization — the JVM uses `tableswitch` (O(1) jump table) when ordinals are dense, `lookupswitch` (binary search) when sparse.

## Graph Theory
Enum constants can form state machine nodes where transitions are edges. Each state is a constant, and transitions are methods on the enum. The state machine graph has n nodes (states) and up to n² possible transitions.
