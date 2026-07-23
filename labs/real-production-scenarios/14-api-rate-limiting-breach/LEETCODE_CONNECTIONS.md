# Lab 14 — API Rate Limiting: LeetCode Connections

**Q1: Design a Rate Limiter (LeetCode 359)** — The canonical rate limiting problem — sliding window, fixed window, token bucket.

**Q2: Logger Rate Limiter (LeetCode 359)** — Same as rate limiter — sliding window per message.

**Q3: Design Hit Counter (LeetCode 362)** — Count requests in time windows for rate limit tracking.

**Q4: Design a HashMap (LeetCode 706)** — Rate limit state is a hash map (client ID → counter/timestamp).

**Q5: Design In-Memory File System (LeetCode 588)** — Hierarchical rate limits (global → region → service → user).

**Q6: Design a Leaderboard (LeetCode 1244)** — Rate limit quotas per customer tier (free/pro/enterprise).

**Q7: Design a Web Crawler (LeetCode 1242)** — Crawlers must respect rate limits (robots.txt, per-domain limits).

**Q8: Design a Parking System (LeetCode 1603)** — Resource allocation (parking spots = tokens in token bucket).

**Q9: Design Bounded Blocking Queue (LeetCode 1188)** — Queue-based rate limiting with backpressure.

**Q10: Traffic Light Controlled Intersection (LeetCode 1279)** — State machine for traffic control — green (allow) / red (block).
