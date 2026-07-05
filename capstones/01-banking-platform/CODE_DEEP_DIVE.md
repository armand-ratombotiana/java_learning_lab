# Code Deep Dive: Banking Platform

## Payment Processing (Payment Service)

The `PaymentProcessor` class handles the full payment lifecycle. It starts by creating a payment record with status `INITIATED`. An idempotency key is generated from the request fingerprint. The processor checks with Account Service for sufficient balance (including holds), then submits to Fraud Service for scoring. On approval, the transaction is committed and the ledger updated. On denial, a compensating release of holds is executed. The entire flow uses CompletableFuture for async orchestration with timeout per step.

## Account Ledger

`LedgerService` maintains an append-only ledger. Every credit/debit is an immutable `LedgerEntry` with a unique sequence number. Balance is computed by scanning entries newer than the last snapshot. Snapshots are taken every 1000 entries to prevent unbounded replay. Concurrent balance updates use optimistic locking with version fields.

## Fraud Rule Engine

`RuleEngine` evaluates a chain of rules: amount thresholds, velocity checks, geo-anomaly, and device trust score. Each rule returns a score (0-100). Rules are ordered by execution cost (cheapest first). If any rule returns SCORE > 90, the transaction is immediately rejected. ML model scores are computed asynchronously via a dedicated thread pool.
