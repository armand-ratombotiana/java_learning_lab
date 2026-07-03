# Performance — Complexity Classes

| Class | n=10 | n=100 | n=1,000 | n=10⁶ |
|-------|------|-------|---------|-------|
| O(1) | 1 | 1 | 1 | 1 |
| O(log n) | 3 | 7 | 10 | 20 |
| O(n) | 10 | 100 | 1,000 | 10⁶ |
| O(n log n) | 33 | 700 | 10,000 | 20×10⁶ |
| O(n²) | 100 | 10,000 | 10⁶ | 10¹² |
| O(2ⁿ) | 1,024 | 10³⁰ | — | — |

## Practical Guidelines
- n < 10: O(n!) is fine
- n < 100: O(n³) is fine
- n < 10,000: O(n²) is acceptable
- n < 10⁶: O(n log n) is expected
- n > 10⁶: O(n) or better is needed
