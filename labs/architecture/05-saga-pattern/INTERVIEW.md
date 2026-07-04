# Saga Pattern Interview Questions

## Junior Level

### Q: What is a saga?
**A:** A saga is a sequence of local transactions. Each transaction updates data within a single service. If a transaction fails, the saga executes compensating transactions to undo previous changes.

### Q: What is the difference between choreography and orchestration sagas?
**A:** Choreography uses events - each service publishes events that others consume. There's no central coordinator. Orchestration uses a coordinator that sends commands to each service and tracks progress.

## Mid Level

### Q: How do you ensure compensating transactions work correctly?
**A:** 
1. Design each step to be idempotent
2. Store saga state persistently
3. Execute compensations in reverse order (LIFO)
4. Make compensations idempotent too
5. Log all compensation attempts
6. Monitor compensation success rate

### Q: What happens if a compensating transaction fails?
**A:** Implement retry with exponential backoff. If persistent failure occurs, log the failure for manual intervention. Use a dead letter queue for failed compensations. Consider implementing a "saga doctor" service that periodically retries failed compensations.

## Senior Level

### Q: Design a saga for a flight booking system.
**A:**
Steps: Book flight -> Reserve hotel -> Book car -> Charge customer
Compensations: Refund car -> Release hotel -> Cancel flight
- Orchestrator coordinates the saga
- Each step is idempotent with a timeout
- Saga state persisted in a saga store
- Monitoring tracks step duration and compensation rate
- Manual intervention process for unrecoverable failures
- Circuit breaker for unreliable downstream services

### Q: How would you test a saga implementation?
**A:**
1. Unit test each step individually
2. Test saga state machine transitions
3. Test success flow end-to-end
4. Test failure at each step with compensation
5. Test timeout scenarios
6. Test duplicate event delivery
7. Test saga store recovery after crash
8. Chaos engineering - inject failures at each step
