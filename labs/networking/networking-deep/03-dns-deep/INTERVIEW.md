# Interview Questions — DNS Deep

## Beginner

Q: What is the difference between recursive and authoritative DNS?
A: Recursive resolver queries multiple servers on behalf of client; authoritative server provides answers for domains it manages.

Q: What is DNSSEC?
A: DNSSEC adds cryptographic signatures to DNS records for data integrity and origin authentication.

## Intermediate

Q: How does DNS resolution work step-by-step?
A: Stub resolver queries local resolver, which checks cache, queries root servers, TLD servers, authoritative servers, returns answer.

Q: What is DNS over HTTPS and why use it?
A: Encrypts DNS queries in HTTPS to prevent eavesdropping and manipulation, improves privacy.

## Advanced

Q: How does anycast work for DNS and what are the benefits?
A: Multiple servers share same IP, BGP routes to closest one. Benefits: load distribution, DDoS resilience, lower latency.

Q: How would you design a DNS architecture for a global SaaS with multi-region failover?
A: Anycast DNS with health-probe-driven routing, GeoDNS for regional affinity, weighted records for traffic distribution.
