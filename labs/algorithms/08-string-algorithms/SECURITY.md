# Security — String Algorithms

- Hash collision DoS: Attacker crafts strings with same hash → O(nm) worst case for Rabin-Karp
- Hash seed: Use random hash base/prime to prevent collision attacks
- Pattern injection: Ensure pattern length validation to prevent DoS
- Trie memory: Large alphabet trie can exhaust memory
- Regex injection: If pattern matching is regex-based, guard against ReDoS
