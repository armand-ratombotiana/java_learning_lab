# LEETCODE_SOLUTIONS — Database Security

## Security-Centric SQL Solutions

| Security Concept | LeetCode Application | Implementation |
|-----------------|---------------------|----------------|
| SQL Injection | Dynamic queries in WHERE | Always use bind variables |
| Row-Level Security | VPD for multi-tenant | Application context in WHERE |
| Data Masking | Show partial data | `SUBSTR(ssn, -4)` |
| Audit Logging | Track data changes | Trigger to audit table |
| Access Control | GRANT/REVOKE | Role-based SELECT access |

### Secure LeetCode Query Patterns
```sql
-- ❌ Insecure: String concatenation
' WHERE name = ''' || user_input || '''';

-- ✅ Secure: Bind variables
SELECT * FROM Employee WHERE name = :name;

-- ✅ Secure: Row-level filter for multi-tenant
SELECT * FROM Employee WHERE tenant_id = SYS_CONTEXT('TENANT', 'id');

-- ✅ Secure: Masked output
SELECT SUBSTR(email, 1, 3) || '***@***' AS masked_email FROM Users;
```

### SQL Injection Prevention for LeetCode-Style Queries
| Risk | LeetCode Problem | Prevention |
|------|-----------------|------------|
| Dynamic table names | Generic query builder | Validate against allowlist |
| Dynamic column names | Sort/filter UI | Use column allowlist |
| User input in WHERE | Search functionality | Bind variables |
| Multiple rows in ORDER | Custom sorting | Parameterized ORDER BY |
