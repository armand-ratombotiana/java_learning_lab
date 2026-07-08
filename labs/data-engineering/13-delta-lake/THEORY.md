# Delta Lake Theory

## Transaction Log Protocol
JSON files in _delta_log/ directory track every commit. Each commit records AddFile and RemoveFile actions. Readers read latest version and construct full file list. Checkpoint files (Parquet) periodically snapshot log state for faster reads.

## ACID Guarantees
Atomicity: all changes in commit succeed or fail. Consistency: schema and constraints enforced. Isolation: optimistic concurrency control detects conflicts. Durability: committed data persisted to storage. Multiple writers can write concurrently with conflict detection and automatic retry.

## Schema Enforcement & Evolution
By default, Delta enforces schema on write — mismatched columns cause errors. mergeSchema=true allows automatic schema evolution (adding columns, widening types). Schema tracking in transaction log enables audit trail of all schema changes.

## Lifecycle Management
OPTIMIZE: bin-packs small files into larger ones (target 256MB-1GB). ZORDER BY: colocates related data for file skipping. VACUUM: removes files not referenced by any version within retention period (default 7 days). Change Data Feed: enables streaming consumption of row-level changes.
