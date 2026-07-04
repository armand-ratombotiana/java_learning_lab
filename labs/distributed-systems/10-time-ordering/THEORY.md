# Time and Ordering: Theory

## Clock Types

### Physical Clocks (Wall Clocks)
- System.currentTimeMillis()
- NTP-synchronized
- Subject to drift and leap seconds

### Logical Clocks
- Count events, not physical time
- Lamport clocks: single counter
- Vector clocks: array of counters

### Hybrid Logical Clocks (HLC)
- Combine physical + logical components
- Bounded by physical time
- Can track causality like vector clocks

## Causality

### Happens-Before (→)
Event a happens-before event b if:
1. a and b are on same process, a occurs before b
2. a is sending a message, b is receiving it
3. There exists transitive closure of 1 and 2

### Concurrent Events
Events neither causally related (a ↛ b and b ↛ a)

## Ordering Guarantees
- FIFO: Messages from same sender in order
- Causal: Causally related messages in order
- Total: All messages in same order
