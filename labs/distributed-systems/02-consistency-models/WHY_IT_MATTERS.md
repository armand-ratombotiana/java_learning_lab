# Why Consistency Models Matter

## Business Impact
- **Strong consistency**: Essential for financial transactions, inventory management
- **Eventual consistency**: Acceptable for social feeds, product catalogs, analytics
- **Causal consistency**: Important for collaboration tools, commenting systems

## Developer Impact
- Strong consistency: Code behaves predictably, easier to debug
- Weak consistency: Requires careful handling of stale data, conflict resolution
- Wrong choice: Can lead to corrupted data or unacceptable performance

## Real-World Examples
- **Amazon DynamoDB**: Tuned consistency per request (strong or eventual)
- **Google Spanner**: External consistency (linearizability across data centers)
- **Cassandra**: Configurable consistency per operation
