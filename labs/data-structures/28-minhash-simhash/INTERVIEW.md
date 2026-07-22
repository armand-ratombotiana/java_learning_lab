# Interview Questions: MinHash & SimHash

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — system design focus) | — | Google, Meta, Amazon, Microsoft, Apple | Near-duplicate detection / similarity |

## NeetCode Reference
Not in NeetCode. MinHash and SimHash are system design topics for large-scale similarity detection.

## Company-Specific Questions

### Google
- Explain how MinHash estimates Jaccard similarity between two sets — how does the min-wise hashing trick work?
- How does SimHash generate a fingerprint for a document and enable detection of near-duplicates?
- Design a system to detect near-duplicate web pages (Google's shingling + MinHash approach)
- Compare MinHash vs SimHash — which is better for document similarity vs image similarity?

### Microsoft
- Implement MinHash with k hash functions — how does increasing k improve accuracy?
- How does Locality-Sensitive Hashing (LSH) banding technique reduce the number of candidate pairs?
- Design a plagiarism detection system using MinHash and LSH

### Meta
- How would you detect near-duplicate photos uploaded to Instagram using SimHash (perceptual hashing)?
- Design a system to find similar Facebook posts (text similarity using MinHash)
- How does the LSH banding technique work? Given signature matrix with r rows per band and b bands, what is the probability of at least one band matching?

### Amazon
- Design a product recommendation system based on "customers who bought this also bought" using MinHash
- How would you detect duplicate product listings (same product, different title/description)?
- Design a near-duplicate detection system for product reviews

### Apple
- How would you detect duplicate photos in iCloud using perceptual hashing + SimHash?
- Design a system to find similar songs in Apple Music (audio fingerprinting + MinHash)
- How does Spotlight handle near-duplicate document detection?

### Oracle
- How would you implement MinHash for large-scale database record deduplication?
- What is the relationship between MinHash and Jaccard similarity? Prove E[minHash(A) = minHash(B)] = J(A, B)
- Compare SimHash vs MinHash for text deduplication in document databases
- How does Oracle's Data Redaction use similarity detection for sensitive data identification?

## Real Production Scenarios

- **Scenario 1: Web Crawler Deduplication** — A search engine web crawler detects near-duplicate pages to avoid indexing near-identical content. Each page is shingled into n-grams (e.g., 3-word shingles). MinHash produces a signature (100-200 hashes). LSH bands group similar pages into candidate pairs. Pages above a Jaccard similarity threshold (e.g., 0.8) are considered near-duplicates.

- **Scenario 2: Plagiarism Detection** — An academic integrity system checks student submissions against a database of existing documents. MinHash signatures are precomputed for all documents. LSH quickly identifies candidate pairs. Exact similarity is verified by comparing the original shingle sets of candidates.

- **Scenario 3: News Clustering** — A news aggregation service groups similar articles from different sources into clusters. SimHash fingerprints (64-bit) are generated for each article. Articles with Hamming distance ≤ 3 (SimHash) or Jaccard ≥ 0.7 (MinHash) are clustered together. The system processes millions of articles/day.

## Interview Tips

- Time: O(n·k) for MinHash signature generation (n shingles, k hash functions); O(k) per similarity query (compare signatures); O(d) for SimHash (d = dimensions)
- Space: O(n·k) for signatures; can be reduced via LSH banding; SimHash stores 64-bit fingerprint per document
- MinHash: Jaccard(A,B) ≈ fraction of signature components where minhash values match
- SimHash: documents are similar if their 64-bit fingerprints are close in Hamming distance (typically ≤ 3)
- LSH banding: split signature into b bands of r rows; documents match if any band's hash matches; reduces pair comparisons from O(n²) to near O(n)
- Common edge cases: empty sets (Jaccard = 0/0? define as 0), singleton sets, identical documents, completely different documents

## Java-Specific Considerations

- No standard MinHash/SimHash class in Java — implement from scratch
- MinHash signature: `int[] signature = new int[k]; Arrays.fill(signature, Integer.MAX_VALUE);` — for each shingle, update each hash
- Hash functions for MinHash: `h_i(x) = (a_i * x + b_i) % p` with random a_i, b_i and large prime p
- Shingling: `Set<String> shingles = new HashSet<>(); for (int i = 0; i <= text.length() - shingleSize; i++) shingles.add(text.substring(i, i + shingleSize));`
- SimHash: `long simhash(String text) { int[] v = new int[64]; for each token hash → set v_i += (bit_i == 1 ? 1 : -1); return bits where v_i > 0; }`
- `Integer.bitCount(long x)` for Hamming distance: `int hamming(long a, long b) { return Long.bitCount(a ^ b); }`
- `long[]` for band hashing in LSH: `HashMap<Long, List<Integer>> buckets` maps band hash → document IDs
- `Java 8+ streams`: `double jaccard = IntStream.range(0, k).filter(i -> sigA[i] == sigB[i]).count() / (double)k;`
- `Object.hashCode()` is NOT a good hash for MinHash — use `Hashing.murmur3_32()` (Guava) instead
- Guava's `com.google.common.hash.HashFunction` provides consistent hashing for MinHash
- Third-party library: `org.apache.datasketches.thetasketch` for MinHash-like cardinality-based similarity
- For large-scale: store signatures as `byte[]` (compact, 4 bytes per hash × k = 800 bytes for k=200)
