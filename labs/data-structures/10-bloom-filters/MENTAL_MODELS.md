# Mental Models for Bloom Filters

## The Guest List at a Party

You're at a party and want to check if someone is on the VIP list. You have a **paper with k checkboxes** for each person. When someone says they're VIP, you check off k boxes. When someone arrives, you look at their k boxes. If any box is unchecked → definitely not VIP. If all checked → probably VIP (but could be a false positive if others happened to check those same boxes).

## The Coffee Cup Sleeve

A coffee shop has a stamp card. Each stamp represents a hash function. When you buy a coffee, they stamp k random positions. To check if you've been there, they check if you have stamps at all k positions. If a position is unstamped → definitely new customer. If all stamped → likely a returning customer.

## The Musical Chairs (Bits)

Imagine m chairs (bits) arranged in a row. When an element arrives, k of its friends sit on k specific chairs. When you query, you check those k chairs. If any chair is empty → the element never arrived. If all chairs are occupied → the element probably arrived (or all k chairs happened to be occupied by other elements).

## The Voting Booth

Each element gets k votes (hash functions) that mark k yes/no boxes. When querying, if any box is "no" → element definitely not registered. If all "yes" → likely registered, but those k boxes could be "yes" due to other elements' votes.

## Memory vs Accuracy Trade-off

```
10 bits/element → 1% false positive
  This means: for 1000 queries about non-existent elements,
  ~10 will incorrectly say "maybe yes"

20 bits/element → 0.01% false positive
5 bits/element → 10% false positive
```

## Guava Model

```java
// Guava creates:
// m bits = -n × ln(P) / (ln(2))²
// k = (m/n) × ln(2) hash functions

BloomFilter<String> filter = BloomFilter.create(
    Funnels.stringFunnel(Charsets.UTF_8),
    expectedInsertions,   // n
    falsePositiveRate     // P
);
```
