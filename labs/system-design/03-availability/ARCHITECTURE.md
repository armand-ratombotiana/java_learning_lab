# Availability - ARCHITECTURE

## High-Availability Deployment

```
                          ┌──────────────────┐
                          │  Route 53 (DNS)   │
                          │  Health-based     │
                          │  routing          │
                          └────────┬─────────┘
                                   │
                          ┌────────▼─────────┐
                          │  CloudFront (CDN) │
                          │  + WAF            │
                          └────────┬─────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
              ┌─────▼─────┐  ┌─────▼─────┐  ┌─────▼─────┐
              │ us-east-1 │  │ eu-west-1 │  │ ap-southeast-1 │
              │ Region A  │  │ Region B  │  │ Region C  │
              └─────┬─────┘  └─────┬─────┘  └─────┬─────┘
                    │              │              │
              ┌─────▼─────┐  ┌─────▼─────┐  ┌─────▼─────┐
              │ AZ 1      │  │ AZ 1      │  │ AZ 1      │
              │ LB + App  │  │ LB + App  │  │ LB + App  │
              │ DB Master  │  │ DB Replica│  │ DB Replica│
              └───────────┘  └───────────┘  └───────────┘
                    │
              ┌─────▼─────┐
              │ AZ 2      │
              │ App       │
              │ DB Replica │
              └───────────┘
```

## Resilience Components

| Component | Purpose | Tool |
|-----------|---------|------|
| Load Balancer | Distribute traffic across AZs | AWS ALB / NLB |
| Circuit Breaker | Stop calling failing services | Resilience4j |
| Bulkhead | Isolate thread pools | Resilience4j |
| Retry | Handle transient failures | Spring Retry |
| Timeout | Bounded waiting times | HTTP client config |
| Rate Limiter | Protect from overload | Resilience4j |
| Cache | Absorb load on failover | Redis / Caffeine |
| Health Checks | Detect unhealthy instances | Spring Actuator |

## Disaster Recovery Architecture

### Three-Region Setup
```
┌─────────────────────────────────────────────────────┐
│ Region 1 (Primary)   │  Region 2 (Warm)  │ Region 3 │
│ Active-Active        │  Read-only        │ Cold     │
│                      │                   │          │
│ App ──► DB Master    │  App ──► DB Repl  │ App off  │
│ App ──► DB Replica   │  App ──► DB Repl  │ DB off   │
└─────────────────────────────────────────────────────┘
│                      │                          │
└──────────────────────┴──────────────────────────┘
                    Traffic routed by DNS health check
```

## Runbook for Common Failures

| Failure | Detection | Response |
|---------|-----------|----------|
| App instance crash | LB health check fails | LB removes instance, AS replaces |
| DB master failure | Replica lag alert | Promote replica, update connection string |
| AZ outage | Multiple instances fail | Route53 to healthy region |
| Cache cluster down | Cache miss spike | Circuit breaker opens, fallback to DB |
| Upstream API slow | P99 latency spike | Circuit breaker opens, serve degraded |
