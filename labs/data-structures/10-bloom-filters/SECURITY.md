# Security Considerations for Bloom Filters

## Denial of Service

### False Positive Bombing

An attacker can craft elements that share hash positions with legitimate elements:
```java
// Attacker sends many elements that hash to same positions as legitimate keys
for (int i = 0; i < 100000; i++) {
    filter.put(attackerCraftedElement);  // fills up the filter
}
// Result: legitimate queries now get false positives → cache misses → backend overload
```

### Bloom Filter Exhaustion

```java
// Attacker inserts huge number of elements
for (int i = 0; i < expectedInsertions * 10; i++) {
    filter.put("attacker-" + i);  // fill beyond capacity
}
// FPP skyrockets → filter becomes useless
```

## Information Leakage

Bloom filters can leak information about the elements they contain:
- A Bloom filter for "blocked websites" reveals which sites are blocked
- The bit array pattern can be analyzed to infer approximate content
- An attacker who can query the filter repeatedly can learn about its contents

### Side-Channel Attack

If the Bloom filter is used as a privacy-preserving membership check:
- An attacker can probe the filter with different elements
- By observing true/false responses, they can deduce which elements might be in the set
- With enough queries, they can estimate the set contents

## Privacy in Bitcoin SPV

Bitcoin's Simplified Payment Verification uses Bloom filters:
- A lightweight wallet sends a Bloom filter to a full node
- The full node returns transactions matching the filter
- The filter _leaks information_ about which addresses the wallet is interested in
- Privacy risk: the node can infer wallet contents from the filter pattern

### Mitigations

- Use **Rindspot** (Randomized Bloom filter) — adds fake elements to obscure patterns
- Use **Cuckoo filter** instead — better privacy characteristics
- Limit the number of queries an untrusted party can make
- Use differential privacy techniques when publishing Bloom filter contents

## Denial of Service via Hash Computation

```java
// If hash computation is expensive (e.g., on very long strings):
String attackString = "A".repeat(1_000_000);
filter.put(attackString);  // slow hash computation
```

Mitigation: limit input size before hashing.

## Secure Practices

- Do not use Bloom filters for security-critical membership tests
- Rate-limit insertions to prevent overfilling
- Consider privacy implications when publishing Bloom filter state
- Use strong, non-invertible hash functions (MurmurHash, not SHA which is slow)
- Regularly re-create filters to limit information accumulation
