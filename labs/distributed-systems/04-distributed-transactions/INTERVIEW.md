# Distributed Transactions - Interview Preparation

> Key interview questions about distributed transaction patterns.

---

## Core Interview Questions

### Q1: Compare 2PC, 3PC, SAGA, and TCC
**Answer**: 2PC has prepare + commit phases; blocking if coordinator fails. 3PC adds pre-commit phase to be non-blocking. SAGA breaks long transactions into steps with compensating actions; no locks held. TCC (Try-Confirm/Cancel) reserves resources in Try phase then confirms or cancels.

### Q2: When would you use SAGA over 2PC?
**Answer**: SAGA for long-running transactions where locks are unacceptable (microservices, e-commerce orders). 2PC for short transactions requiring ACID (financial transfers). SAGA sacrifices atomicity for availability.

### Q3: Explain the SAGA pattern with an example
**Answer**: E-commerce order: create order (step 1), reserve payment (step 2), reserve inventory (step 3), ship (step 4). If step 3 fails, compensate: release payment (step 2 compensate), cancel order (step 1 compensate). Implemented via choreography (events) or orchestration (coordinator).

### Q4: What is the "phantom commit" problem in 2PC?
**Answer**: When the coordinator decides to commit but fails before all participants receive the commit. Participants that received commit have committed; those that didn't are stuck in prepared state and must be manually resolved.

### Q5: How does Spanner implement distributed transactions?
**Answer**: Spanner uses TrueTime for external consistency, Paxos for replication within zones, and 2PC for cross-zone transactions. The coordinator leader is the Paxos leader of the first participant. Lock tables prevent conflicts.

## Company-Specific Focus

| Company | Transaction Focus |
|---------|------------------|
| Google | "Explain Spanner's 2PC + Paxos hybrid" |
| Amazon | "Does DynamoDB support transactions? (Yes - since 2018)" |
| Stripe | "How do you ensure exactly-once in payment transactions?" |
| Apple | "How does CloudKit handle multi-device transaction conflicts?" |

## LeetCode Connections

| Problem | # | Transaction Concept |
|---------|---|-------------------|
| Course Schedule | 207 | 2PC dependency resolution |
| Course Schedule II | 210 | SAGA step ordering |
| Alien Dictionary | 269 | Total order of transactions |
| Task Scheduler | 621 | Resource scheduling |

## System Design Connections

- **Design a Payment System**: Use SAGA with compensating actions
- **Design a Booking System**: Use TCC to reserve inventory
- **Design a Distributed Database**: Use 2PC for cross-shard transactions
- **Design a Bank Transfer**: 2PC or distributed ledger with SAGA

> **Key Insight**: Always discuss failure scenarios and recovery mechanisms when describing transaction patterns.