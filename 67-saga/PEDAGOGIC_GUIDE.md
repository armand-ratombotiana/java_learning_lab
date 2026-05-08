# Pedagogic Guide - Saga Pattern

## Learning Path

### Phase 1: Fundamentals
1. Understand distributed transactions
2. Learn saga vs 2-phase commit
3. Design compensating actions

### Phase 2: Intermediate
1. Choreography implementation
2. Orchestration approach
3. Timeout handling

### Phase 3: Advanced
1. Saga orchestrator frameworks
2. Recovery strategies
3. Monitoring and debugging

## Approaches

| Approach | Description |
|----------|-------------|
| Choreography | Services emit/consume events |
| Orchestration | Central coordinator |

## Compensating Actions
Each step has a corresponding undo:
- Payment -> Refund
- Inventory -> Release
- Order -> Cancel

## Best Practices
- Idempotent operations
- Timeouts on each step
- Compensate in reverse order
- Log saga state