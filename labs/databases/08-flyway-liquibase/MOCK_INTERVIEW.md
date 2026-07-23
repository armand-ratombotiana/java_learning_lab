# Mock Interview: Flyway & Liquibase (Lab 08)

**Role:** Backend Engineer / Database DevOps Engineer  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are database migration tools and why are they important?

**Candidate:** Database migration tools version-control database schema changes alongside application code. Benefits:
- **Version control:** Every schema change is committed to source control
- **Repeatability:** Same migration runs in dev, test, staging, production
- **Collaboration:** Multiple devs can work on schema changes without conflicts
- **Traceability:** Each migration has a timestamp and author
- **Rollback:** Can undo migrations if needed

**Interviewer:** Compare Flyway and Liquibase.

**Candidate:**

| Aspect | Flyway | Liquibase |
|--------|--------|-----------|
| Migration format | SQL files | XML, YAML, JSON, SQL |
| Versioning | Numeric (V1__description.sql) | Sequential (changelog ordered) |
| Rollback | Limited (undo files) | Built-in (rollback tags) |
| Learning curve | Low (just SQL) | Medium (XML/DSL syntax) |
| Idempotency | Checksum validation | Context-dependent |
| Spring Boot | Auto-configuration with `flyway` dependency | Auto-configuration with `liquibase` dependency |

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you design a migration strategy for a multi-environment (dev, staging, prod) deployment?

**Candidate:** 

**Strategy:**
1. **Migration files:** `V1__create_users.sql`, `V2__add_email_verified.sql`, etc.
2. **Environment-specific migrations:**
   - All envs run common migrations sequentially
   - Dev runs `V__dev_seed_data.sql`
   - Staging runs `V__staging_seed_data.sql`
   - Prod runs no seed data (data loaded separately)
3. **Repeatable migrations:** `R__views.sql`, `R__functions.sql` — re-run if checksum changes
4. **CI/CD integration:**
   - Build phase: Validates migration checksums against committed files
   - Deploy to staging: Runs migrations against staging database
   - Deploy to prod: Runs migrations with `flyway.migrate()`

**Best practices:**
- Never modify a migration that has been committed (creates checksum mismatch)
- Back-fill data changes in separate migrations
- Use `@Transactional` annotation to run migrations within a transaction (where supported)
- Test migrations on a clone of production before deploying

**Interviewer:** How do you handle a failed migration in production?

**Candidate:** Failure handling:
1. **Check_error:** Flyway creates a `flyway_schema_history` table tracking each migration
2. **Failed state:** The failed migration is marked with "FAILED" state
3. **Fix options:**
   a. Fix the migration SQL, run `flyway repair` to remove failure record, re-run
   b. Execute manual SQL to undo partial changes, `flyway repair`, re-run
   c. For Liquibase, use `rollback` command to revert changelog tag

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a zero-downtime database migration strategy for a critical production system. Include backward-compatible schema changes, data migration, and rollback plans.

**Candidate:** 

**Expand-Contract pattern (zero-downtime):**

**Phase 1 — Expand (Deploy 1):**
```sql
-- Add new column as nullable
ALTER TABLE orders ADD COLUMN customer_new_id BIGINT NULL;

-- Create index on new column
CREATE INDEX idx_orders_customer_new ON orders(customer_new_id);

-- Application writes to BOTH old and new columns
-- Application reads from old column
```

**Phase 2 — Migrate (Background job):**
```sql
-- Backfill data for existing records
UPDATE orders SET customer_new_id = customer_id WHERE customer_new_id IS NULL;
-- Run in batches to avoid long locks
```

**Phase 3 — Switch (Deploy 2):**
```sql
-- Application now reads from new column, writes to both
-- Verify no issues

-- Make new column NOT NULL
ALTER TABLE orders MODIFY customer_new_id BIGINT NOT NULL;
```

**Phase 4 — Contract (Deploy 3):**
```sql
-- Remove old column
ALTER TABLE orders DROP COLUMN customer_id;
-- Application now only uses new column
```

**Rollback at each phase:**
- Phase 1: Revert to previous application version (old column still works)
- Phase 2: Stop migration job, application still works on old column
- Phase 3: Revert application, old column still has data
- Phase 4: Cannot easily rollback — must restore from backup

---

## Interviewer Feedback

**Strengths:** Good migration tool comparison, practical multi-env strategy, excellent zero-downtime design  
**Areas to Improve:** Could discuss Liquibase diffChangelog for schema comparison  
**Verdict:** Strong Hire

---

*Databases Lab 08 MOCK_INTERVIEW.md*
