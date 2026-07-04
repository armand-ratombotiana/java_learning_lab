# Code Deep Dive: Bloom Filter Applications

## Counting Bloom Filter

```java
public class CountingBloomFilter<T> {
    private final short[] counters;  // 4-bit counters per slot
    private final int numHashFunctions;
    private final int bitSize;

    public CountingBloomFilter(int expectedInsertions, double fpp) {
        bitSize = optimalBitSize(expectedInsertions, fpp);
        numHashFunctions = optimalNumHashFunctions(expectedInsertions, bitSize);
        counters = new short[bitSize];
    }

    public void put(T element) {
        int[] hashes = hash(element);
        for (int h : hashes) {
            if (counters[h] < 15) counters[h]++;  // saturating
        }
    }

    public boolean mightContain(T element) {
        int[] hashes = hash(element);
        for (int h : hashes) {
            if (counters[h] == 0) return false;
        }
        return true;
    }

    public boolean delete(T element) {
        int[] hashes = hash(element);
        // First verify all counters > 0
        for (int h : hashes) {
            if (counters[h] == 0) return false;
        }
        // Then decrement
        for (int h : hashes) {
            counters[h]--;
        }
        return true;
    }

    private int[] hash(T element) {
        // ... use two hash functions to generate k positions
        return new int[numHashFunctions];  // placeholder
    }
}
```

## Bloom Filter for Caching (Cache-Aside)

```java
public class CacheWithBloomFilter<K, V> {
    private final BloomFilter<K> bloomFilter;
    private final Map<K, V> cache;
    private final DataStore<K, V> dataStore;

    public CacheWithBloomFilter(int expectedInsertions, double fpp) {
        this.bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            expectedInsertions, fpp
        );
        this.cache = new HashMap<>();
        this.dataStore = new DataStore<>();
    }

    public V get(K key) {
        // Quick rejection: if key definitely not in data store
        if (!bloomFilter.mightContain(key)) {
            return null;  // skip expensive lookup
        }
        // Check cache first
        V value = cache.get(key);
        if (value != null) return value;
        // Bloom filter said maybe → check actual store
        value = dataStore.get(key);
        if (value != null) {
            cache.put(key, value);
        }
        return value;
    }

    public void put(K key, V value) {
        bloomFilter.put(key);
        cache.put(key, value);
        dataStore.put(key, value);
    }
}
```

## Bloom Filter for Web Crawler Dedup

```java
class WebCrawler {
    private final BloomFilter<String> visitedUrls;
    private final Set<String> confirmedVisited;  // small, for confirmation
    private final Queue<String> queue;

    public WebCrawler(int estimatedPages) {
        visitedUrls = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8),
            estimatedPages, 0.001  // 0.1% false positive
        );
        confirmedVisited = new HashSet<>();  // exact for confirmed
        queue = new LinkedList<>();
    }

    public void crawl(String seedUrl) {
        queue.add(seedUrl);
        while (!queue.isEmpty()) {
            String url = queue.poll();
            if (visitedUrls.mightContain(url)) {
                if (!confirmedVisited.contains(url)) {
                    // Might be false positive — verify
                    // Actually visit and add to confirmed set
                } else {
                    continue;  // already visited
                }
            }
            visitedUrls.put(url);
            confirmedVisited.add(url);
            // Fetch page, extract links, add to queue
        }
    }
}
```
