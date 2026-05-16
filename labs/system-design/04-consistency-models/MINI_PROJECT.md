# Consistency Models - MINI PROJECT

## Project: Distributed Cache with Tunable Consistency

Build a distributed cache demonstrating different consistency models.

## Requirements

- 5 node cluster
- Configurable read/write quorums
- Vector clocks for conflict detection
- Circuit breaker for node failures

## Implementation

### Architecture

```
Client
  │
  ▼
┌─────────────────────────────────┐
│      Quorum Manager             │
│  ┌─────┬─────┬─────┬─────┬─────┐ │
│  │ N1  │ N2  │ N3  │ N4  │ N5  │ │
│  └─────┴─────┴─────┴─────┴─────┘ │
└─────────────────────────────────┘
```

### Key Components

1. **QuorumManager**: Configurable W/R quorums
2. **VectorClockStore**: Causality tracking
3. **ConflictResolver**: LWW or custom resolution
4. **CircuitBreakerManager**: Node health monitoring

## Testing Scenarios

1. **Strong consistency**: W=3, R=3 (W+R > 5)
2. **Eventual consistency**: W=2, R=2 (W+R < 5)
3. **Node failure**: Simulate node down, verify circuit breaker
4. **Concurrent writes**: Two clients write simultaneously, verify resolution

## Deliverables

- [ ] Quorum-based read/write with configurable quorums
- [ ] Vector clock tracking
- [ ] Conflict resolution (LWW and custom)
- [ ] Circuit breaker for node failures
- [ ] Unit tests for all consistency scenarios