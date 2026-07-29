# Company-Specific Interview Guide: Advanced Data Structures

## Google

### Interview Focus
Google interviews heavily feature algorithm design with optimisation constraints. They ask about data structure internals and trade-offs.

### High-Frequency DS Questions

**Trie (Very High):**
- "Implement a trie with insert, search, startsWith" (LC 208)
- "Word Search II" with trie optimisation (LC 212)
- "Design autocomplete for search engine"
- "Longest word in dictionary with trie" (LC 720)
- "Design an IP routing table using longest prefix match"

**Bloom Filter (High):**
- "Design a web crawler deduplication system"
- "Design a Bloom filter for Chrome's Safe Browsing"
- "How would you detect malicious URLs with limited memory?"
- "Design a system to prevent cache stampede"

**Segment Tree (High):**
- "Range sum query with updates" (LC 307)
- "Count of range sum" (LC 327)
- "Falling squares" with lazy segment tree (LC 699)
- "Rectangle area II" with 2D segment tree (LC 850)

**Union-Find (Very High):**
- "Number of islands II" (dynamic additions)
- "Redundant connection" (LC 684/685)
- "Accounts merge" (LC 721)
- "Satisfiability of equality equations" (LC 990)
- "Number of good paths" (LC 2421)

**Suffix Array (Medium):**
- "Longest duplicate substring" (LC 1044)
- "String search in genome"

### Google Interview Tips
1. **SCAMP**: State, Constraints, Approach, Math (Complexity), Pseudocode, Program
2. Google asks "design" for DS that are not standard library
3. Follow-ups often involve distributed scaling
4. Write clean Java with proper generics
5. Expect deep probing on runtime analysis (amortised vs worst-case)

### Sample Google-Style Problem
> Design a spell checker that suggests corrections for misspelled words. The system should support adding new words to the dictionary and suggesting the closest matching words. Optimise for memory in a mobile environment.

**Solution approach:** Trie with Levenshtein distance traversal (branch-and-bound). Use word frequency for ranking suggestions.

---

## Amazon

### Interview Focus
Amazon focuses on scalable systems, concurrency, and practical application of DS in distributed environments. Leadership principles (bias for action, dive deep) are evaluated.

### High-Frequency DS Questions

**Bloom Filter (Very High):**
- "Deduplicate 1B URLs with limited memory"
- "Design Amazon's product recommendation bloom filter"
- "Prevent cache stampede in DynamoDB"
- "Weak password detection on signup"

**Skip List (High):**
- "Design DynamoDB's index (lock-free concurrent skip list)"
- "Implement a leaderboard with O(log n) rank queries"
- "Design an in-memory key-value store with range scan"
- "ConcurrentSkipListMap internals"

**Red-Black Tree (High):**
- "Design a consistent hash ring with virtual nodes"
- "Implement TreeMap from scratch"
- "Design a range-based key-value store"
- "Design inventory reservation system with expiry"

**Union-Find (Medium):**
- "Number of connected components in AWS network"
- "Find redundant connection in VPC topology"
- "Accounts merge across Amazon services"

**Trie (Medium):**
- "Search autocomplete for Amazon search"
- "Product catalogue prefix search"

### Amazon Interview Tips
1. Always consider **scalability**: how does this work with 1M QPS?
2. Mention **concurrency**: lock-free, CAS, optimistic locking
3. Use **Java concurrency** knowledge: ConcurrentHashMap, AtomicReference
4. Connect to **AWS services**: DynamoDB (Skip List), S3 (Merkle), Route53 (Trie)
5. Give **real-world** examples from your experience

### Sample Amazon-Style Problem
> Amazon's product catalogue has millions of ASINs. Design a system that allows customers to type a prefix and see matching products in real time. The system should handle 100K QPS and update as new products are added.

**Solution approach:** Multi-level trie with caching at the first 2-3 levels. Bloom filter to check if prefix exists before expensive trie traversal. Use compressed trie for mobile.

---

## Meta (Facebook)

### Interview Focus
Meta asks problems with string manipulation, social graph analysis, and real-time systems. They value understanding of lower-level details.

### High-Frequency DS Questions

**Trie (Very High):**
- "Implement a trie" (LC 208)
- "Add and search word with wildcard" (LC 211)
- "Word Search II" (LC 212)
- "Design Facebook search autocomplete"
- "Design Mentions typeahead for Messenger"

**Suffix Array (High):**
- "Longest repeated substring in a document"
- "Detect plagiarism in submitted posts"
- "Shortest unique substring for URL shortening"

**Union-Find (Very High):**
- "Number of islands" (LC 200)
- "Accounts merge" (LC 721)
- "Friend circles / number of friend groups"
- "Verify social graph is a tree"

**Treap (Medium):**
- "Order statistics for News Feed ranking"
- "Dynamic array for real-time collaborative editing"

### Meta Interview Tips
1. **String problems** are extremely common
2. **Time-box the solution**: 25-30 min coding, 10 min testing
3. **ASCII/whiteboard drawings** help explain tree traversal
4. Follow-ups about **memory optimisation** are frequent
5. Know **recursion vs iteration** trade-offs in detail

### Sample Meta-Style Problem
> Facebook has 2B+ users. When a user types in the search bar, suggest friends, pages, groups by prefix. The suggestion list must update within 50ms. Design the system.

**Solution approach:** Tiered trie: level 1 in Redis (hot prefixes), level 2 in memory (frequent queries), full trie on SSD. Background rebuild. Bloom filter for prefix existence check.

---

## Microsoft

### Interview Focus
Microsoft problems are methodical, often involving mathematical reasoning, range queries, and system-level thinking. They value correctness and completeness.

### High-Frequency DS Questions

**Fenwick Tree (High):**
- "Range sum query with updates" (LC 307)
- "Count of smaller numbers after self" (LC 315)
- "Reverse pairs" (LC 493)
- "Create sorted array through instructions" (LC 1649)
- "Count good triplets" (LC 2179)

**Segment Tree (High):**
- "Range module" (LC 715)
- "My Calendar I/II/III" (LC 729/731/732)
- "Falling squares" (LC 699)
- "Rectangle area" (LC 850)

**Union-Find (Medium):**
- "Redundant connection" (LC 684)
- "Evaluate division" (LC 399)
- "Check if graph is a tree"

**Trie (Medium):**
- "Word Search II"
- "Replace Words" (LC 648)

**Merkle Tree (Low-Medium):**
- "Design Azure Blob Storage's integrity check"
- "Implement verifiable log for audit"

### Microsoft Interview Tips
1. Understand **Windows/Office** applications of DS (trie in OneNote, Merkle in OneDrive)
2. **Methodical approach**: always write base case first
3. **Unit test mindset**: edge cases, null, empty inputs
4. Know **C#/Java** equivalences well
5. **System design** focuses on Azure: Cosmos DB, Blob Storage, Active Directory

### Sample Microsoft-Style Problem
> Design the file sync engine for OneDrive. We need to detect which files changed between the local machine and the cloud, and efficiently sync only the changed blocks. Minimise network transfer.

**Solution approach:** Merkle tree per directory. Each file is split into fixed-size blocks (e.g., 4KB). Build Merkle tree of file blocks. Compare root hashes between client and server. Use Merkle proof to identify differing blocks. Only download changed blocks.

---

## Apple

### Interview Focus
Apple emphasises performance, memory efficiency, and hardware-aware optimisation. Mobile constraints are frequently discussed.

### High-Frequency DS Questions

**Bloom Filter (High):**
- "Optimise Spotlight search indexing with Bloom filter"
- "Safari malicious URL detection"
- "Photos face recognition Bloom filter for caching"
- "Duplicate SMS detection with limited memory"

**Skip List (High):**
- "Design a disk-backed key-value store"
- "Implement a concurrent index for Core Data"
- "Range queries in CloudKit"

**Merkle Tree (Medium):**
- "Software update integrity verification"
- "iCloud Drive sync consistency"
- "App Store package authenticity check"
- "Time Machine backup verification"

**Trie (Medium):**
- "Keyboard autocomplete"
- "Siri suggestion trie"
- "Spotlight search index"

**Treap (Low):**
- "Ordered playlist with dynamic reordering"
- "Implicit treap for collaborative Notes app"

### Apple Interview Tips
1. **Memory constraints**: iPhone has limited RAM, mention cache-friendly design
2. **Battery impact**: mention power-aware optimisation (less computation = less battery drain)
3. **Privacy**: local processing vs server (on-device trie vs cloud)
4. **Metal/Swift** knowledge: mention integration with Swift Collection, Combine
5. **User experience**: 60fps UI means <16ms per operation

### Sample Apple-Style Problem
> Safari needs to detect malicious URLs while keeping the blocklist on-device. The blocklist has 10M entries. We have 1MB of storage for this purpose. Design the system.

**Solution approach:** Bloom filter. With 10M entries and 1% false positive, need ~12MB without optimisation. Use tiered Bloom: first tier with higher FP rate (2MB storage), second tier for confirmed positives. Or use XOR filters which are smaller. Cache misses to server (privacy-preserving via Private Set Intersection).

---

## General Cross-Company Strategy

### DS Frequency Summary

| Structure | Amazon | Apple 
| Structure | Amazon | Apple 
| Structure | Amazon | Apple | Meta | Microsoft | Google |
|-----------|--------|-------|------|-----------|--------|
| Trie | M | M | VH | M | VH |
| Bloom Filter | VH | H | M | L | H |
| Suffix Array | L | L | H | M | M |
| Fenwick Tree | L | L | M | H | M |
| Segment Tree | M | L | M | H | H |
| Skip List | H | H | L | L | M |
| Red-Black Tree | H | M | M | M | L |
| Treap | L | L | M | L | L |
| Union-Find | M | L | VH | M | VH |
| Merkle Tree | M | H | L | M | L |

VH = Very High (must know), H = High (likely), M = Medium (possible), L = Low (rare)

### Week Before Interview

For each company:
1. **Google**: Focus on trie, union-find, segment tree. Review amortised analysis.
2. **Amazon**: Focus on Bloom filter, skip list, RB tree. Review DynamoDB/Cassandra internals.
3. **Meta**: Focus on trie, union-find, suffix array. Review string algorithms.
4. **Microsoft**: Focus on Fenwick tree, segment tree, union-find. Review Azure storage.
5. **Apple**: Focus on Bloom filter, skip list, Merkle tree. Review on-device constraints.

### Day Before Interview

1. Re-read this guide's cheatsheet
2. Practice 2-3 medium problems from the focus area
3. Prepare 2-3 "tell me about a time" stories with DS context
4. Sleep well — these are thought-intensive interviews

### Day Of Interview

1. **Structure your answer**: clarify → brute force → optimal → complexity
2. **Keep calm**: common hint is given if you're on right track
3. **Test your code**: walk through with the example
4. **Ask good questions**: "Should we handle unicode?" "What's the expected QPS?"
5. **Interviewer feedback**: if they seem confused, ask "Should I clarify the approach?"