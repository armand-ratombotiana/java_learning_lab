# Why CAP Theorem Matters

## Impact on System Design
- Every distributed database makes explicit CAP tradeoffs
- MongoDB (CP), Cassandra (AP), traditional RDBMS (CA in single cluster)
- Network partitions are inevitable in cloud environments

## Business Implications
- **Banking**: CP required - inconsistency means incorrect balances
- **Social Media**: AP preferred - stale likes are acceptable, downtime is not
- **E-Commerce**: Mixed - inventory must be consistent, product catalog can be eventually consistent

## Key Insight
The CAP theorem forces explicit acknowledgment of tradeoffs rather than pretending all properties can be achieved simultaneously.
