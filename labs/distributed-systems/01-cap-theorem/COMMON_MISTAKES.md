# Common Mistakes with CAP Theorem

## 1. Misinterpreting "Choose 2 of 3"
CAP doesn't mean you always have three options. During a partition, you choose between CP and AP.

## 2. Treating Consistency as Binary
Consistency exists on a spectrum from strong to eventual. CAP focuses on strong consistency.

## 3. Ignoring the "During Partition" Qualification
CAP only forces tradeoffs during network partitions, not during normal operation.

## 4. Assuming CA Systems Are Possible
In a distributed system, partitions are inevitable. CA is only achievable in single-node systems.

## 5. Confusing CAP with ACID
CAP consistency (all nodes see same data) differs from ACID consistency (transactions maintain database invariants).
