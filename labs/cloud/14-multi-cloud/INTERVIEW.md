# Interview Questions - Multi-Cloud

## Beginner Level

**Q1**: What is Multi-Cloud and why is it important?
**A**: Multi-Cloud is a fundamental concept addressing the challenges of building distributed systems at scale.

**Q2**: How do you handle errors in Multi-Cloud implementations?
**A**: Use specific exception types, try-with-resources for cleanup, and proper logging with context.

**Q3**: What is the difference between a library and a service?
**A**: A library is linked into your application; a service is a separate process over the network.

## Intermediate Level

**Q4**: How would you design a fault-tolerant Multi-Cloud system?
**A**: Circuit breakers, retries with backoff, bulkheads, and graceful degradation.

**Q5**: What metrics would you collect and why?
**A**: Request rate, error rate, latency distribution (p50/p95/p99), resource utilization.

**Q6**: How do you ensure backward compatibility?
**A**: Semantic versioning, maintain deprecated endpoints, add adapters for old clients.

## Advanced Level

**Q7**: How would you implement distributed tracing across services?
**A**: OpenTelemetry instrumentation, propagate trace context via headers, export to tracing backend.

**Q8**: Design a multi-cloud deployment strategy. Key considerations?
**A**: Provider abstraction, stateless design, consistent pipelines, network latency, data sovereignty.

**Q9**: How would you handle a 10x traffic spike?
**A**: Auto-scaling, rate limiting, load shedding, caching, async processing, circuit breakers.

## System Design

**Q10**: Design a Multi-Cloud system for 1M requests/day.
**A**: Horizontal scaling, CDN for static content, read replicas, caching, async for non-critical paths.
