# Mental Models: Banking Platform

- **Ledger = Append-Only Log**: Every financial event is recorded immutably; balance is a derived view.
- **Service = Independent Business Unit**: Each microservice owns its data, logic, and lifecycle.
- **Transaction = Saga**: A payment is a choreographed dance across services with rollback steps.
- **Idempotency Key = Replay Protection**: Same request submitted twice produces same result once.
- **Account Balance = Materialized View**: Recomputable from the event stream at any point in time.
- **Fraud Model = Statistical Anomaly Detector**: Flag transactions that deviate from user's behavioral baseline.
