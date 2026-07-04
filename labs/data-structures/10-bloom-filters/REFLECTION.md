# Reflection: Bloom Filters

## What I Learned

- Bloom filters are probabilistic data structures with no false negatives and tunable false positives
- They use m bits and k hash functions to represent a set without storing elements
- False positive probability depends on m, n, k — optimal k = (m/n) × ln(2)
- Counting Bloom filters extend the concept to support deletion using counters
- Guava provides a production-ready BloomFilter implementation
- Bloom filters are widely used in databases, caches, web crawlers, and blockchain

## Questions to Consider

1. Why can't standard Bloom filters support deletion? (Removing an element would risk false negatives for other elements sharing those bits)
2. When is a Bloom filter better than a hash set? (Large sets where 1% false positives are acceptable to save 10x memory)
3. How does the false positive rate change as the filter fills up? (It increases exponentially as more bits become 1)
4. What are the privacy implications of Bloom filters? (The bit pattern leaks information about the set contents)
5. How would you implement a Bloom filter for sets that grow unpredictably? (Scalable Bloom filter — chain of filters with increasing capacity)

## Connections

Bloom filters connect to:
- **Hash tables** (Bloom filters use hash functions but give up exact membership for space)
- **Hash functions** (quality of hash functions directly affects FPP)
- **Probability theory** (the mathematical foundation for FPP calculation)
- **Databases** (Cassandra, PostgreSQL, Bigtable use Bloom filters)
- **Network security** (malicious URL checking, packet classification)
- **System design** (caching, deduplication, spell checking)

## Self-Assessment

- [ ] Can implement a Bloom filter from scratch
- [ ] Understand false positive probability and parameter calculation
- [ ] Can implement a Counting Bloom filter
- [ ] Understand the Bloom filter's role in system design
- [ ] Can use Guava's BloomFilter in practice
- [ ] Know when to use and when NOT to use a Bloom filter
