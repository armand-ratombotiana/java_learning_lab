# Common Mistakes with Bloom Filters

## Assuming Deletion is Supported

```java
// WRONG — standard Bloom filters do NOT support deletion
BloomFilter<String> filter = BloomFilter.create(...);
filter.put("hello");
// Can't remove "hello" without risking false negatives!
```

Use Counting Bloom Filter if deletion is needed.

## Using Too Few Hash Functions

```java
// WRONG — k=1 makes it a simple hash set with many false positives
// Every element sets exactly 1 bit → high collision rate
BloomFilter<String> filter = BloomFilter.create(funnel, n, 0.1);
// Under the hood this computes optimal k, so it's actually fine.
// But manually: using k=1 for m/n=10 gives P=10% vs optimal k=7 gives P=0.8%
```

## Overfilling the Filter

```java
// WRONG — adding more than expected elements
BloomFilter<String> filter = BloomFilter.create(funnel, 1000, 0.01);
for (int i = 0; i < 10000; i++) {
    filter.put("element-" + i);  // 10x expected! FPP skyrockets
}
// Expected 1% FPP → actual ~50% FPP after overfilling
```

The false positive rate degrades quickly when n exceeds expectations. Monitor and rebuild if needed.

## Not Handling Serialization Size

```java
// Bloom filter with large m serializes as a large bit array
BloomFilter<String> filter = BloomFilter.create(funnel, 10_000_000, 0.001);
// m ≈ 144 MB → serialized size ≈ 144 MB!
```

Consider compression for serialization/deserialization.

## Incorrect Hash Function Usage

```java
// WRONG — using hash codes that don't distribute uniformly
class BadFunnel implements Funnel<MyObj> {
    @Override
    public void funnel(MyObj from, PrimitiveSink into) {
        into.putInt(from.hashCode());  // Bad: uses default Object hashCode!
    }
}
```

## Not Understanding "Might Contain"

```java
// WRONG — treating Bloom filter as exact
if (filter.mightContain(key)) {
    // Assume key exists — might be false positive!
    // Always verify with actual storage
    Value v = store.get(key);  // confirm
    if (v != null) { ... }
}
```

## Using Bloom Filter for Small Sets

```java
// Bloom filter overhead > HashSet for small sets
// For n < 1000, HashSet is simpler and more efficient
BloomFilter<String> filter = BloomFilter.create(funnel, 100, 0.01);
// ~960 bits = 120 bytes
HashSet<String> set = new HashSet<>();  // ~96 bytes for 100 empty strings
// For 100 strings, HashSet is comparable or smaller
```
