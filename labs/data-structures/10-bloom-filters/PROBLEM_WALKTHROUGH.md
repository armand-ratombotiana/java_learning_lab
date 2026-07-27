# Problem Walkthrough: 10-Bloom-Filters

## Problem 1: Design a Bloom Filter — Google

### Interview Scenario
"Google interviewer: 'Design a probabilistic data structure that efficiently tests whether an element is in a set. A small false-positive rate is acceptable, but false negatives are not.'"

### The Problem
A Bloom filter supports `add(element)` and `mightContain(element)`. It may return true for elements not in the set (false positive), but never returns false for elements that were added.

### Step 1: Clarify (30 seconds)
- **Q:** Expected number of elements (n)? **A:** ~10⁶.
- **Q:** Acceptable false positive rate (p)? **A:** ~1% (0.01).
- **Q:** What type of elements? **A:** Strings (URLs, usernames, etc.).
- **Q:** Single-threaded or concurrent? **A:** Single-threaded for this design.
- **Q:** Can I assume good hash functions? **A:** Yes, but discuss what makes a good hash for a Bloom filter.
- **Edge cases:** Empty filter, adding duplicates, filter at capacity, massive number of elements pushing the false positive rate up.

### Step 2: Brute Force (2 min)
- Standard HashSet: O(1) exact membership, but requires O(n) memory per element.
- "For 10⁶ strings of average length 20, a HashSet would take ~20 MB just for the data, plus object overhead (40+ bytes per entry) → ~40-60 MB total."
- This is the exact alternative — Bloom filter trades memory for probabilistic accuracy.

### Step 3: Optimize (5 min)
- "A Bloom filter uses a bit array of size m and k independent hash functions. On add: hash the element with each function and set the corresponding bits. On query: check if all k bits are set. If any bit is 0, the element is definitely not in the set. If all bits are 1, it might be."
- Optimal formulas: m = -n * ln(p) / (ln(2))², k = (m/n) * ln(2).
- For n = 10⁶, p = 0.01: m ≈ 9.6 * 10⁶ bits ≈ 1.2 MB, k ≈ 7 hash functions.
- **Why Google values this:** Google uses Bloom filters extensively — Bigtable (to reduce SSTable lookups), Chrome (malicious URL checking), Google Docs (spell check), and web crawler deduplication.

### Step 4: Code (10 min)

```java
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * A Bloom filter for approximate membership queries.
 * <p>
 * Add: O(k) | MightContain: O(k) | Space: O(m) bits
 */
public class BloomFilter {
    private final BitSet bits;
    private final int m; // number of bits
    private final int k; // number of hash functions

    /**
     * @param n Expected number of elements
     * @param p Desired false positive rate (0 < p < 1)
     */
    public BloomFilter(int n, double p) {
        this.m = (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
        this.k = (int) Math.ceil((double) m / n * Math.log(2));
        this.bits = new BitSet(m);
    }

    public void add(String element) {
        byte[] data = element.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < k; i++) {
            int hash = hash(data, i);
            bits.set(Math.abs(hash % m), true);
        }
    }

    public boolean mightContain(String element) {
        byte[] data = element.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < k; i++) {
            int hash = hash(data, i);
            if (!bits.get(Math.abs(hash % m))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Double-hashing scheme: hash1 + i * hash2 to avoid needing k
     * independent hash function implementations.
     */
    private int hash(byte[] data, int i) {
        int h1 = murmur3(data, 0);
        int h2 = murmur3(data, h1);
        return h1 + i * h2;
    }

    private int murmur3(byte[] data, int seed) {
        int h = seed ^ 0xdeadbeef;
        for (byte b : data) {
            h ^= (b & 0xFF);
            h *= 0x5bd1e995;
            h ^= h >>> 15;
        }
        return h;
    }
}
```

### Step 5: Test (3 min)
- Add "hello", mightContain("hello") → true
- mightContain("world") → false (probably, given low false positive rate)
- Test with 10⁶ random strings, measure false positive rate empirically
- **Edge:** Filter at capacity — false positive rate increases; discuss optimal resizing (not possible with standard Bloom filter — need scalable Bloom filter)
- The test here is empirical: load with n elements, query n unadded elements, count false positives

### Step 6: Follow-ups
- "How do you delete elements?" — Standard Bloom filters don't support deletion. Use Counting Bloom Filter (counters instead of bits, but space increases).
- "What if you need to resize?" — Scalable Bloom filter: chain of Bloom filters with decreasing error rates.
- "What about concurrent access?" — Use AtomicBitSet or ReadWriteLock.
- "What hash functions should you use?" — MurmurHash, xxHash, or SipHash. Need uniform distribution and speed.
- **What Google looks for:** Can you derive m and k from first principles? Do you understand the trade-off between m and false positive rate?

### Company Evaluation Criteria
- **Google:** Depth of understanding. They want to see the math: m = -n*ln(p)/(ln(2))², k = (m/n)*ln(2). They will ask you to derive it.
- **Amazon:** Would ask about Counting Bloom Filter for ad deduplication.
- **Meta:** Would ask about Bloom filter vs. cuckoo filter trade-offs.

---

## Problem 2: URL Deduplication for Web Crawler — Amazon

### Interview Scenario
"Amazon interviewer: 'Our web crawler needs to avoid revisiting the same URL. We have billions of URLs. How do you efficiently check if a URL has already been crawled?'"

### The Problem
Design a deduplication system for web crawler URLs. Must handle billions of URLs, be memory efficient, and have a configurable false positive rate.

### Step 1: Clarify (30 seconds)
- **Q:** How many URLs? **A:** ~10¹⁰ (10 billion).
- **Q:** Acceptable false positive rate? **A:** ~1%. Missing a URL to crawl is OK (false positive = thinking we crawled it when we didn't). False negatives (thinking we didn't when we did) are not.
- **Q:** How is the crawler distributed? **A:** Multiple machines, each crawling independently.
- **Q:** URL length? **A:** Average ~100 bytes.
- **Edge cases:** Duplicate URLs from different machines, same URL with different casing (normalize first), querystring handling (normalize by sorting params).

### Step 2: Brute Force (2 min)
- Store all crawled URLs in a SQL database or a distributed key-value store (DynamoDB, Cassandra).
- **Storage:** 10¹⁰ URLs × 100 bytes = 1 TB. Too expensive and slow for a real-time "have I seen this?" check.
- Database query per URL adds latency and load.

### Step 3: Optimize (5 min)
- "Use a Bloom filter on each crawler node as a first-pass filter. If the Bloom filter says 'not crawled', we definitely haven't seen it. If it says 'seen', we do a more expensive exact check against a backing store (e.g., RocksDB or DynamoDB)."
- Layer 1 (Bloom filter): fast, memory efficient (1-2 GB for 10¹⁰ URLs at 1% false positive rate).
- Layer 2 (exact store): slower but authoritative. Only queried when Bloom filter says "seen".
- **Why Amazon values this:** Caching layers are fundamental to Amazon's architecture. Bloom filter as a first-pass filter reduces load on backend stores (DynamoDB, S3 metadata). This pattern is used across AWS services.

### Step 4: Code (10 min)

```java
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * Two-layer URL deduplication: Bloom filter + backing store.
 * <p>
 * The Bloom filter acts as a fast pre-filter before consulting
 * the authoritative (but slower) exact store.
 */
public class CrawlerUrlDedup {
    private final BloomFilter bloomFilter;
    private final Set<String> exactStore; // RocksDB, DynamoDB, etc.

    public CrawlerUrlDedup(long n, double p) {
        this.bloomFilter = new BloomFilter((int) Math.min(n, Integer.MAX_VALUE), p);
        this.exactStore = new java.util.concurrent.ConcurrentHashMap<>();
    }

    /**
     * Returns true if the URL has likely been crawled.
     * False negatives are impossible; false positives are possible.
     */
    public boolean isCrawled(String url) {
        String normalized = normalize(url);
        // Fast path: Bloom filter check
        if (!bloomFilter.mightContain(normalized)) {
            return false; // Definitely not crawled
        }
        // Slow path: exact check against backing store
        return exactStore.contains(normalized);
    }

    /**
     * Marks a URL as crawled.
     */
    public void markCrawled(String url) {
        String normalized = normalize(url);
        bloomFilter.add(normalized);
        exactStore.add(normalized);
    }

    /**
     * URL normalization: lowercase, strip fragment, sort query params.
     */
    private String normalize(String url) {
        // In practice: parse URL, lowercase scheme+host,
        // remove fragment, sort query parameters,
        // resolve relative paths, remove default ports
        return url.toLowerCase().trim();
    }
}
```

### Step 5: Test (3 min)
- markCrawled("https://amazon.com/product/1") followed by isCrawled("https://amazon.com/product/1") → true
- isCrawled("https://amazon.com/product/2") → false (probably)
- Test false positive rate empirically by adding 10⁶ URLs and querying 10⁶ unadded URLs
- **Edge:** Normalization — "HTTP://AMAZON.COM" and "http://amazon.com" must match
- **Edge:** URL with fragment "http://amazon.com/#section" should match "http://amazon.com/"

### Step 6: Follow-ups
- "What if the Bloom filter is too large for memory?" — Partition URLs by domain hash and distribute across machines (sharded Bloom filters).
- "How do you handle the backing store being unavailable?" — In that case, err on the side of re-crawling (bloom filter alone, accept increased crawl load).
- "What about the crawler being distributed?" — Each node has its own Bloom filter + local cache. Periodically merge filters. Use a distributed store (DynamoDB) for the exact set.
- **What Amazon looks for:** Systems thinking. Can you design a multi-layer system with graceful degradation?

### Company Evaluation Criteria
- **Amazon:** System design and layered architecture. They evaluate trade-offs: Bloom filter size vs. false positive rate vs. load on backing store.
- **Google:** Would ask about optimizing hash functions for URL patterns.
- **Meta:** Would ask about URL normalization and deduplication across billions of user-shared links.

---

## Problem 3: Malicious URL Checker — Meta

### Interview Scenario
"Meta interviewer: 'Meta's platform needs to check if a shared URL is known to be malicious/phishing. The list has 10 million entries and grows daily. How do you implement a client-side check that is fast, small, and doesn't leak the full list?'"

### The Problem
A client application needs to check URLs against a server-side malicious URL database without downloading the entire database.

### Step 1: Clarify (30 seconds)
- **Q:** How big is the malicious URL list? **A:** ~10⁷ entries, daily updates.
- **Q:** Client constraints? **A:** Limited memory, offline-capable, fast response.
- **Q:** Can the client query a server every time? **A:** Not always — poor connectivity, privacy concerns (don't want to reveal every URL the user visits).
- **Q:** False positive tolerance? **A:** Some false positives are acceptable (extra warning), but false negatives are dangerous.
- **Edge cases:** URLs with typosquatting (e.g., "faceboook.com"), IP-based URLs, internationalized domain names (IDN), URL shorteners.

### Step 2: Brute Force (2 min)
- Download the entire malicious URL database to the client. 10⁷ × 100 bytes = 1 GB. Not feasible for a mobile app.
- Server-side only check: every URL visit requires a network call. Privacy issues, latency, and no offline support.

### Step 3: Optimize (5 min)
- "Use a Bloom filter on the client. The server periodically pushes the Bloom filter (updated daily) to all clients. The client checks every URL against the local Bloom filter. If the filter says 'not malicious', the user proceeds safely. If 'possibly malicious', the client either blocks or queries the server for a definitive check."
- Bloom filter size for 10⁷ URLs at 0.1% false positive rate: m ≈ 180 MB. Can be compressed and sent as a daily delta.
- **Chrome actually does this:** Safe Browsing uses a Bloom filter-like structure (actually a set of prefix hashes) downloaded to the client.
- **Why Meta values this:** Security at scale. Meta uses similar techniques for detecting phishing links, malicious content, and banned URLs across Messenger, WhatsApp, and Facebook.

### Step 4: Code (10 min)

```java
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * Client-side malicious URL checker using a Bloom filter.
 * <p>
 * The server periodically publishes a new Bloom filter.
 * The client uses it for fast, private URL checks without
 * revealing browsing history to the server.
 */
public class MaliciousUrlChecker {
    private volatile BloomFilter currentFilter;
    private final MaliciousUrlApi api;

    public MaliciousUrlChecker(MaliciousUrlApi api, BloomFilter initialFilter) {
        this.api = api;
        this.currentFilter = initialFilter;
    }

    /**
     * Updates the local Bloom filter from the server.
     * Called periodically (e.g., every 30 minutes).
     */
    public void updateFilter() {
        BloomFilter newFilter = api.fetchBloomFilter();
        if (newFilter != null) {
            this.currentFilter = newFilter;
        }
    }

    /**
     * Checks whether a URL is potentially malicious.
     * <p>
     * Level 1: Local Bloom filter (fast, private).
     * Level 2: Server-side check (slow, reveals URL).
     *
     * @return true if the URL is known malicious
     */
    public boolean isMalicious(String url) {
        String normalized = normalizeUrl(url);

        // Level 1: Bloom filter check (fast, no network)
        if (!currentFilter.mightContain(normalized)) {
            return false; // Definitely safe
        }

        // Level 2: Server-side check for confirmation
        // Only sends the URL if the Bloom filter flagged it
        return api.checkUrl(normalized);
    }

    /**
     * Normalizes the URL to reduce false negatives.
     */
    private String normalizeUrl(String url) {
        // Lowercase, canonicalize, remove tracking params
        return url.toLowerCase()
                  .replaceAll("^https?://", "")
                  .replaceAll("/$", "")
                  .trim();
    }
}

/**
 * API interface for server communication.
 */
interface MaliciousUrlApi {
    BloomFilter fetchBloomFilter();
    boolean checkUrl(String url);
}
```

### Step 5: Test (3 min)
- Assume "http://phishing.com" is in the malicious list
- isMalicious("http://phishing.com") → true (Bloom filter flags → server confirms)
- isMalicious("http://google.com") → false (Bloom filter does not flag)
- **Edge:** URL normalization — "HTTP://PHISHING.COM/" should match
- **Edge:** International characters — "http://🅴🆇🅰🅼🅿🅻🅴.com" needs punycode encoding
- An adversary could try to flood the server with lookups for URLs that happen to be false positives → rate limit the Level 2 check

### Step 6: Follow-ups
- "What about privacy — you reveal URLs to the server on Level 2 hits?" — Use Oblivious HTTP or a private set intersection protocol to avoid revealing the URL.
- "What about Bloom filter size reduction?" — Use a partitioned or block-based Bloom filter for better compression. Chrome uses 32-bit hash prefixes (not a full Bloom filter).
- "What about IDN homograph attacks (lookalike characters)?" — Normalize Unicode (NFD/NFC), map confusable characters to their ASCII alternatives.
- **What Meta looks for:** Privacy-aware design. Can you build a secure system without compromising user privacy?

### Company Evaluation Criteria
- **Meta:** System design with privacy constraints. They care deeply about not leaking user data.
- **Google:** Would ask about Chrome Safe Browsing implementation details (prefix-based vs. full hash).
- **Amazon:** Would ask about using this for marketplace seller URL fraud detection.

---

## Study Notes

### Key Patterns
- **Bloom filter basics:** Bit array + k hash functions, no false negatives, configurable false positive rate
- **Hash function technique:** Double hashing (h₁ + i·h₂) instead of k independent hash functions
- **Layered architecture:** Bloom filter as fast pre-filter + exact store for verification
- **Scalable Bloom filter:** Chain of filters with decreasing error rates for dynamic growth
- **Counting Bloom filter:** Counters instead of bits to support deletion

### Common Mistakes
- Not computing optimal m and k — using arbitrary large values without justification
- Using Java's hashCode() — not uniformly distributed enough
- Forgetting that you can't iterate over elements in a Bloom filter
- Not normalizing input — "Hello" vs "hello" produce different hashes
- Overestimating the space savings — Bloom filter is most effective when you accept 1-10% false positive rate

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Standard Bloom filter | O(k) per op | O(m) bits |
| Counting Bloom filter | O(k) per op | O(m * C) bits (C = counter bits) |
| Scalable Bloom filter | O(k) per op | O(Σm_i) bits |
| Cuckoo filter | O(1) per op | O(n) buckets |
| Partitioned Bloom filter | O(k) per op | O(m/k * k) bits |
| Exact HashSet | O(1) per op | O(n * object overhead) |
