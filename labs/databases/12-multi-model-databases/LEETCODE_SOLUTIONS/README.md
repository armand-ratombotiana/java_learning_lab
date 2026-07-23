# LEETCODE_SOLUTIONS — Multi-Model Databases

## Polyglot Persistence Solutions

| LeetCode Problem | Best Database Model | Rationale |
|-----------------|--------------------|-----------|
| 175 Combine Tables | Relational (JOIN) | Structured relationship |
| 380 Randomized Set | Redis (Set) | O(1) random access |
| 706 Design HashMap | Redis (Hash) | Key-value with field access |
| 588 Design In-Memory FS | Document (tree) | Hierarchical structure |
| 139 Word Break | Graph (Trie) | String traversal |
| 642 Design Search Auto | Trie (Redis sorted set) | Prefix + ranking |

### Multi-Model Approach to LeetCode
- **Relational:** problems requiring JOINs (175, 181, 184, 185)
- **Key-Value:** cache-like problems (146 LRU, 362 Hit Counter)
- **Document:** nested/hierarchical data (588 FS)
- **Graph:** connectivity problems (social graph, shortest paths)
- **Time-Series:** sequential data (197 Rising Temperature)
