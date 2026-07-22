# Production Scenarios: PL/SQL Foundations (Oracle Focus)

## Scenario 1: PL/SQL Procedure Taking 8 Hours
**Context**: A nightly batch procedure processed 5M financial transactions to calculate account balances.
**Problem**: The procedure ran for 8 hours, exceeding the 4-hour batch window. The next day's processing was delayed.
**Root Cause**: The procedure used a cursor loop that processed one row at a time: `FOR rec IN (SELECT ... ) LOOP ... UPDATE ... END LOOP;`. Each iteration performed a separate UPDATE statement. 5M iterations = 5M round trips. The procedure was also not using bulk operations.
**Solution**: 1) Rewrote using `BULK COLLECT` with `LIMIT 1000` to fetch 1000 rows at a time. 2) Used `FORALL` to batch UPDATE statements: `FORALL i IN 1..batch.COUNT SAVE EXCEPTIONS UPDATE ...`. 3) Replaced row-by-row logic with a single `MERGE` statement where possible. 4) Reduced execution time from 8 hours to 25 minutes. 5) Added benchmarking: log execution time per batch for monitoring.
**Lessons Learned**: Never use row-by-row processing in PL/SQL batch procedures. Use BULK COLLECT + FORALL for bulk operations. Use SQL set operations (MERGE) instead of PL/SQL loops. Monitor batch procedure execution times.

## Scenario 2: Trigger Mutating Table Error
**Context**: A trigger on the EMPLOYEES table was supposed to update the DEPARTMENT summary when an employee's salary changed.
**Problem**: The trigger fired `ORA-04091: table DEPARTMENTS is mutating, trigger/function may not see it`. The salary update failed.
**Root Cause**: The trigger on EMPLOYEES tried to SELECT from DEPARTMENTS (the parent table) when EMPLOYEES was already being modified. Oracle prevents a trigger from reading the table that is currently being modified by the same transaction.
**Solution**: 1) Changed to a compound trigger (Oracle 11g+): used the `AFTER EACH ROW` section to capture affected department IDs into a collection. 2) Used the `AFTER STATEMENT` section to update departments using the collected IDs. 3) Or moved the logic to a stored procedure and called it explicitly instead of using a trigger. 4) Used a `DEFERRED` approach with `DBMS_APPLICATION_INFO` to defer the update. 5) Documented the mutating table pattern and solution.
**Lessons Learned**: Use compound triggers for multi-row operations that need to read/modify parent tables. Avoid complex business logic in triggers — use stored procedures. Document mutating table scenarios and approved solutions.

## Scenario 3: Package State Corrupted Between Sessions
**Context**: A PL/SQL package used package-level variables to cache user preferences for performance.
**Problem**: User A's preferences were incorrectly applied to User B's session. Session A and Session B were sharing cached values.
**Root Cause**: Package variables are session-specific in Oracle. However, the application used a connection pool with session-less mode. Each HTTP request could get a different database session. Package state from Session A's previous request was not available in the new session, so the code fell back to a default — which was the same default for all users.
**Solution**: 1) Changed to use `CONTEXT` (DBMS_SESSION.SET_CONTEXT) instead of package variables for user preferences. 2) Used Oracle's `APPLICATION_CONTEXT` with `CLIENT_CONTEXT` for cross-session user identification. 3) Or stored cached preferences in a global temporary table keyed by session ID. 4) Added `AUTHID CURRENT_USER` to the package for caller's rights. 5) Documented the session management approach for connection pool environments.
**Lessons Learned**: Package variables are per-session, not per-user or per-request. Use contexts or global temporary tables for cross-request state. Be explicit about session handling in connection-pooled environments. Test package state with connection pooling.

## Scenario 4: Dynamic SQL Injection via User Input
**Context**: A reporting function used `EXECUTE IMMEDIATE` with user-provided column names for dynamic filtering.
**Problem**: A user entered `salary FROM employees WHERE 1=1 UNION SELECT username, password FROM admin_users --` as a column name. The query returned sensitive data. Security breach.
**Root Cause**: The code concatenated user input directly into SQL: `EXECUTE IMMEDIATE 'SELECT ' || user_column || ' FROM employees WHERE id = :id'`. No input validation or sanitization was performed.
**Solution**: 1) Used `DBMS_ASSERT.SQL_OBJECT_NAME` to validate column names against `ALL_TAB_COLUMNS`. 2) Changed to use bind variables for all data values. 3) Used a whitelist approach: compared user input against list of allowed column names. 4) Added `AUTHID DEFINER` to limit privileges. 5) Implemented code review with automated SQL injection scanning.
**Lessons Learned**: Never concatenate user input into dynamic SQL. Use whitelist validation for object names. Use bind variables for all data values. Use `DBMS_ASSERT` for identifier validation. Implement least-privilege for PL/SQL packages.
