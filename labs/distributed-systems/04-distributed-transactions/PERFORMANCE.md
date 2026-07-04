# Distributed Transactions: Performance

## Protocol Performance

| Protocol   | Latency      | Throughput | Lock Duration | Scalability |
|------------|--------------|------------|---------------|-------------|
| 2PC        | 2 RTT        | Low        | Long          | Low         |
| 3PC        | 3 RTT        | Very Low   | Medium        | Low         |
| SAGA       | N RTT        | High       | None          | High        |
| XA         | 2 RTT + SQL  | Low        | Long          | Low         |

## Optimization Techniques

1. **Reduce coordinator involvement**: Use asynchronous SAGA
2. **Compensating transaction design**: Make compensations lightweight
3. **Batch processing**: Group transactions for throughput
4. **Read-only optimization**: Skip 2PC for read-only operations

## Bottlenecks
- Coordinator capacity
- Database lock contention
- Network round trips
