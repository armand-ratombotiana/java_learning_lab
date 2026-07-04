# Common Mistakes with Distributed ID Generation

## 1. Clock Drift
System clock going backwards causes duplicate IDs. Solution: report errors, wait, or use HLC.

## 2. Insufficient Sequence Bits
Underestimating max IDs per millisecond causes blocking. Monitor ID generation rate.

## 3. Worker ID Mismanagement
Duplicate worker IDs cause ID collisions. Use ZooKeeper or etcd for allocation.

## 4. UUID Performance
UUID v4 random generation is slower than Snowflake. Bench overhead per operation.

## 5. Database-Backed Sequences
Database becomes bottleneck for ID generation. Use batch allocation (Hi/Lo).

## 6. Readable IDs
Sequential IDs leak information (order volume, growth rate). Use opaque Snowflake IDs.
