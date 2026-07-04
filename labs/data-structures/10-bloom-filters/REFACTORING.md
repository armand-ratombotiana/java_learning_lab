# Refactoring Bloom Filters

## Use Guava BloomFilter Instead of Custom

```java
// Before — custom implementation
class MyBloomFilter {
    BitSet bits = new BitSet();
    int k = 7;
    // ... manual hash, put, mightContain
}

// After — Guava
BloomFilter<String> filter = BloomFilter.create(
    Funnels.stringFunnel(StandardCharsets.UTF_8),
    expectedInsertions, 0.01
);
```

## Extract Parameter Calculation

```java
// Before — inline magic numbers
int m = (int) (-expectedInsertions * Math.log(0.01) / 0.480);
int k = (int) ((double) m / expectedInsertions * 0.693);

// After — named methods
int m = BloomFilters.optimalBitSize(expectedInsertions, 0.01);
int k = BloomFilters.optimalNumHashFunctions(expectedInsertions, m);
```

## Use Enum Funnels for Common Types

```java
// Before
Funnel<String> stringFunnel = (from, into) -> into.putString(from, Charsets.UTF_8);

// After — Guava provides built-in funnels
Funnel<String> funnel = Funnels.stringFunnel(Charsets.UTF_8);
Funnel<Integer> intFunnel = Funnels.integerFunnel();
Funnel<byte[]> byteFunnel = Funnels.byteArrayFunnel();
```

## Replace HashSet with BloomFilter for Large Sets

```java
// Before — HashSet for all lookup
Set<String> blockedUrls = new HashSet<>();
// Memory: O(n × string_size) — may be huge

// After — Bloom filter for first pass
BloomFilter<String> blockedUrls = BloomFilter.create(
    Funnels.stringFunnel(Charsets.UTF_8), 1000000, 0.001
);
// Plus small HashSet for confirmed false positives
Set<String> confirmedBlocked = new HashSet<>();
```

## Convert to Counting Bloom Filter for Deletions

```java
// Before — can't delete from standard BloomFilter
BloomFilter<String> filter = BloomFilter.create(funnel, n, 0.01);
// Can't remove elements!

// After — Counting Bloom Filter
CountingBloomFilter<String> cbf = new CountingBloomFilter<>(n, 0.01);
cbf.put(key);
cbf.delete(key);  // supported
```

## Use Bloom Filter in Stream Pipeline

```java
// Before — accumulator
Set<String> seen = new HashSet<>();
List<String> deduped = list.stream()
    .filter(s -> !seen.contains(s))
    .peek(seen::add)
    .toList();

// After — Bloom filter (for very large streams)
BloomFilter<String> seen = BloomFilter.create(funnel, list.size(), 0.001);
List<String> deduped = list.stream()
    .filter(s -> !seen.mightContain(s))
    .peek(seen::put)
    .toList();
```
