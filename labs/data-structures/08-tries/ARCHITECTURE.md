# Architecture: Tries in System Design

## Autocomplete Architecture

```
User Input
    ↓
Prefix Normalizer (lowercase, trim)
    ↓
Trie Lookup (O(L))
    ↓
Candidate Collection (DFS from prefix node)
    ↓
Ranking (frequency, recency, personalization)
    ↓
Top K Results (e.g., 5 suggestions)
    ↓
Response
```

## Router (CIDR) Architecture

```
Incoming Packet (destination IP: 192.168.1.1)
    ↓
Binary Trie Lookup (bits of IP address)
    ↓
Longest Prefix Match (e.g., 192.168.0.0/16)
    ↓
Next Hop / Forwarding Decision
```

Routers use **binary tries** (Patricia tries / radix trees) for IP prefix matching. Each bit of the IP address determines left/right traversal. The node with the longest matching prefix wins.

## Search Engine Architecture

```
Documents → Tokenization → Trie of Terms
                                ↓
Search Query → Prefix Lookup → Matching Terms
                                ↓
    Autocomplete Suggestions ← Posting Lists
```

## Spell Check Architecture

```
Input Word
    ↓
Trie Exact Search → Found? → Return
    ↓ (not found)
Levenshtein Distance (within threshold)
    ↓
Generate Candidates (edits + trie lookup)
    ↓
Rank by distance + frequency
    ↓
Top Suggestions
```

## Distributed Trie

```java
// Partition by prefix range
// "a-m" shard, "n-z" shard
// Each shard has its own trie
// Coordinator routes queries to appropriate shard(s)
```

## Java Ecosystem

- **Apache Commons Collections 4**: `PatriciaTrie` — production-ready compressed trie
- **Concurrent-Trees**: third-party library with lock-free trie implementations
- **Lucene**: uses terms index (similar to trie) for search engine
- **ICU4J**: Unicode text processing with trie-like structures
- **Elasticsearch**: uses tries for autocomplete (edge-ngrams index)

## System Design Considerations

- **Memory**: tries are memory-intensive; consider radix trees for memory-constrained environments
- **Concurrent access**: use read-write locks or concurrent hash map children for thread safety
- **Persistence**: serialize trie to disk (binary format or double-array) for restart recovery
- **Sharding**: partition by prefix to distribute load across servers
- **Caching**: cache hot prefixes in front of trie for frequently searched prefixes
