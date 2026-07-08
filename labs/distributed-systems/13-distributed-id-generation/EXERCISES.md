# Exercises â€” ID Generation

## Exercise 1: Snowflake Generator (Easy)
Implement a basic Snowflake-style ID generator with worker ID and sequence.

## Exercise 2: UUID v7 (Medium)
Implement RFC 9562 UUID v7 generation with timestamp and random components.

## Exercise 3: ULID Generator (Medium)
Implement ULID with Crockford Base32 encoding and monotonicity.

## Exercise 4: Concurrent Test (Easy)
Test all generators with 10 concurrent threads producing 100K IDs each.

## Exercise 5: Clock Rollback Handler (Hard)
Add clock rollback detection and mitigation to Snowflake.

## Exercise 6: Distributed Worker ID Assignment (Hard)
Implement a registry for worker ID assignment using ZooKeeper or database.

## Exercise 7: Performance Benchmark (Medium)
Benchmark all generators: throughput, latency p99, memory.

## Exercise 8: Format Converter (Medium)
Convert between Snowflake, UUID, ULID formats.

## Exercise 9: Custom Epoch Calculator (Easy)
Calculate remaining lifetime for different custom epochs.

## Exercise 10: ID Parser (Medium)
Implement ID parsing with field extraction for each format.
