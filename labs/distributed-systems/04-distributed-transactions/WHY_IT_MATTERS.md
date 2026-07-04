# Why Distributed Transactions Matter

## Business Impact
- **Data integrity**: Prevents orphaned records, double charges, lost updates
- **Customer trust**: Reliable operations build confidence
- **Compliance**: Auditing requires transactional guarantees

## Performance Tradeoffs
- 2PC: Strong guarantees but blocks during coordinator failure
- SAGA: Better availability but eventual consistency
- XA: Standardized but heavyweight

## Key Insight
No single approach works for all scenarios. The best protocol depends on failure tolerance, latency requirements, and consistency needs.
