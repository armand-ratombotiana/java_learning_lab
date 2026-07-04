# Internals: Guava BloomFilter

## Guava's BloomFilter (Java 17+)

```java
@Beta
@Immutable
@ElementTypesAreNonnullByDefault
public final class BloomFilter<T> implements Predicate<T>, Serializable {

    private final BitArray bits;
    private final int numHashFunctions;  // k
    private final Funnel<? super T> funnel;
    private final Strategy strategy;     // hashing strategy

    // BitArray is a custom class wrapping long[]
    // Each long stores 64 bits
    private static class BitArray {
        final long[] data;
        long bitCount;  // number of 1s

        BitArray(long bits) {
            this.data = new long[Ints.checkedCast(
                LongMath.divide(bits, 64, RoundingMode.CEILING))];
        }

        boolean set(long index) {
            if (!get(index)) {
                data[(int)(index >>> 6)] |= (1L << index);
                bitCount++;
                return true;
            }
            return false;
        }

        boolean get(long index) {
            return (data[(int)(index >>> 6)] & (1L << index)) != 0;
        }
    }
}
```

### Strategy: Kirsch-Mitzenmacher Optimization

Instead of computing k independent hash functions, Guava computes **two** 64-bit hashes and derives k hash positions:

```java
// hash1 = first 64 bits, hash2 = second 64 bits
// hash_i = hash1 + i * hash2
// This is equivalent to k independent hash functions
// in terms of false positive probability.

enum Murmur3_128Strategy implements Strategy {
    INSTANCE;

    @Override
    public <T> boolean put(T object, Funnel<? super T> funnel,
                           int numHashFunctions, BitArray bits) {
        long bitSize = bits.bitSize();
        byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).getBytesInternal();
        long hash1 = lowerEight(bytes);
        long hash2 = upperEight(bytes);

        boolean bitsChanged = false;
        long combinedHash = hash1;
        for (int i = 0; i < numHashFunctions; i++) {
            bitsChanged |= bits.set((combinedHash & Long.MAX_VALUE) % bitSize);
            combinedHash += hash2;
        }
        return bitsChanged;
    }

    @Override
    public <T> boolean mightContain(T object, Funnel<? super T> funnel,
                                     int numHashFunctions, BitArray bits) {
        long bitSize = bits.bitSize();
        byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).getBytesInternal();
        long hash1 = lowerEight(bytes);
        long hash2 = upperEight(bytes);

        long combinedHash = hash1;
        for (int i = 0; i < numHashFunctions; i++) {
            if (!bits.get((combinedHash & Long.MAX_VALUE) % bitSize)) {
                return false;
            }
            combinedHash += hash2;
        }
        return true;
    }
}
```

### Serialization

```java
// BloomFilter is serializable
// Reconstructed filter has the same bits and properties
// Serialized as: version, strategy ordinal, numHashFunctions, bit data
```

### Approximate Element Count

```java
public long approximateElementCount() {
    long bitSize = bits.bitSize();
    long bitCount = bits.bitCount();
    // n = -m × ln(1 - bits_set / m) / k
    return (long) (-bitSize * Math.log(1.0 - (double) bitCount / bitSize)
                   / numHashFunctions);
}
```

### Expected False Positive Rate

```java
public double expectedFpp() {
    return Math.pow((double) bits.bitCount() / bitSize(), numHashFunctions);
}
```

### Thread Safety

Guava's `BloomFilter` is **not thread-safe** for concurrent `put` operations. Use external synchronization or create per-thread instances and merge later.

### Memory

- A `BloomFilter` with 1% FPP and 1M elements: ~12 MB
- Equivalent HashSet<String>: ~50-100 MB (depending on string size)
