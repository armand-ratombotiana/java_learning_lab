# Uber Distributed Systems Interview Guide

> Complete preparation guide for distributed systems roles at Uber.

---

## How Uber Tests Distributed Systems

Uber's interview process focuses on real-time distributed systems at global scale. Geospatial data processing is a key differentiator.

### Interview Rounds

1. **Phone Screen**: Coding (45 min)
2. **Coding**: Algorithms (45 min)
3. **System Design**: Real-time infrastructure (60 min)
4. **Domain Specific**: Kafka, Spark, geospatial (45 min)
5. **Behavioral**: Customer obsession, reliability (45 min)

### Uber's Unique DS Focus

- **Real-time Dispatch**: Matching drivers to riders under 100ms
- **Geospatial**: H3 hex grid, geohashing, spatial indexing
- **State Management**: Schemaless (Uber's NoSQL), Ringpop
- **Event Streaming**: Kafka at massive scale (millions of events/sec)
- **Marketplace**: Supply-demand forecasting, surge pricing

### Top 15 Questions

1. **Design Ride Matching** - Geohashing, bipartite matching, dispatch optimization
2. **Design H3 Geospatial Index** - Hex grid hierarchy, aggregation
3. **Design Real-time Map** - Driver location, WebSocket/SSE, Kafka streams
4. **Design Uber Eats** - Restaurant discovery, order dispatch, ETA prediction
5. **Design Payment Platform** - Multi-currency, provider abstraction, idempotency
6. **Design Trip Service** - State machine, event-driven, Schemaless
7. **Design Pricing Engine** - Supply-demand curves, surge detection
8. **Design Notification Service** - Multi-channel (push, SMS, email), priority queue
9. **Design Fraud Detection** - Real-time ML, risk scoring, rule engine
10. **Design OLAP Platform** - Presto/Hive for analytics, query federation
11. **Design Driver Allocation** - Assignment algorithm, batching optimization
12. **Design ETA Computation** - Historical + real-time traffic, ML models
13. **Design Driver/Payout System** - Split earnings, weekly settlements
14. **Design Rider Rating System** - Bi-directional ratings, fraud detection
15. **Design Marketplace Forecasting** - Demand prediction for driver supply

### Evaluation Criteria

- **Real-time**: Sub-second decision making critical
- **Reliability**: No downtime (riders depend on Uber 24/7)
- **Scalability**: From 1 to 1M concurrent users
- **Data-driven**: Every feature has metrics

### Key Systems to Study

- H3: Hexagonal hierarchical geospatial indexing system
- Ringpop: Consistent hash ring for RPC routing
- Schemaless: Uber's NoSQL database (MySQL + DocStore)
- Peloton: Resource-aware job scheduler
- Marmaray: Data ingestion and processing framework

### Key LeetCode Problems

| Problem | # | Uber Relevance |
|---------|---|--------------|
| Reconstruct Itinerary | 332 | Route reconstruction |
| Network Delay Time | 743 | ETA computation |
| Cheapest Flights K Stops | 787 | Multi-hop routing |
| Bus Routes | 815 | Multi-leg trips |
| Minimum Path Sum | 64 | Route optimization |

### Study Plan

- Understand H3 hex grid architecture
- Study Ringpop for distributed routing
- Learn Uber's Schemaless database design
- Practice real-time system design patterns
- Study marketplace economics basics

---

> **Uber Tip**: Uber interviewers focus on real-time constraints. Every design must account for sub-100ms response times. Discuss tradeoffs between batch and streaming processing.