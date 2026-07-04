# Common Mistakes with Time and Ordering

## 1. Using Physical Time for Causality
Wall clocks drift and aren't synchronized. Never use System.currentTimeMillis() for causality.

## 2. Assuming Lamport Clocks Detect Concurrency
Lamport clocks cannot distinguish concurrent from causally related events.

## 3. Vector Clock Bloat
Vector clocks grow with number of processes. Use interval tree clocks for large systems.

## 4. Ignoring Clock Uncertainty
TrueTime-style intervals require waiting; don't assume single timestamps are reliable.

## 5. Not Pruning Vector Clocks
Over time, vector clocks grow unboundedly. Periodically prune stale entries.

## 6. NTP Reliance
NTP can cause clock jumps (forward and backward). Use monotonic clocks for measuring intervals.
