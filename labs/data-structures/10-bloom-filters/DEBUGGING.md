# Debugging Bloom Filters

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| False positive rate too high | Overfilled filter (n >> expected) |
| False positives on empty filter | Never happens — only 1s cause false positives |
| Filter returns false for inserted element | Impossible — Bloom filters never have false negatives (check implementation!) |
| Slow insert/query | Too many hash functions (k too high) |
| Memory higher than expected | Bit array size m too large for expected n |

## Debugging Techniques

### Measure Actual False Positive Rate

```java
void measureFPP(BloomFilter<String> filter, int testSize) {
    int falsePositives = 0;
    for (int i = 0; i < testSize; i++) {
        String test = "never-inserted-" + i;
        if (filter.mightContain(test)) {
            falsePositives++;
        }
    }
    double fpp = (double) falsePositives / testSize;
    System.out.println("Measured FPP: " + fpp);
    System.out.println("Expected FPP: " + filter.expectedFpp());
}
```

### Check Fill Level

```java
void printStats(BloomFilter<?> filter) {
    // Reflection to access internal bit count
    // Guava provides expectedFpp() and approximateElementCount()
    System.out.println("Approx elements: " + filter.approximateElementCount());
    System.out.println("Expected FPP: " + filter.expectedFpp());
}
```

### Validate Configuration

```java
void validateConfig(int n, double p) {
    int m = optimalBitSize(n, p);
    int k = optimalNumHashFunctions(n, m);
    double actualP = Math.pow(1 - Math.exp(-(double) k * n / m), k);
    System.out.println("n=" + n + " p=" + p);
    System.out.println("m=" + m + " bits (" + (m/8/1024) + " KB)");
    System.out.println("k=" + k + " hash functions");
    System.out.println("Actual P=" + actualP + " (target=" + p + ")");
}
```

### Unit Testing

```java
@Test
void testBloomFilterNoFalseNegatives() {
    BloomFilter<String> filter = BloomFilter.create(
        Funnels.stringFunnel(Charsets.UTF_8), 1000, 0.01);
    String[] inserted = {"apple", "banana", "cherry"};
    for (String s : inserted) filter.put(s);
    for (String s : inserted) {
        assertTrue(filter.mightContain(s));
    }
}

@Test
void testBloomFilterFalsePositiveRate() {
    BloomFilter<String> filter = BloomFilter.create(
        Funnels.stringFunnel(Charsets.UTF_8), 10000, 0.01);
    for (int i = 0; i < 10000; i++) {
        filter.put("key-" + i);
    }
    int fp = 0;
    int trials = 10000;
    for (int i = 0; i < trials; i++) {
        if (filter.mightContain("not-key-" + i)) fp++;
    }
    double rate = (double) fp / trials;
    assertTrue(rate < 0.05);  // should be ~1%, allow margin
}

@Test
void testCountingBloomDelete() {
    CountingBloomFilter<String> cbf = new CountingBloomFilter<>(100, 0.01);
    cbf.put("hello");
    assertTrue(cbf.mightContain("hello"));
    assertTrue(cbf.delete("hello"));
    assertFalse(cbf.mightContain("hello"));  // now absent
}
```
