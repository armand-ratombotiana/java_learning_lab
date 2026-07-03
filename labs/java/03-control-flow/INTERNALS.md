# Control Flow — Internal Mechanics

## Tableswitch vs Lookupswitch

`tableswitch`: O(1) — uses index offset to jump. Requires contiguous case values. The bytecode stores min/max and jump offsets for each value.

`lookupswitch`: O(log n) — binary search on case values. Used when cases are sparse. The bytecode stores key-offset pairs.

## String Switch Implementation

The compiler produces a two-level switch:
1. Hash code switch: `switch (s.hashCode()) { case 99162322: ... }`
2. Inside each hash case, verify with `.equals()`:

```java
case 99162322: // hash of "hello"
    if (s.equals("hello")) { /* hello case */ }
    goto default;
```

## Branch Prediction

Modern JVMs and CPUs use branch prediction. Tight loops with predictable patterns are fast. Random conditions cause branch mispredictions, slowing execution. The JIT compiler may reorder branches based on profiling.

## Loop Optimizations

- **Loop unrolling**: JIT may replicate loop body to reduce branching overhead
- **Loop invariant code motion**: Computations that don't change per iteration are moved out
- **Loop fusion**: Adjacent loops over same range may be combined
- **Peeling**: First/last iteration handled separately for alignment
