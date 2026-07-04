# Availability - VISUAL GUIDE

## Circuit Breaker State Machine

```
                             +---------+
      +----------------------| CLOSED  |--------------------+
      |                      +----+----+                    |
      |                           |                          |
      |            failures >     |                          |
      |            threshold      |  success > threshold     |
      |                           v                          |
      |  +------------+     +----------+     +-----------+   |
      |  |   OPEN     |────►|HALF_OPEN |◄────|  CLOSED   |   |
      |  +-----+------+     +-----+----+     +-----------+   |
      |        |                  |                           |
      |        |    timeout       |  single failure           |
      |        +-------(wait)─────+                           |
      |                                                        |
      +--------------------------------------------------------+
```

## Active-Active Deployment

```
                     ┌─────────────────────┐
                     │  Global Load        │
                     │  Balancer (DNS)     │
                     └────────┬────────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
      ┌─────▼─────┐     ┌─────▼─────┐     ┌─────▼─────┐
      │ Region 1  │     │ Region 2  │     │ Region 3  │
      │ us-east-1 │     │ eu-west-1 │     │ ap-southeast-1 │
      └─────┬─────┘     └─────┬─────┘     └─────┬─────┘
            │                 │                 │
      ┌─────▼─────┐     ┌─────▼─────┐     ┌─────▼─────┐
      │ LB → App  │     │ LB → App  │     │ LB → App  │
      │ → DB (R/W)│     │ → DB (R)  │     │ → DB (R)  │
      └───────────┘     └───────────┘     └───────────┘
```

## Redundancy Patterns

```
Active-Active:         Active-Passive:        N+1 Redundancy:
┌──┐ ┌──┐ ┌──┐       ┌──┐   ┌──┐            ┌──┐ ┌──┐ ┌──┐ ┌──┐
│A1│ │A2│ │A3│       │P │   │S │            │N1│ │N2│ │N3│ │S1│
└──┘ └──┘ └──┘       └──┘   └──┘            └──┘ └──┘ └──┘ └──┘
All active            Primary + Standby      N active, 1 standby
```

## Health Check Response

```
GET /health → 200 OK

{
    "status": "UP",
    "components": {
        "db":       { "status": "UP" },
        "redis":    { "status": "UP" },
        "diskSpace":{ "status": "UP", "details": { "free": "500GB" } },
        "kafka":    { "status": "DOWN", "details": { "error": "connection refused" } }
    }
}
```
