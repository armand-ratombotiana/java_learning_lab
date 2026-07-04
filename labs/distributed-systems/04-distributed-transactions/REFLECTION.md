# Distributed Transactions: Reflection

## Key Insights
- Strong consistency is expensive in distributed environments
- SAGA sacrifices atomic visibility for availability
- Compensating transactions require careful design
- Idempotency is the most important property for reliability

## Questions
1. Can you redesign your system to avoid distributed transactions?
2. What's the business cost of inconsistency vs unavailability?
3. Are your compensations truly idempotent?
4. How do you test distributed transaction reliability?

## Personal Notes
- Distributed transactions are a last resort, not a default choice
- Event-driven approaches often eliminate need for distributed transactions
- Testing failure scenarios is harder than implementing the happy path
