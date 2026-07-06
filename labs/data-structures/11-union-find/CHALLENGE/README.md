# Challenge: Dynamic Graph with Edge Deletions

Extend DSU to support edge deletions in offline queries.

## Problem

Given a graph with N nodes and M edges, followed by Q queries of three types:
1. `ADD u v` — add edge between u and v
2. `REMOVE u v` — remove edge between u and v  
3. `QUERY u v` — are u and v connected?

## Constraints

- N up to 10^5
- M, Q up to 10^5
- Solve in O((N+M+Q) * alpha(N) * log N)

## Approach

Use divide-and-conquer on the timeline with a DSU that supports undo operations (persistent DSU). Process queries in a segment tree over time.
