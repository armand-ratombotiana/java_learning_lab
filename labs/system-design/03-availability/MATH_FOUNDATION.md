# Availability - MATH FOUNDATION

## Calculating System Availability

### Series (Chain) Availability
When components are in series, availability multiplies:
```
A_chain = A_1 × A_2 × ... × A_n
```

If three services each have 99.9% availability:
```
A = 0.999 × 0.999 × 0.999 = 0.997 = 99.7%
```

That drops from 8.76 hours downtime to 26.28 hours — 3x worse.

### Parallel (Redundant) Availability
With N redundant components in parallel:
```
A_parallel = 1 - (1 - A_1) × (1 - A_2) × ... × (1 - A_n)
```

Two servers at 99% each:
```
A = 1 - (0.01 × 0.01) = 1 - 0.0001 = 99.99%
```

## Mean Time Between Failures (MTBF)

MTBF = Total operating time / Number of failures

For a fleet of 100 servers running 1 year with 5 failures:
```
MTBF = (100 × 365 × 24) / 5 = 175,200 hours ≈ 20 years
```

## Mean Time To Repair (MTTR)

MTTR = Total repair time / Number of repairs

```
Availability = MTBF / (MTBF + MTTR)
```

If MTBF = 100 hours and MTTR = 1 hour:
```
Availability = 100 / 101 = 99.01%
```

Improving MTTR matters as much as improving MTBF:
```
MTTR = 0.5 hours  →  A = 99.5%
MTTR = 0.1 hours  →  A = 99.9%
```

## N-Plus-2 Redundancy

For `N` required instances, run `N + 2` to survive two simultaneous failures.

```
Run = N + 2
Extra cost = 2/N × 100%
```

For N=10: 20% extra capacity allows 2 failures without impact.

## Failure Probability

For a system with 100 servers, each with 1% failure rate per year:
```
P(no failures) = 0.99^100 = 36.6%
P(1+ failures) = 63.4%
```

At scale, failures are not exceptional — they're expected daily.

## Cost of High Availability

```
Cost ∝ 10^(9s - 2)
```

- 99.9%: Cost factor 1x (baseline)
- 99.99%: Cost factor ~10x
- 99.999%: Cost factor ~100x
- 99.9999%: Cost factor ~1000x
