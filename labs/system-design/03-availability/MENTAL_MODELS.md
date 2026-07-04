# Availability - MENTAL MODELS

## Mental Model 1: The Airplane
- **Engine redundancy**: Multiple engines (any one fails, plane flies)
- **Active-Passive**: Backup pilot takes over if primary is incapacitated
- **Checklists**: Standard procedures for each failure mode (like runbooks)
- **Black box**: Post-incident analysis (like chaos engineering)

## Mental Model 2: The Electrical Grid
- **Redundant paths**: Multiple power lines to each neighborhood
- **Circuit breakers**: Trip when fault detected, isolate problem area
- **Load shedding**: Drop non-critical loads to prevent grid collapse
- **Backup generators**: Active-passive failover (UPS + generator)

## Mental Model 3: The Hospital
- **ER triage**: Prioritize critical patients (circuit breaker prioritization)
- **Code Blue**: Emergency team assembles (incident response)
- **Life support**: Keep core functions running (minimal viable system)
- **Quarantine**: Isolate infection to prevent spread (bulkhead pattern)

## Availability Patterns in One Line

| Pattern | What It Does | Mental Image |
|---------|-------------|-------------|
| Load Balancer | Distribute traffic | Toll plaza with many lanes |
| Circuit Breaker | Stop calling failing services | Electrical breaker tripping |
| Bulkhead | Isolate failures into compartments | Ship watertight compartments |
| Retry | Automatic retry on transient failure | Press elevator button again |
| Health Check | Verify instance is alive | Pulse check on patient |
| Rate Limiter | Protect from overload | Bouncer at club door |

## The 9s Framework

```
99% (2 9s)    = 87.6h downtime/year  ──► Small app, weekends OK
99.9% (3 9s)  = 8.76h downtime/year  ──► Startup / SaaS MVP
99.99% (4 9s) = 52min downtime/year  ──► Enterprise production
99.999% (5 9s)= 5min downtime/year   ──► Telecom, financial services
99.9999% (6 9s)= 31sec downtime/year ──► Air traffic control
```
Each additional 9 costs roughly 10x more infrastructure.
