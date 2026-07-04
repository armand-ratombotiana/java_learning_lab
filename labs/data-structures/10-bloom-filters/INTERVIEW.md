# Interview Questions: Bloom Filters

## Conceptual

1. **Explain a Bloom filter** — Describe the data structure, its operations, and guarantees.

2. **False positives vs false negatives** — Which one does a Bloom filter allow, and why?

3. **Parameter tuning** — Given n=10K desired P=1%, calculate m and k.

4. **Real-world use cases** — Where are Bloom filters used in production?

## System Design

5. **Design a cache with Bloom filter** — How would you use a Bloom filter to reduce cache misses and database load?

6. **Design a web crawler** — How would you avoid revisiting billions of URLs with limited memory?

7. **Design a spell checker** — How would you check if a word is in a dictionary of 1M words with minimal memory?

8. **Design Bitcoin SPV** — How does a lightweight Bitcoin wallet use Bloom filters for transaction filtering?

## Implementation

9. **Implement a Bloom filter** — Write a Bloom filter class with put, mightContain, and parameter calculation.

10. **Counting Bloom filter** — How would you modify a Bloom filter to support deletion?

11. **Merge two Bloom filters** — How would you combine two filters that were created with the same parameters?

## Analysis

12. **FPP vs fill rate** — Explain how the false positive rate changes as more elements are added.

13. **Bloom filter vs HashSet** — Compare space, time, and accuracy for membership testing.

14. **Scalable Bloom filter** — How would you handle the case where the expected number of elements is unknown?

## Key Points

- Guava's `BloomFilter` is the standard Java implementation
- Always verify "maybe" results with exact storage (Bloom filter is an optimization, not a replacement)
- Never use for security-critical membership decisions (no false negative guarantee doesn't mean safe)
- Parameters must be chosen based on expected n and acceptable P
- Overfilling degrades performance gracefully (unlike hash tables which can become unusable)

## Java-Specific Topics

- `com.google.common.hash.BloomFilter` — how to create and use
- `Funnels` — for funneling different types into the filter
- `BitSet` — Java's bit-level data structure
- Serialization of Bloom filters (for distributed systems)
- Thread safety considerations (not thread-safe, use external sync)
