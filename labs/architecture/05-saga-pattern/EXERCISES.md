# Saga Pattern Exercises

## Beginner Exercises

### Exercise 1: Simple Choreography Saga
Implement a 2-step saga using events:
- OrderCreated event triggers inventory reservation
- InventoryReserved event triggers payment
- PaymentFailed triggers inventory release

### Exercise 2: Compensating Action
Create a money transfer saga between two accounts:
- Debit source account
- Credit target account
- Compensation: credit back source account

## Intermediate Exercises

### Exercise 3: Axon Orchestration Saga
Implement a 3-step saga with Axon Framework:
1. Reserve hotel room
2. Book flight
3. Confirm booking
Compensation: cancel flight, release room

### Exercise 4: Saga with Timeout
Add timeout handling to a saga:
```java
@SagaTimeout(duration = 5, unit = ChronoUnit.MINUTES)
public class TravelBookingSaga {
    // Handle timeout with complete compensation
}
```

### Exercise 5: Parallel Steps
Implement a saga where two steps run in parallel:
- Reserve hotel AND check car rental simultaneously
- After both succeed, book flight
- If either fails, compensate

## Advanced Exercises

### Exercise 6: Saga Monitoring Dashboard
Create a monitoring system for sagas:
- Track active sagas
- Measure compensation rate
- Alert on stuck sagas
- Visualize saga flow

### Exercise 7: Multi-level Compensation
Implement a saga with nested compensating actions:
- Each step has its own sub-saga
- Sub-saga failures trigger parent compensation
- Ensure atomic compensation

### Exercise 8: Distributed Saga Store
Implement a saga store that works across data centers:
- Replicate saga state
- Handle split-brain scenarios
- Ensure saga continuity during failover
