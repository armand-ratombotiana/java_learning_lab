# How Bloom Filters Work

## Implementation

```java
public class BloomFilter<T> {
    private final BitSet bits;
    private final int bitSize;       // m
    private final int numHashFunctions;  // k
    private final Funnel<T> funnel;
    private int insertions;  // n

    public BloomFilter(Funnel<T> funnel, int expectedInsertions, double fpp) {
        this.funnel = funnel;
        this.bitSize = optimalBitSize(expectedInsertions, fpp);
        this.numHashFunctions = optimalNumHashFunctions(expectedInsertions, bitSize);
        this.bits = new BitSet(bitSize);
        this.insertions = 0;
    }

    public void put(T element) {
        int[] hashes = hash(element);
        for (int i = 0; i < numHashFunctions; i++) {
            bits.set(hashes[i]);
        }
        insertions++;
    }

    public boolean mightContain(T element) {
        int[] hashes = hash(element);
        for (int i = 0; i < numHashFunctions; i++) {
            if (!bits.get(hashes[i])) {
                return false;  // definitely not present
            }
        }
        return true;  // might be present
    }

    // Hash using two independent hash functions (Kirsch-Mitzenmacher)
    private int[] hash(T element) {
        byte[] data = funnel.apply(element);
        long hash1 = murmurHash64(data, 0);
        long hash2 = murmurHash64(data, 1);
        int[] result = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            result[i] = (int) ((hash1 + i * hash2) & Long.MAX_VALUE) % bitSize;
        }
        return result;
    }

    // Optimal calculations
    static int optimalBitSize(int n, double p) {
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    static int optimalNumHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    // Funnel interface (like Guava)
    interface Funnel<T> {
        byte[] apply(T from);
    }

    // MurmurHash 64-bit (simplified)
    private long murmurHash64(byte[] data, int seed) {
        // Standard MurmurHash64 implementation
        // Returns 64-bit hash value
        return 0L; // placeholder
    }
}
```

## Usage Example

```java
BloomFilter<String> filter = new BloomFilter<>(
    s -> s.getBytes(StandardCharsets.UTF_8),
    10000,        // expected insertions
    0.01          // 1% false positive rate
);

filter.put("apple");
filter.put("banana");

System.out.println(filter.mightContain("apple"));   // true
System.out.println(filter.mightContain("banana"));  // true
System.out.println(filter.mightContain("cherry"));  // probably false (1% chance false positive)
```

## Optimal Parameters

For n = 10,000 and P = 0.01:
- m = -10000 × ln(0.01) / ln²(2) ≈ 95,428 bits ≈ 12 KB
- k = (95428 / 10000) × ln(2) ≈ 6.6 ≈ 7 hash functions

For n = 1,000,000 and P = 0.001:
- m = -1,000,000 × ln(0.001) / ln²(2) ≈ 14.3 MB
- k = 10 hash functions
