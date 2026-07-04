# Math Foundation: Connection Pool Sizing

## Little's Law
```
L = λ × W
```
- L = average number of connections in use
- λ = arrival rate (requests per second)
- W = average time a connection is held (seconds)

Example: 200 req/s, 50ms per query → 200 × 0.05 = 10 connections

## Optimal Pool Size
```
PoolSize = T × (C - 1)
```
- T = target throughput
- C = average query time in seconds

For a database handling 100 concurrent queries averaging 25ms:
```
PoolSize ≈ 100 × 0.025 = 2.5 (but at least 2-3 per core)
```

## Connection Wait Probability (M/M/c queue)
Given pool of size N, arrival rate λ, service rate μ:
```
P(wait) = (λ/μ)^N / (N! × (1 - λ/(N×μ))) × P0
```
Where P0 = probability pool is empty.

## Practical Guidelines
- Start with `maxPoolSize = CPU_cores × 2 + disk_spindles`
- For modern SSDs: `maxPoolSize = 10-30` (most applications)
- Monitor: pool utilization should be 70-80% at peak
- If 100% utilization with wait times: increase pool size
- If pool rarely exceeds 50%: decrease pool size
