# Architecture: Query Optimization in Production

```
Development                    Staging                    Production
────────────                   ───────                    ──────────
                                                          
[Write query with       Slow query log monitoring
 EXPLAIN ANALYZE]       auto_explain (threshold=1s)
        │                       │
        ▼                       ▼
Local optimization      Plan regression detection
Add indexes             Compare plans with previous
Rewrite queries         Notify on plan changes
        │                       │
        ▼                       ▼
Code review with       [OPTIMIZER]
query plan check       Review + index changes
- Added/removed indexes via CI
- Query regressions flagged
```

## Repository Layer Query Strategy

```
Repository Method → Query Method
   ├─ Derived query (small, simple)
   ├─ @Query JPQL (moderate complexity)
   ├─ @Query native (optimization required)
   ├─ Specifications (dynamic, composable)
   └─ EntityGraph (fetch plan control)
```

## Database Index Management
- **Create indexes with migrations** (Flyway/Liquibase)
- **Monitor index usage**: `pg_stat_user_indexes`
- **Remove unused indexes**: periodic review
- **Rebuild fragmented indexes** (VACUUM, REINDEX)

## Performance Budget
| Query Type | Max Time | Max Rows |
|---|---|---|
| API lookup (by PK) | <10ms | 1 |
| List with filters | <100ms | <1000 |
| Admin report | <5s | <100000 |
| Batch job | <30min | unlimited |
