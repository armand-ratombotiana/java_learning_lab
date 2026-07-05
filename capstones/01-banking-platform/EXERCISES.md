# Exercises: Banking Platform

## Beginner
1. Implement a new `GET /api/v1/accounts/{id}/transactions` endpoint in Account Service
2. Add a new fraud rule: reject transactions > $50,000 regardless of score
3. Create a simple health check endpoint for each service

## Intermediate
4. Implement the Saga compensation: if notification fails after payment, rollback
5. Add a Materialized View table for daily transaction summaries
6. Implement a circuit breaker between Payment Service and Fraud Service
7. Write integration tests for the complete payment flow

## Advanced
8. Implement distributed tracing with OpenTelemetry across all services
9. Add a new `loan-service` that integrates with Account Service for credit checks
10. Build a real-time dashboard showing transaction throughput and fraud rate
11. Implement Blue-Green deployment strategy for zero-downtime updates
