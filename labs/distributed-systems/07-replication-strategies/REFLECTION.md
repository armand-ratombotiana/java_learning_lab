# Replication: Reflection

## Key Insights
- Replication is the foundation of distributed storage reliability
- Tradeoffs between consistency, latency, and durability
- Async replication is fast but risks data loss
- Multi-leader requires careful conflict handling

## Questions
1. What's your current replication strategy and does it meet your RPO/RTO?
2. Have you tested leader failover scenarios?
3. Is your replication lag within acceptable bounds?
4. Could you reduce replication factor without compromising durability?

## Personal Notes
- Always test failover scenarios; they never work as expected first time
- Monitor replication lag as a critical production metric
- Simpler strategies (single-leader) often outperform complex ones
