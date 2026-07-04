# Distributed ID Generation: Interview Questions

## Q1: How would you design a distributed ID generator?
**A**: Use Snowflake-style: timestamp + worker ID + sequence. 64-bit, millisecond precision, 4096 IDs per second per worker. Handle clock drift with waits. Allocate worker IDs via ZooKeeper.

## Q2: What are the alternatives to Snowflake?
**A**: UUID v4 (random, not sorted), UUID v7 (sorted, 128-bit), DB sequences (centralized bottleneck), Hi/Lo (batched sequences).

## Q3: How do you handle clock drift?
**A**: Detect backwards clock and wait, throw exception if drift exceeds threshold, use logical clocks (HLC), or register sequence advancement in ZooKeeper.

## Q4: How many unique IDs do you need?
**A**: Snowflake: 4K/ms × 1024 workers = 4M/second, continuously for 69 years. UUID v4: 2¹²² total (virtually infinite).

## Q5: How do you make Snowflake IDs URL-safe?
**A**: Base62 encode the long value. URL-safe, shorter representation. Can decode back to long for storage.
