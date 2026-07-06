# Performance — String Matching

## Theoretical Performance

| Algorithm | Preprocessing | Search (worst) | Search (avg) | Space |
|-----------|--------------|----------------|--------------|-------|
| Naive | O(1) | O(nm) | O(nm) | O(1) |
| KMP | O(m) | O(n) | O(n) | O(m) |
| Boyer-Moore | O(m+sigma) | O(n+m) | O(n/m) | O(m+sigma) |
| Rabin-Karp | O(m) | O(nm) | O(n+m) | O(1) |
| Z-algorithm | O(m) | O(n) | O(n) | O(n+m) |
| Aho-Corasick | O(total m) | O(n) | O(n) | O(total m) |

## Practical Benchmarks

For a 10MB English text file searching for a 5-character word:
- Naive: ~50ms
- KMP: ~10ms
- Boyer-Moore: ~2ms
- Rabin-Karp: ~15ms
- Z-algorithm: ~10ms

## Optimization Tips

- Use char[] instead of String for tight loops
- Precompute powers for rolling hash once
- For Boyer-Moore, precompute both heuristics upfront
- For Aho-Corasick, use lookup tables for small alphabets
