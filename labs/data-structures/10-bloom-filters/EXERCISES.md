# Exercises: Bloom Filters

## Basic

1. **Simple Bloom filter** — Implement a Bloom filter with a fixed bit array and k hash functions. Use built-in `hashCode()` (split into two longs) for simplicity.

2. **Optimal parameter calculator** — Write methods to compute optimal m (bit array size) and k (hash count) given n (expected elements) and P (desired false positive rate).

3. **FPP measurement** — Implement your Bloom filter, insert n elements, then measure actual false positive rate by querying m different non-existent elements.

4. **BitSet Bloom filter** — Implement a Bloom filter using `java.util.BitSet` and Java's `Objects.hash()` for hash generation.

## Intermediate

5. **Counting Bloom filter** — Implement a counting Bloom filter using short[] counters that supports insert, query, and delete.

6. **Scalable Bloom filter** — Implement a scalable Bloom filter that starts with one filter and adds new filters as elements exceed capacity.

7. **Bloom filter for spell checker** — Create a Bloom filter from a dictionary file and use it as a first-pass spell checker.

8. **Cache with Bloom filter** — Implement a cache-aside cache that uses a Bloom filter to avoid lookups for non-existent keys.

9. **Serialization** — Add serialization/deserialization support to your Bloom filter (write bit array to byte[] or file).

## Advanced

10. **Cuckoo filter** — Implement a Cuckoo filter with two hash tables and support for insertion, lookup, and deletion.

11. **Mergeable Bloom filter** — Implement a Bloom filter that supports `putAll()` to combine two filters (OR operation).

12. **Partitioned Bloom filter** — Implement a Bloom filter where each hash function has its own dedicated bit array region.

13. **Bloom filter with hash library** — Use Guava's `BloomFilter` in a real application (e.g., deduplicate a large file of URLs).

14. **False positive analysis** — Write a Monte Carlo simulation to study how actual FPP varies with fill ratio, comparing theoretical vs measured values.
