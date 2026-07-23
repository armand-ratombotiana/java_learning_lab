# Mock Interview: Database Testing (Lab 16)

**Role:** Database Engineer / QA Engineer  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is database testing and what are the main types?

**Candidate:** Database testing verifies data integrity, performance, and security. Types:
1. **Unit testing:** Test stored procedures, functions, triggers in isolation
2. **Integration testing:** Test database interactions with application
3. **Performance testing:** SQL load testing, query timing
4. **Security testing:** Access controls, SQL injection, encryption
5. **Migration testing:** Verify data after schema changes

**Interviewer:** How do you test database code (PL/SQL, triggers, procedures)?

**Candidate:** Using **utPLSQL** (Oracle unit testing framework):
```sql
CREATE OR REPLACE PACKAGE ut_customer_tests AS
    -- %suite(Customer Management)
    -- %test(Calculate discount)
    PROCEDURE test_discount_calculation;
END;

CREATE OR REPLACE PACKAGE BODY ut_customer_tests AS
    PROCEDURE test_discount_calculation IS
        v_discount NUMBER;
    BEGIN
        -- Arrange
        INSERT INTO customers(id, total_purchases) VALUES(1, 15000);
        
        -- Act
        v_discount := calculate_discount(1);
        
        -- Assert
        ut.expect(v_discount).to_equal(15); -- 15% for > $10K
    END;
END;
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you performance test database queries and identify regressions?

**Candidate:** Use **SQL Test Suite** approach:
1. **Capture query execution plans** before and after schema changes
2. **Measure** elapsed time, buffer gets, disk reads, CPU time
3. **Compare** using AWR compare reports or SQL Tuning Set

```sql
-- Capture baseline
CREATE SQL TUNING SET baseline_ts;
ADD SQL TO baseline_ts FROM AWR BETWEEN SYSDATE-7 AND SYSDATE;

-- After change, compare
EXEC DBMS_SQLTUNE.CREATE_STGTAB_SQLSET;
EXEC DBMS_SQLTUNE.PACK_STGTAB_SQLSET('after_change_set', 'baseline_ts');
-- Load into repository and compare
```

**Interviewer:** How do you test database backup and recovery?

**Candidate:** Regular DR testing:
1. **Backup testing:** Restore backup to test environment, verify data integrity
2. **Recovery testing:** Simulate failures and test recovery procedures
3. **RPO/RTO validation:** Document actual time to recover vs SLA

Automated testing:
```bash
# Weekly backup test
rman target / <<EOF
STARTUP MOUNT;
RESTORE DATABASE VALIDATE;  # Verify backup can restore
EXIT;
EOF
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a comprehensive database testing strategy for a financial application migration (Oracle 19c upgrade).

**Candidate:** 

**Testing phases:**

**Phase 1 — Schema compatibility (1 week):**
```sql
-- Run all DDL scripts in test environment
-- Check: invalid objects, compilation errors
SELECT object_name, object_type FROM dba_objects WHERE status != 'VALID';

-- Compare schema between old and new version
SELECT * FROM (
    SELECT table_name, column_name, data_type FROM old_db.tabs
    MINUS
    SELECT table_name, column_name, data_type FROM new_db.tabs
);
```

**Phase 2 — Data integrity (1 week):**
```sql
-- Row count comparison
SELECT COUNT(*) FROM old_db.accounts;
SELECT COUNT(*) FROM new_db.accounts;

-- Checksum comparison (all columns, all rows)
SELECT ORA_HASH(column1 || column2 || ...), COUNT(*) FROM old_db.accounts;

-- Referential integrity check
SELECT constraint_name FROM all_constraints 
WHERE constraint_type = 'R' AND status != 'ENABLED';
```

**Phase 3 — Performance regression (2 weeks):**
- Compare AWR reports: Top SQL before/after
- Run SQL Tuning Set on new system
- Test peak load simulation

**Phase 4 — Application integration (2 weeks):**
- Test all CRUD operations
- Test batch processing (payroll, interest calculation)
- Verify report outputs match

**Phase 5 — Disaster recovery (1 week):**
- Test RMAN backup and restore on new version
- Test Data Guard failover to standby
- Document actual RPO/RTO

**Automation:** Use Jenkins pipeline running automated tests nightly, with JUnit XML reports for CI dashboards.

---

## Interviewer Feedback

**Strengths:** Comprehensive testing approach, practical PL/SQL testing, thorough migration testing strategy  
**Areas to Improve:** Could discuss SQL Injection testing tools  
**Verdict:** Strong Hire

---

*Databases Lab 16 MOCK_INTERVIEW.md*
