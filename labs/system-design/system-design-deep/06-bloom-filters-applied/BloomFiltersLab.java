package com.systemdesign.deep.lab06;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lab 06: Bloom Filters Applied — Standard Bloom, Counting Bloom,
 * Scalable Bloom, Cuckoo Filter, and real-world system design applications.
 */
public class BloomFiltersLab {

    static int hash(String data, int seed) {
        int h = seed;
        for (byte b : data.getBytes(StandardCharsets.UTF_8)) {
            h ^= (b & 0xff);
            h *= 0x01000193;
        }
        return h & 0x7fffffff;
    }

    // ──────────────────────────────────────────────
    // 1. Standard Bloom Filter
    // ──────────────────────────────────────────────
    static class BloomFilter {
        final BitSet bits;
        final int k;       // hash functions
        final int m;       // bits
        final AtomicInteger added = new AtomicInteger();

        BloomFilter(int expectedElements, double falsePositiveRate) {
            this.m = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
            this.k = (int) Math.round((m / (double) expectedElements) * Math.log(2));
            this.bits = new BitSet(m);
            System.out.println("  [Bloom] Created: m=" + m + " bits, k=" + k + " hashes, expected FPP=" + falsePositiveRate);
        }

        void add(String element) {
            for (int i = 0; i < k; i++) {
                bits.set(hash(element, i) % m);
            }
            added.incrementAndGet();
        }

        boolean mightContain(String element) {
            for (int i = 0; i < k; i++) {
                if (!bits.get(hash(element, i) % m)) return false;
            }
            return true;
        }

        double currentFpp() {
            double n = added.get();
            return Math.pow(1 - Math.exp(-k * n / m), k);
        }
    }

    // ──────────────────────────────────────────────
    // 2. Counting Bloom Filter (supports delete)
    // ──────────────────────────────────────────────
    static class CountingBloomFilter {
        final int[] counters;
        final int k;
        final int m;

        CountingBloomFilter(int expectedElements, double falsePositiveRate) {
            this.m = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
            this.k = (int) Math.round((m / (double) expectedElements) * Math.log(2));
            this.counters = new int[m];
        }

        void add(String element) {
            for (int i = 0; i < k; i++) {
                int idx = hash(element, i) % m;
                if (counters[idx] < 255) counters[idx]++;
            }
        }

        void remove(String element) {
            for (int i = 0; i < k; i++) {
                int idx = hash(element, i) % m;
                if (counters[idx] > 0) counters[idx]--;
            }
        }

        boolean mightContain(String element) {
            for (int i = 0; i < k; i++) {
                if (counters[hash(element, i) % m] == 0) return false;
            }
            return true;
        }
    }

    // ──────────────────────────────────────────────
    // 3. Scalable Bloom Filter
    // ──────────────────────────────────────────────
    static class ScalableBloomFilter {
        final List<BloomFilter> filters = new ArrayList<>();
        final double baseFpp;
        final double tighteningRatio;
        final int initialCapacity;
        final AtomicInteger totalAdded = new AtomicInteger();

        ScalableBloomFilter(int initialCapacity, double baseFpp, double tighteningRatio) {
            this.initialCapacity = initialCapacity;
            this.baseFpp = baseFpp;
            this.tighteningRatio = tighteningRatio;
            addFilter();
        }

        private void addFilter() {
            int capacity = initialCapacity * (1 << filters.size());
            double fpp = baseFpp * Math.pow(tighteningRatio, filters.size());
            var filter = new BloomFilter(capacity, fpp);
            filters.add(filter);
            System.out.println("  [Scalable] Added filter #" + filters.size()
                    + " capacity=" + capacity + " FPP=" + String.format("%.6f", fpp));
        }

        void add(String element) {
            filters.get(filters.size() - 1).add(element);
            totalAdded.incrementAndGet();
            // Check if current filter needs expansion
            if (filters.get(filters.size() - 1).added.get() > initialCapacity * 0.75) {
                addFilter();
            }
        }

        boolean mightContain(String element) {
            for (var f : filters) {
                if (f.mightContain(element)) return true;
            }
            return false;
        }
    }

    // ──────────────────────────────────────────────
    // 4. Cuckoo Filter
    // ──────────────────────────────────────────────
    static class CuckooFilter {
        static class Bucket {
            final List<Integer> fingerprints = new ArrayList<>(4);
            boolean add(int fp) {
                if (fingerprints.size() < 4) {
                    fingerprints.add(fp);
                    return true;
                }
                return false;
            }
            boolean remove(int fp) { return fingerprints.remove((Integer) fp); }
            boolean contains(int fp) { return fingerprints.contains(fp); }
        }

        final Bucket[] buckets;
        final int bucketCount;
        final Random rand = new Random();
        final int maxKicks = 500;

        CuckooFilter(int capacity, int bitsPerFingerprint) {
            this.bucketCount = nextPowerOf2(capacity / 4);
            this.buckets = new Bucket[bucketCount];
            for (int i = 0; i < bucketCount; i++) buckets[i] = new Bucket();
        }

        int nextPowerOf2(int n) {
            int p = 1;
            while (p < n) p <<= 1;
            return p;
        }

        int fingerprint(String item) {
            return hash(item, 0) & 0x7fff; // 15-bit fingerprint
        }

        int bucketIndex(int fp) { return fp & (bucketCount - 1); }
        int alternateIndex(int fp, int idx) { return (idx ^ (hash(Integer.toString(fp), 1) & (bucketCount - 1))); }

        boolean add(String item) {
            int fp = fingerprint(item);
            int i1 = bucketIndex(fp);
            int i2 = alternateIndex(fp, i1);

            if (buckets[i1].add(fp) || buckets[i2].add(fp)) return true;

            // Cuckoo: kick out existing fingerprint
            int idx = rand.nextBoolean() ? i1 : i2;
            for (int k = 0; k < maxKicks; k++) {
                int oldFp = buckets[idx].fingerprints.remove(rand.nextInt(buckets[idx].fingerprints.size()));
                buckets[idx].add(fp);
                fp = oldFp;
                idx = alternateIndex(fp, idx);
                if (buckets[idx].add(fp)) return true;
            }
            return false; // filter full
        }

        boolean mightContain(String item) {
            int fp = fingerprint(item);
            int i1 = bucketIndex(fp);
            int i2 = alternateIndex(fp, i1);
            return buckets[i1].contains(fp) || buckets[i2].contains(fp);
        }

        boolean remove(String item) {
            int fp = fingerprint(item);
            int i1 = bucketIndex(fp);
            int i2 = alternateIndex(fp, i1);
            return buckets[i1].remove(fp) || buckets[i2].remove(fp);
        }
    }

    // ──────────────────────────────────────────────
    // 5. Application: Cache Optimization + Dedup
    // ──────────────────────────────────────────────
    static class Applications {

        static void cacheFilterDemo() {
            System.out.println("  [App] Cache key membership filter");
            var bloom = new BloomFilter(1000, 0.01);
            // Pre-populate with existing cache keys
            for (int i = 0; i < 800; i++) bloom.add("cache:key:" + i);

            int lookups = 10_000;
            int bloomSaysNo = 0;
            int actualMisses = 0;

            for (int i = 0; i < lookups; i++) {
                String key = "cache:key:" + ThreadLocalRandom.current().nextInt(2000);
                if (!bloom.mightContain(key)) {
                    bloomSaysNo++;
                    // Skip cache lookup — definitely not in cache
                } else {
                    // Might be in cache — do actual lookup
                    if (i >= 800) actualMisses++; // false positive cache hit
                }
            }
            System.out.println("  Bloom said NO to " + bloomSaysNo + "/" + lookups
                    + " lookups (skipped cache entirely)");
        }

        static void dedupDemo() {
            System.out.println("  [App] URL deduplication");
            var bloom = new BloomFilter(1_000_000, 0.001);
            int inserted = 0;
            int duplicatesCaught = 0;
            int falsePositives = 0;
            Set<String> actual = new HashSet<>();

            for (int i = 0; i < 50_000; i++) {
                String url = "https://example.com/page/" + ThreadLocalRandom.current().nextInt(80_000);
                if (!bloom.mightContain(url)) {
                    bloom.add(url);
                    inserted++;
                    actual.add(url);
                } else {
                    duplicatesCaught++;
                    if (!actual.contains(url)) falsePositives++;
                }
            }
            System.out.println("  Inserted: " + inserted + ", duplicates caught: " + duplicatesCaught
                    + ", false positives: " + falsePositives);
        }

        static void demo() {
            System.out.println("5. Applications");
            cacheFilterDemo();
            dedupDemo();
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 06: Bloom Filters Applied              ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // 1. Standard Bloom Filter
        System.out.println("1. Standard Bloom Filter");
        var bloom = new BloomFilter(1000, 0.01);
        bloom.add("apple");
        bloom.add("banana");
        bloom.add("cherry");
        System.out.println("  Contains 'apple'? " + bloom.mightContain("apple"));
        System.out.println("  Contains 'durian'? " + bloom.mightContain("durian"));
        System.out.println("  Estimated FPP: " + String.format("%.4f", bloom.currentFpp()));
        System.out.println();

        // 2. Counting Bloom Filter
        System.out.println("2. Counting Bloom Filter");
        var counting = new CountingBloomFilter(100, 0.01);
        counting.add("item-1");
        counting.add("item-2");
        System.out.println("  Contains 'item-1'? " + counting.mightContain("item-1"));
        counting.remove("item-1");
        System.out.println("  After remove, contains 'item-1'? " + counting.mightContain("item-1"));
        System.out.println();

        // 3. Scalable Bloom Filter
        System.out.println("3. Scalable Bloom Filter");
        var scalable = new ScalableBloomFilter(100, 0.01, 0.5);
        for (int i = 0; i < 500; i++) scalable.add("element-" + i);
        System.out.println("  Filters created: " + scalable.filters.size());
        System.out.println("  Contains 'element-42'? " + scalable.mightContain("element-42"));
        System.out.println();

        // 4. Cuckoo Filter
        System.out.println("4. Cuckoo Filter");
        var cuckoo = new CuckooFilter(1000, 15);
        for (int i = 0; i < 800; i++) cuckoo.add("user:" + i);
        System.out.println("  Contains 'user:42'? " + cuckoo.mightContain("user:42"));
        System.out.println("  Contains 'user:999'? " + cuckoo.mightContain("user:999"));
        cuckoo.remove("user:42");
        System.out.println("  After remove, contains 'user:42'? " + cuckoo.mightContain("user:42"));
        System.out.println();

        // 5. Applications
        Applications.demo();

        System.out.println("All Bloom filter variants demonstrated successfully.");
    }
}
