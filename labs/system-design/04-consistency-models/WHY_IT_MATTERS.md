# Consistency Models - WHY IT MATTERS

## Business Impact

### Real-World Failures
- **Amazon DynamoDB outage (2015)**: Eventual consistency caused read-after-write inconsistencies for hours
- **Uber's split-brain (2015)**: Two databases drifted apart, causing payment and trip history corruption
- **GitHub (2021)**: Replication lag caused 503 errors and data inconsistencies

### Cost of Inconsistency
| Scenario | Impact |
|----------|--------|
| Bank transfer shows wrong balance | Regulatory fines, customer lawsuits |
| Shopping cart items disappear | Lost revenue, user frustration |
| Calendar appointment double-booked | Scheduling conflicts, SLA breaches |
| Inventory shows wrong count | Overselling, fulfillment failure |

## Key Reasons It Matters

### 1. User Trust
Users must trust that what they see is real. Stale data erodes confidence.

### 2. Correctness
Business logic assumes data consistency. Violations cause bugs that are hard to reproduce.

### 3. Legal Compliance
Financial regulations (Sarbanes-Oxley), healthcare (HIPAA) require audit trails and consistent records.

### 4. Development Complexity
With weak consistency, developers must handle conflict resolution, tombstone records, and reconciliation.

### 5. Operational Cost
Strong consistency is expensive. Choosing the right model saves 50-80% on infrastructure.

## Choosing the Right Model

| Application | Model Needed | Example |
|------------|-------------|---------|
| Bank transfer | Strong (CP) | PostgreSQL synchronous replication |
| Social feed | Eventual (AP) | Cassandra, DynamoDB |
| Shopping cart | Read-after-write | Redis with WAIT command |
| Comment system | Eventual | MongoDB with causal consistency |
| DNS | Eventual | Multi-master DNS |
| Stock trading | Linearizable | Spanner (TrueTime) |
