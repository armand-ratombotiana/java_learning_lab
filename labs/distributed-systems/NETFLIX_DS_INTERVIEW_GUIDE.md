# Netflix Distributed Systems Interview Guide

> Complete preparation guide for distributed systems roles at Netflix.

---

## How Netflix Tests Distributed Systems

Netflix's interview process focuses on cloud infrastructure at global scale, chaos engineering, and streaming technology.

### Interview Rounds

1. **Technical Phone**: Coding + systems design (45-60 min)
2. **System Design 1**: Large-scale infrastructure (60 min)
3. **System Design 2**: Chaos engineering focus (60 min)
4. **Behavioral**: Freedom & Responsibility (60 min)
5. **Hiring Manager**: Team alignment (45 min)

### Netflix's Unique DS Focus

- **Cloud-native**: Deep AWS knowledge, microservices
- **Chaos Engineering**: Failure injection, resilience testing
- **Streaming**: CDN (Open Connect), video encoding, DRM
- **Data Platform**: Genie, Spinnaker, Atlas, Eureka
- **Freedom & Responsibility**: Bold decisions, no approval needed

### Top 15 Questions

1. **Design Open Connect CDN** - ISP-embedded, predictive caching
2. **Design Recommendation System** - ML pipeline, A/B testing
3. **Design Chaos Engineering Platform** - Chaos Monkey, Simian Army
4. **Design Video Encoding Pipeline** - Per-title encoding, VMAF
5. **Design Zuul API Gateway** - Edge services, routing, filters
6. **Design Hystrix Circuit Breaker** - Resilience patterns
7. **Design Eureka Service Discovery** - Registry, heartbeats
8. **Design Content Pipeline** - Studio to screen delivery
9. **Design Traffic Management** - DNS routing, failover
10. **Design Data Platform** - Genie, Spark, Flink pipelines
11. **Design Adaptive Bitrate Algorithm** - Client-side ABR
12. **Design Video Thumbnail Service** - Frame extraction
13. **Design Subscription Management** - Billing, entitlements
14. **Design Customer Support Platform** - Request routing
15. **Design A/B Testing Platform** - Experimentation at scale

### Evaluation Criteria

- **Judgment**: Making right tradeoffs independently
- **Impact**: Quantified results, bold decisions
- **Innovation**: Novel solutions to hard problems
- **Communication**: Clear, concise, opinionated

### Key LeetCode Problems

| Problem | # | Netflix Relevance |
|---------|---|-----------------|
| Design Hit Counter | 362 | View counting |
| LRU Cache | 146 | CDN caching |
| Design Twitter | 355 | Feed/recommendation |
| Top K Frequent | 347 | Popular content |
| Number of Islands | 200 | Cell isolation |

### Study Plan

- Read Netflix Tech Blog (medium.com/netflix-techblog)
- Understand Chaos Monkey, Hystrix, Eureka
- Study CDN architecture: Open Connect
- Learn video encoding basics (H.264, H.265, VP9, AV1)
- Practice circuit breaker and bulkhead patterns

---

> **Netflix Tip**: Netflix interviewers value bold, well-reasoned opinions. "I believe strongly that..." is a valid way to start a design discussion. Back it with experience and data.