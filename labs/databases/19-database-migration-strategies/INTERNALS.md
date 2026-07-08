# Internals: Database Migration Strategies

## Internal Architecture

### Expand-Migrate-Contract Pattern
A three-phase strategy for zero-downtime schema changes.

**Phase 1: EXPAND**
- Add new schema elements alongside existing
- Enable dual-write mode (write to both old and new)
- No breaking changes; backward compatible

**Phase 2: MIGRATE**
- Backfill historical data from old to new schema
- Verify consistency between old and new
- Run validation checks

**Phase 3: CONTRACT**
- Cut over reads from old to new
- Remove dual-write logic
- Drop old schema elements
- Final cleanup

### Zero-Downtime Migration State Machine
```
PLANNING -> CANARY_DEPLOY -> CANARY_VERIFY -> MAIN_DEPLOY -> VERIFY -> COMPLETE
              |                                    |
              v                                    v
           ROLLBACK                            ROLLBACK
```

### Canary Deployment Strategy
1. Deploy migration to a small subset of servers/customers
2. Monitor for errors, latency changes, data inconsistencies
3. If canary passes: roll out to full fleet
4. If canary fails: rollback immediately

### Migration Safety Checks
- **Pre-migration:** Schema compatibility, data size estimation, backup
- **During migration:** Progress monitoring, error rate tracking
- **Post-migration:** Data verification, performance comparison, rollback readiness

### Rollback Strategies
- **Forward-only:** No rollback, fix forward (for additive changes)
- **Automated rollback:** Scripted reverse migration
- **Point-in-time recovery:** Restore from backup

### Blue-Green Database Deployment
- Blue: Current production database
- Green: New database with migration applied
- Cutover: Switch traffic from blue to green
- Rollback: Switch back to blue

### Monitoring
- Migration progress (% complete)
- Error rate during migration
- Data consistency checksums
- Query latency before/during/after
- Rollback readiness status
