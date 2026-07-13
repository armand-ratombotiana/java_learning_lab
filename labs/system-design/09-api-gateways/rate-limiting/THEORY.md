# Rate Limiting Theory & Intuition

## 💡 The Problem: Unbounded Traffic
An API Gateway is the front door to your microservices. If you expose a public API, you face several threats:
1. **Denial of Service (DoS/DDoS)**: Malicious actors can flood your API with millions of requests per second, overwhelming your backend servers and crashing the database.
2. **Resource Exhaustion**: Even legitimate users (e.g., a poorly written script by a partner company) can accidentally send too much traffic, starving other users of resources.
3. **Cost Control**: If your backend autoscales (e.g., AWS Lambda), a traffic spike can result in a massive, unexpected cloud bill.
4. **Monetization**: You might want to offer tiered pricing (e.g., Free tier: 100 requests/day, Pro tier: 10,000 requests/day).

## 🛡️ The Solution: Rate Limiting
Rate limiting controls the rate of traffic sent or received by a network interface. If a user exceeds their allowed limit, the API Gateway rejects the request (usually returning an `HTTP 429 Too Many Requests` status code).

Rate limiting is almost always implemented at the API Gateway layer, before the traffic ever reaches the backend microservices. This protects the internal network from congestion.

## 🌐 Distributed Rate Limiting
In a modern architecture, you don't have just one API Gateway server; you might have 10 instances running behind a load balancer. 
If User A is allowed 10 requests per minute, and they send 10 requests to Gateway Instance 1, and then 10 requests to Gateway Instance 2, how do the instances coordinate?

**The Redis Solution**:
Instead of keeping the rate limit counters in the local RAM of each Gateway instance, the instances use a centralized, fast, in-memory store like Redis. 
When a request arrives, the Gateway executes a Lua script in Redis to atomically check the user's current count and increment it. Redis guarantees that the count is consistent across all Gateway instances.