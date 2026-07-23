# Lab 07 — Circuit Breaker: LeetCode Connections

**Q1: Design Bounded Blocking Queue (LeetCode 1188)**
Connection: Circuit breaker's thread pool isolation (bulkhead) is a bounded blocking queue. Threads waiting for downstream are like consumers waiting for work. When circuit opens, consumers are rejected — like a full queue.

**Q2: The Dining Philosophers (LeetCode 1226)**
Connection: Circuit breaker prevents the distributed version of the dining philosophers — if downstream service is "fork" and requests are "philosophers," the circuit breaker prevents all philosophers from starving by failing fast when a fork is unavailable.

**Q3: Web Crawler Multithreaded (LeetCode 1242)**
Connection: A crawler with fallback URLs is like circuit breaker with fallback. If primary URL fails, use secondary. The crawler's rate limiting and failure handling map directly to circuit breaker patterns.

**Q4: Traffic Light Controlled Intersection (LeetCode 1279) — State Machine**
Connection: Circuit breaker is a state machine (CLOSED → OPEN → HALF_OPEN → CLOSED). Traffic light is also a state machine (GREEN → YELLOW → RED → GREEN). Both manage access to a shared resource with explicit state transitions.

**Q5: Design a Leaderboard (LeetCode 1244) — Fallback Pattern**
Connection: When primary data source fails, circuit breaker triggers fallback (cached data). Leaderboard with cache fallback is the same pattern — if database is down, serve cached leaderboard.
