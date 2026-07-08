# Advanced Strangler Fig Pattern — Theory

## 1. Introduction
The Advanced Strangler Fig pattern extends the basic strangler pattern with sophisticated decomposition strategies, database splitting approaches, and asynchronous migration techniques for large-scale systems.

## 2. Decomposition Strategies

### 2.1 Domain-Driven Decomposition
Identify bounded contexts in the legacy system and extract them one at a time. Techniques include Event Storming workshops, aggregate identification through domain analysis, context mapping, and anti-corruption layers.

### 2.2 Feature-Based Decomposition
Extract features based on usage patterns, business value, or technical dependency. Consider business value, technical risk, dependency count, and data ownership clarity.

### 2.3 Organizational Decomposition (Conway's Law)
Align extraction boundaries with team structures. Each team owns the extraction of its domain.

## 3. Database Splitting Strategies

### 3.1 Shared Database to Separate Databases
Split the legacy database into multiple domain-specific databases. Phases include identifying data ownership, creating new schemas, implementing dual-write, backfilling historical data, verifying consistency, cutting over reads, and removing old references.

### 3.2 Database View Strategy
Create database views that expose the same schema as the legacy database but query the new data stores.

### 3.3 Database Trigger Strategy
Use database triggers and change data capture (CDC) to synchronize data between old and new databases during migration.

## 4. Async Migration Patterns

### 4.1 Bulk Synchronization
Large-scale data migration jobs running in batches. Suitable for historical data.

### 4.2 Change Data Capture (CDC)
Stream database changes in real-time to the new system. Tools like Debezium capture row-level changes.

### 4.3 Dual-Write with Verification
Write to both systems synchronously during migration. Verify consistency asynchronously.

## 5. Migration Coordination

### 5.1 Migration State Machine
Track each migrated component: NOT_STARTED, MIGRATION_IN_PROGRESS, DUAL_RUN, VERIFIED, LEGACY_DECOMMISSIONED.

### 5.2 Feature Flag Matrix
Manage which features use legacy vs new system across user segments, geographic regions, traffic percentages, and functional areas.

## 6. Testing Advanced Strategies
Chaos engineering during migration, production shadow testing with traffic replay, data consistency audits with automated reconciliation, performance baseline comparison before and after migration, and rollback drills with measurable success criteria.
