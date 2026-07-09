# Security Considerations for Advanced SQL

## SQL Injection in Dynamic SQL
```sql
-- VULNERABLE: string concatenation
EXECUTE IMMEDIATE 'SELECT * FROM ' || table_name || ' WHERE dept_id = ' || dept_id;

-- SAFE: bind variables
EXECUTE IMMEDIATE 'SELECT * FROM employees WHERE dept_id = :1' USING dept_id;

-- SAFE: DBMS_ASSERT for identifiers
EXECUTE IMMEDIATE 'SELECT * FROM ' || DBMS_ASSERT.SQL_OBJECT_NAME(table_name);
```

## SQL Injection in PIVOT
PIVOT columns in IN clause must be validated; use DBMS_ASSERT or whitelist:
```sql
-- Validate before using in PIVOT IN clause
FUNCTION safe_pivot_col(p_col VARCHAR2) RETURN VARCHAR2 IS
  whitelist CONSTANT SYS.KU$_VCNT := SYS.KU$_VCNT('IT_PROG', 'SA_MAN', 'FI_ACCOUNT');
BEGIN
  IF p_col MEMBER OF whitelist THEN RETURN p_col;
  ELSE RAISE_APPLICATION_ERROR(-20001, 'Invalid pivot column');
  END IF;
END;
```

## Row-Level Security and Virtual Private Database
```sql
-- VPD policy
BEGIN
  DBMS_RLS.ADD_POLICY(
    object_schema => 'HR',
    object_name => 'EMPLOYEES',
    policy_name => 'emp_access_policy',
    function_schema => 'HR',
    policy_function => 'emp_sec_function',
    statement_types => 'SELECT, UPDATE',
    sec_relevant_cols => 'salary',
    sec_relevant_cols_opt => DBMS_RLS.ALL_ROWS
  );
END;
```

## Redaction of Sensitive Data
```sql
BEGIN
  DBMS_REDACT.ADD_POLICY(
    object_schema => 'HR',
    object_name => 'EMPLOYEES',
    policy_name => 'redact_ssn',
    column_name => 'SSN',
    function_type => DBMS_REDACT.PARTIAL,
    regexp_pattern => '([0-9]{3})-([0-9]{2})-([0-9]{4})',
    regexp_replace_string => 'XXX-XX-\3',
    expression => 'dept_id <= 30'
  );
END;
/
```

## Data Encryption (TDE)
Oracle Transparent Data Encryption encrypts data at rest. Without TDE, data files are readable at OS level. Enable tablespace encryption:
```sql
CREATE TABLESPACE secure_ts ENCRYPTION USING 'AES256' DEFAULT STORAGE(ENCRYPT);
```

## Audit for Advanced SQL
```sql
-- Audit window function queries
AUDIT SELECT ON hr.employees BY ACCESS;
-- Audit specific SQL
SELECT * FROM DBA_AUDIT_TRAIL WHERE SQL_TEXT LIKE '%OVER%';
```

## Query Hints Security
Hints can expose optimization details or bypass query transformation. Avoid hints in application code; use SQL profiles instead. Hints are not visible to end users in packaged applications.

## Partitioning Security
Partitions mapped to different tablespaces can have different encryption keys. Use tablespace encryption per partition for sensitive time ranges.

## SQL Profile Security
SQL profiles can contain hints that change query behavior. Only trusted users (DBA role) should manage profiles. Audit profile changes:
```sql
SELECT * FROM DBA_SQL_PROFILES;
```

## MERGE Security
MERGE can touch many rows unintentionally. Always use error logging:
```sql
MERGE /*+ LOG ERRORS INTO merge_errors ('MERGE_FAILED') */ INTO target USING source ON (...);
```

## Row-Level Security Functions
```sql
CREATE OR REPLACE FUNCTION emp_sec_policy(obj_schema VARCHAR2, obj_name VARCHAR2)
RETURN VARCHAR2 AS
BEGIN
  IF USER = 'HR_MGR' THEN RETURN '1=1';
  ELSE RETURN 'dept_id = (SELECT dept_id FROM employees WHERE email = USER)';
  END IF;
END;
/
```

## PL/SQL Injection Prevention
```sql
CREATE OR REPLACE PROCEDURE safe_report(p_table VARCHAR2) AS
  v_table VARCHAR2(30) := DBMS_ASSERT.ENQUOTE_NAME(DBMS_ASSERT.SQL_OBJECT_NAME(p_table));
BEGIN
  EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM ' || v_table INTO v_cnt;
END;
/
```