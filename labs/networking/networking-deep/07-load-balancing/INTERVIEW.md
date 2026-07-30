# Interview Questions — Load Balancing

## Beginner

Q: What is the difference between Layer 4 and Layer 7 load balancing?
A: Layer 4 routes based on TCP/UDP info; Layer 7 inspects HTTP headers, cookies, application data.

Q: What is round-robin load balancing?
A: Distributes requests sequentially across backend servers.

## Intermediate

Q: How does consistent hashing work for load balancing?
A: Backends placed on hash ring; request key hashed to find nearest backend clockwise; ring change only affects N/1 of keys.

Q: What are health checks and why are they important?
A: Probes to determine backend availability; active (periodic requests) and passive (observe failures).

## Advanced

Q: Design a global load balancer for a multi-region application.
A: DNS-based GSLB with health monitoring, anycast VIP, geographic awareness, traffic steering ratios.

Q: How do you handle session persistence in a stateless architecture?
A: External session store (Redis, Memcached), JWT tokens for state, client-side sessions, distributed cache.
