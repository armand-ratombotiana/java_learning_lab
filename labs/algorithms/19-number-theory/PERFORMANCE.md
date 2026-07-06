# Performance — Number Theory

## Algorithm Comparison

| Algorithm | Complexity | Input Range |
|-----------|-----------|-------------|
| Euclidean GCD | O(log n) | Up to 2^63 |
| Extended GCD | O(log n) | Up to 2^63 |
| Simple Sieve | O(n log log n) | Up to 10^7 (memory bound) |
| Segmented Sieve | O(n log log n) | Up to 10^12 |
| Miller-Rabin (k rounds) | O(k log^3 n) | Arbitrary precision |
| Modular exponentiation | O(log exp) | Up to 2^63 |
| CRT | O(k log N) | k moduli |

## Benchmark Data

- GCD(10^12, 10^12-1): < 1 microsecond
- Sieve up to 10^7: ~100ms
- Miller-Rabin on 10^12: ~1 microsecond per base
- Modular exponentiation 2^10^9 mod p: ~1 microsecond

## Optimization Tips

- Use bit operations for modulus by power of 2
- Precompute powers for repeated modular exponentiation
- Use segmented sieve for range queries over 10^7
- Cache totient function values up to common limits
