# Performance — Sorting Basics

## Empirical Performance

| Algorithm | n=10 | n=100 | n=1,000 | n=10,000 |
|-----------|------|-------|---------|----------|
| Bubble Sort | <1μs | 15μs | 1.5ms | 150ms |
| Selection Sort | <1μs | 10μs | 1.0ms | 100ms |
| Insertion Sort | <1μs | 8μs | 0.8ms | 80ms |

## When to Use

- **Bubble Sort**: Never in production
- **Selection Sort**: When minimizing swaps is critical (e.g., EEPROM)
- **Insertion Sort**: Small arrays (n < 50), nearly-sorted data
