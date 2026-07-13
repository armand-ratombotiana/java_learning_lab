# Rate Limiting Algorithms

There are several standard algorithms used for rate limiting, each with different trade-offs regarding memory usage, strictness, and handling of traffic bursts.

## 1. Token Bucket (The Industry Standard)
Imagine a bucket that can hold a maximum of $B$ tokens. 
- A background process adds a new token to the bucket at a constant rate $R$ (e.g., 1 token per second).
- If the bucket is full, new tokens overflow and are discarded.
- When a request arrives, it must take 1 token out of the bucket to proceed.
- If the bucket is empty, the request is rejected.

**Pros**: Memory efficient (only needs to store the last refill timestamp and the current token count). Allows for sudden bursts of traffic (up to the bucket capacity $B$). Used heavily by AWS and Stripe.

## 2. Leaky Bucket
Imagine a bucket with a hole in the bottom.
- Requests arrive and are poured into the top of the bucket.
- The bucket leaks requests out the bottom at a strictly constant rate (e.g., 10 requests per second).
- If water (requests) pours in faster than it leaks out, the bucket fills up. If it overflows, new requests are rejected.

**Pros**: Smooths out traffic. The backend servers receive a perfectly steady, predictable stream of requests.
**Cons**: A sudden burst of traffic can fill the bucket with old requests, causing new, potentially more important requests to be rejected until the bucket drains.

## 3. Fixed Window Counter
Time is divided into fixed windows (e.g., 12:00 to 12:01, 12:01 to 12:02).
- A counter is maintained for each window.
- If the counter exceeds the limit, requests are rejected until the next window begins.

**Pros**: Very easy to implement in Redis using `INCR` and `EXPIRE`.
**Cons**: The **Burst at the Edge** problem. If the limit is 100 per minute, a user can send 100 requests at 12:00:59, and another 100 requests at 12:01:01. They successfully bypassed the limit, sending 200 requests in a 2-second span.

## 4. Sliding Window Log
Instead of fixed windows, we keep a timestamp for *every single request* in a sorted set (like Redis `ZSET`).
- When a new request arrives, we remove all timestamps older than 1 minute ago.
- If the number of remaining timestamps is less than the limit, the request is accepted and its timestamp is logged.

**Pros**: Perfectly accurate. Solves the edge burst problem completely.
**Cons**: Extremely memory intensive. If the limit is 1,000,000 requests per hour, you must store 1,000,000 timestamps per user in memory.

## 5. Sliding Window Counter
A hybrid approach that combines the low memory of Fixed Window with the accuracy of Sliding Window Log.
It assumes a constant rate of requests in the previous window to calculate a weighted sum of the previous window's count and the current window's count.