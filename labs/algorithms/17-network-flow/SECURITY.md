# Security — Network Flow

## Capacity Overflow

If capacities are provided from untrusted sources, they could cause integer overflow. Use long and validate capacities.

## Graph Size DoS

An attacker could specify a very large graph (millions of nodes) causing memory exhaustion. Validate n and edge count before processing.

## Resource Exhaustion

Pathological graphs can cause exponential iterations in Ford-Fulkerson with DFS. Always prefer Edmonds-Karp or Dinic for safety.

## Irrational Capacities

In theory, irrational capacities can cause non-termination in Ford-Fulkerson. Always use rational (integer) capacities or use a polynomial algorithm.
