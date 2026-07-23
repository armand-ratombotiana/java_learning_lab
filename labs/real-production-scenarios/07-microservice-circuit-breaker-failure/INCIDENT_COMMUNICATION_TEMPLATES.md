# Lab 07 — Circuit Breaker: Communication Templates

## Initial Alert

```
Title: [SEV2] Circuit Breaker OPEN for Payment Service
Service: Order Service → Payment Service downstream
Severity: SEV2

Metrics:
- Payment circuit: OPEN for 5 minutes
- Fallback enabled: order queued for retry
- Payment error rate: 100% (circuit open)
- Order processing: degraded (payments deferred)

Impact: Users can place orders but payment will be processed later
Action: Investigate payment service health
```

## Status Updates

```
STATUS #1 — Payment Circuit OPEN

Payment service is unresponsive (timeout on 95% of calls).
Circuit breaker opened at 14:30 UTC.
Orders are being accepted with deferred payment.

Payment team investigating: DB connection pool issue.
Circuit will auto-retry in 30 seconds (HALF_OPEN).

Next update: 15 minutes or upon recovery.
```

```
STATUS #2 — Payment Circuit CLOSED

Payment service recovered (DB pool restarted).
Circuit breaker CLOSED at 14:35 UTC.
Normal payment processing resumed.
No orders lost — all deferred payments processed successfully.

Post-mortem: Payment team will follow up on DB pool root cause.
```
